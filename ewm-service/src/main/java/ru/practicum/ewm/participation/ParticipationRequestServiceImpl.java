package ru.practicum.ewm.participation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.ConflictInitiatorException;
import ru.practicum.ewm.exception.ConflictParticipationRequestException;
import ru.practicum.ewm.exception.ConflictParticipationRequestLimitException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestStorage participationRequestStorage;
    private final UserService userService;
    private final EventService eventService;

    @Transactional
    @Override
    public ParticipationRequestDto save(Long userId, Long eventId) {
        User requester = userService.checkUser(userId);
        Event event = eventService.checkEvent(eventId);
        checkParticipationRequestByUserIdAndEventId(userId, eventId);
        checkEventInitiatorForParticipate(requester, event);
        checkEventPublishedState(event);
        checkEventParticipationLimit(event);

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(requester);
        participationRequest.setEvent(event);

        ParticipationRequestStatus status = event.getRequestModeration()
                ? ParticipationRequestStatus.PENDING
                : ParticipationRequestStatus.CONFIRMED;

        participationRequest.setStatus(status);

        participationRequest = participationRequestStorage.save(participationRequest);
        if (status.equals(ParticipationRequestStatus.CONFIRMED)) {
            eventService.incrementConfirmedRequests(eventId);
        }
        log.info("Participation request is registered with the ID:{} by requesterId:{} for eventId:{}",
                participationRequest.getId(), userId, eventId);

        return ParticipationRequestMapper.mapToDto(participationRequest);
    }

    @Override
    public Collection<ParticipationRequestDto> findByUserId(Long userId) {
        userService.checkUser(userId);
        return participationRequestStorage.findByRequesterId(userId)
                .stream()
                .map(ParticipationRequestMapper::mapToDto)
                .toList();
    }

    @Override
    public Collection<ParticipationRequestDto> findPartRequestsByUserId(Long userId, Long eventId) {
        User initiator = userService.checkUser(userId);
        Event event = eventService.checkEvent(eventId);
        checkEventInitiator(initiator, event);
        return participationRequestStorage.findAllByEventId(eventId)
                .stream()
                .map(ParticipationRequestMapper::mapToDto)
                .toList();
    }

    @Transactional
    @Override
    public ParticipationRequestStatusUpdateResult updatePartRequestsByInitiatorId(Long userId, Long eventId,
                                                                                  ParticipationRequestStatusUpdateRequest request) {
        User initiator = userService.checkUser(userId);
        Event event = eventService.checkEvent(eventId);
        checkEventInitiator(initiator, event);

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) return null;

        List<ParticipationRequest> requestsToUpdate = participationRequestStorage.findAllById(request.getRequestIds());

        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();

        Long alreadyConfirmed = event.getConfirmedRequests();
        Integer limit = event.getParticipantLimit();

        for (ParticipationRequest partRequest : requestsToUpdate) {
            checkPendingStatus(partRequest.getStatus());
            if (request.getStatus() == ParticipationRequestStatus.CONFIRMED) {
                if (alreadyConfirmed < limit) {
                    partRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
                    confirmed.add(partRequest);
                    alreadyConfirmed++;
                } else {
                    partRequest.setStatus(ParticipationRequestStatus.REJECTED);
                    rejected.add(partRequest);
                }
            } else if (request.getStatus() == ParticipationRequestStatus.REJECTED) {
                partRequest.setStatus(ParticipationRequestStatus.REJECTED);
                rejected.add(partRequest);
            }
        }

        participationRequestStorage.saveAll(requestsToUpdate);

        if (!confirmed.isEmpty()) {
            eventService.addConfirmedRequests(eventId, (long) confirmed.size());
        }

        List<ParticipationRequestDto> confirmedDto = confirmed.stream().map(ParticipationRequestMapper::mapToDto).toList();
        List<ParticipationRequestDto> rejectedDto = rejected.stream().map(ParticipationRequestMapper::mapToDto).toList();

        return new ParticipationRequestStatusUpdateResult(confirmedDto, rejectedDto);
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userService.checkUser(userId);
        ParticipationRequest participationRequest = checkParticipationRequest(requestId);
        if (participationRequest.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
            eventService.decrementConfirmedRequests(participationRequest.getEvent().getId());
        }
        participationRequest.setStatus(ParticipationRequestStatus.CANCELED);
        participationRequest = participationRequestStorage.save(participationRequest);
        log.info("Participation request has been canceled with the ID:{} by requesterId:{}",
                participationRequest.getId(), userId);
        return ParticipationRequestMapper.mapToDto(participationRequest);
    }

    private void checkPendingStatus(ParticipationRequestStatus status) {
        if (!ParticipationRequestStatus.PENDING.equals(status)) {
            log.error("The status can only be changed for applications that are in a pending status. Current status:{}", status);
            throw new ConflictParticipationRequestException("The status can only be changed for applications that are in a pending status. Current status:" + status);
        }
    }

    private ParticipationRequest checkParticipationRequest(Long requestId) {
        return participationRequestStorage.findById(requestId).orElseThrow(() -> {
            log.error("The participation request was not found with the ID: {}", requestId);
            return new NotFoundException("The participation request was not found with the ID: " + requestId);
        });
    }

    private void checkParticipationRequestByUserIdAndEventId(Long userId, Long eventId) {
        if (participationRequestStorage.findByRequesterIdAndEventId(userId, eventId).isPresent()) {
            log.error("Participation request for userId:{} and eventId:{} already exists", userId, eventId);
            throw new ConflictParticipationRequestException("Participation request for userId:" + userId + " and eventId:" + eventId + " already exists");
        }
    }

    private void checkEventInitiatorForParticipate(User requester, Event event) {
        if (requester.getId().equals(event.getInitiator().getId())) {
            log.error("The initiator of the event cannot add a request to participate in his event. requesterId:{}, initiatorId:{}",
                    requester.getId(), event.getInitiator().getId());
            throw new ConflictParticipationRequestException("The initiator of the event cannot add a request to participate in his event. requesterId:"
                    + requester.getId() + ", initiatorId:" + event.getInitiator().getId());
        }
    }

    private void checkEventPublishedState(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.error("You cannot participate in an unpublished event. EventState:{}", event.getState());
            throw new ConflictParticipationRequestException("You cannot participate in an unpublished event. EventState:" + event.getState());
        }
    }

    private void checkEventParticipationLimit(Event event) {
        if (!(event.getConfirmedRequests() < event.getParticipantLimit())) {
            log.error("The event has reached the limit of participation requests. Limit:{}", event.getParticipantLimit());
            throw new ConflictParticipationRequestLimitException("The event has reached the limit of participation requests. Limit:" + event.getParticipantLimit());
        }
    }

    private void checkEventParticipationLimit(Long currConfirmed, Event event) {
        if (!(currConfirmed < event.getParticipantLimit())) {
            log.error("The event has reached the limit of participation requests. Limit:{}", event.getParticipantLimit());
            throw new ConflictParticipationRequestException("The event has reached the limit of participation requests. Limit:" + event.getParticipantLimit());
        }
    }

    private void checkEventInitiator(User initiator, Event event) {
        if (!initiator.getId().equals(event.getInitiator().getId())) {
            log.error("UserId:{} is not the initiator of the eventId:{}", initiator.getId(), event.getId());
            throw new ConflictInitiatorException("UserId:" + initiator.getId() + " is not the initiator of the eventId:" + event.getId());
        }
    }
}
