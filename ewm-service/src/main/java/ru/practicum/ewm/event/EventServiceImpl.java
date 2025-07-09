package ru.practicum.ewm.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserService;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventStorage eventStorage;
    private final UserService userService;
    private final CategoryService categoryService;

    @Transactional
    @Override
    public EventDto save(Long userId, NewEventRequest newEventRequest) {
        User initiator = userService.checkUser(userId);
        Category category = categoryService.checkCategory(newEventRequest.getCategory());
        checkEventDate(newEventRequest.getEventDate());
        Event event = EventMapper.mapToEvent(newEventRequest, category, initiator, EventState.PENDING);
        event = eventStorage.save(event);
        log.info("Event {} is registered with the ID: {} by user: {}", event.getTitle(), event.getId(), initiator);
        return EventMapper.mapToEventDto(
                event,
                CategoryMapper.mapToCategoryDto(category),
                UserMapper.mapToShortDto(initiator));
    }

    @Override
    public Collection<EventShortDto> findAllByInitiatorId(Long initiatorId, Pageable pageable) {
        User initiator = userService.checkUser(initiatorId);
        return eventStorage.findByInitiatorId(initiatorId, pageable)
                .stream()
                .map(event -> EventMapper.mapToShortDto(
                        event,
                        CategoryMapper.mapToCategoryDto(event.getCategory()),
                        UserMapper.mapToShortDto(initiator)))
                .toList();
    }

    @Override
    public EventDto findByIdAndInitiatorId(Long initiatorId, Long eventId) {
        User initiator = userService.checkUser(initiatorId);
        Event event = checkEventByInitiatorId(eventId, initiatorId);
        return EventMapper.mapToEventDto(
                event,
                CategoryMapper.mapToCategoryDto(event.getCategory()),
                UserMapper.mapToShortDto(initiator));
    }

    @Override
    public EventDto findPublishedById(Long eventId) {
        Event event = checkPublishedEventById(eventId);
        return EventMapper.mapToEventDto(
                event,
                CategoryMapper.mapToCategoryDto(event.getCategory()),
                UserMapper.mapToShortDto(event.getInitiator()));
    }

    @Override
    public Collection<EventShortDto> findPublishedByParams(String text, Collection<Long> categories, Boolean paid,
                                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                           Boolean onlyAvailable, Pageable pageable) {
        checkDateTimes(rangeStart, rangeEnd);
        return eventStorage.findPublishedByParams(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, pageable)
                .stream()
                .map(event -> EventMapper.mapToShortDto(
                        event,
                        CategoryMapper.mapToCategoryDto(event.getCategory()),
                        UserMapper.mapToShortDto(event.getInitiator())))
                .toList();
    }

    @Override
    public Collection<EventDto> findByParams(Collection<Long> users, Collection<EventState> states,
                                             Collection<Long> categories, LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd, Pageable pageable) {
        checkDateTimes(rangeStart, rangeEnd);
        return eventStorage.findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(users, states, categories, rangeStart, rangeEnd, pageable)
                .stream()
                .map(event -> EventMapper.mapToEventDto(
                        event,
                        CategoryMapper.mapToCategoryDto(event.getCategory()),
                        UserMapper.mapToShortDto(event.getInitiator())))
                .toList();
    }

    @Transactional
    @Override
    public EventDto updateAdmin(Long eventId, UpdateEventRequest request) {
        Event eventToUpdate = checkEvent(eventId);
        LocalDateTime publishedOn = eventToUpdate.getPublishedOn();
        Category category = null;
        EventState eventState = null;
        if (request.getStateAction() != null) {
            publishedOn = processStateAction(eventToUpdate.getState(), request.getStateAction());
            if (publishedOn != null) {
                eventState = EventState.PUBLISHED;
            }
        }
        if (request.getEventDate() != null && publishedOn != null) {
            checkEventDateBeforePublishing(request.getEventDate(), publishedOn);
        }
        if (request.getCategory() != null) {
            category = categoryService.checkCategory(request.getCategory());
        }
        Event updatedEvent = EventMapper.updateEventFields(eventToUpdate, request, category, eventState, publishedOn);
        updatedEvent = eventStorage.save(updatedEvent);
        log.info("Event has been updated with ID:{}", eventId);
        return EventMapper.mapToEventDto(
                updatedEvent,
                CategoryMapper.mapToCategoryDto(updatedEvent.getCategory()),
                UserMapper.mapToShortDto(updatedEvent.getInitiator()));
    }

    @Transactional
    @Override
    public EventDto updatePrivate(Long userId, Long eventId, UpdateEventRequest request) {
        userService.checkUser(userId);
        Event eventToUpdate = checkEvent(eventId);
        checkEventState(eventToUpdate.getState());
        Category category = null;
        EventState eventState = null;
        if (request.getEventDate() != null) {
            checkEventDate(request.getEventDate());
        }
        if (request.getCategory() != null) {
            category = categoryService.checkCategory(request.getCategory());
        }
        if (StateAction.CANCEL_REVIEW.equals(request.getStateAction())) eventState = EventState.CANCELED;
        Event updatedEvent = EventMapper.updateEventFields(eventToUpdate, request, category, eventState, null);
        updatedEvent = eventStorage.save(updatedEvent);
        log.info("Event has been updated with ID:{} by userId:{}", eventId, userId);
        return EventMapper.mapToEventDto(
                updatedEvent,
                CategoryMapper.mapToCategoryDto(updatedEvent.getCategory()),
                UserMapper.mapToShortDto(updatedEvent.getInitiator()));
    }


    private LocalDateTime processStateAction(EventState eventState, StateAction stateAction) {
        if (stateAction.equals(StateAction.PUBLISH_EVENT)) {
            if (!eventState.equals(EventState.PENDING)) {
                log.error("An event can only be published if it is in the waiting state for publication. eventState={}, stateAction={}",
                        eventState, stateAction);
                throw new ConflictStateActionException("An event can only be published if it is in the waiting state for publication. eventState="
                        + eventState + ", stateAction=" + stateAction);
            }
            return LocalDateTime.now();
        } else if (stateAction.equals(StateAction.REJECT_EVENT)) {
            if (eventState.equals(EventState.PUBLISHED)) {
                log.error("An event can only be rejected if it has not been published yet. eventState={}, stateAction={}",
                        eventState, stateAction);
                throw new ConflictStateActionException("An event can only be rejected if it has not been published yet. eventState="
                        + eventState + ", stateAction=" + stateAction);
            }
        }
        return null;
    }

    private void checkEventDateBeforePublishing(LocalDateTime eventDate, LocalDateTime publishedOn) {
        if (eventDate.isBefore(publishedOn.plusHours(1))) {
            log.error("The start date of the event to be modified must be no earlier than 1 hour from the publication date. " +
                    "Specified eventDate:{}, publishedOn:{}", eventDate, publishedOn);
            throw new ConflictEventDateException("The start date of the event to be modified must be no earlier than 1 hour from the publication date. " +
                    "Specified eventDate:" + eventDate + ", publishedOn:" + publishedOn);
        }
    }

    @Override
    public Event checkEvent(Long eventId) {
        return eventStorage.findById(eventId).orElseThrow(() -> {
            log.error("Event with ID:{} was not found", eventId);
            return new NotFoundException("Event with ID:" + eventId + " was not found");
        });
    }

    @Override
    public List<Event> checkEventInIdList(List<Long> eventIds) {
        List<Event> events = eventStorage.findAllById(eventIds);
        Set<Long> foundedIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toSet());

        List<Long> notFoundedIds = eventIds.stream()
                .filter(id -> !foundedIds.contains(id))
                .toList();

        if (!notFoundedIds.isEmpty()) {
            log.error("No events with ID found: {}", notFoundedIds);
            throw new NotFoundException("No events with ID found: " + notFoundedIds);
        }
        return events;
    }

    @Transactional
    @Override
    public void incrementConfirmedRequests(Long eventId) {
        log.info("Incrementing confirmed requests for eventId:{}", eventId);
        eventStorage.incrementConfirmedRequests(eventId);
    }

    @Transactional
    @Override
    public void decrementConfirmedRequests(Long eventId) {
        log.info("Decrementing confirmed requests for eventId:{}", eventId);
        eventStorage.decrementConfirmedRequests(eventId);
    }

    @Transactional
    @Override
    public void addConfirmedRequests(Long eventId, Long confirmedRequests) {
        log.info("Adding {} confirmed requests for eventId:{}", confirmedRequests, eventId);
        eventStorage.addConfirmedRequests(eventId, confirmedRequests);
    }

    private Event checkEventByInitiatorId(Long eventId, Long initiatorId) {
        return eventStorage.findByIdAndInitiatorId(eventId, initiatorId).orElseThrow(() -> {
            log.error("Event with ID:{} was not found by initiatorID:{}", eventId, initiatorId);
            return new NotFoundException("Event with ID:" + eventId + " was not found by initiatorID:" + initiatorId);
        });
    }

    private Event checkPublishedEventById(Long eventId) {
        return eventStorage.findByIdAndState(eventId, EventState.PUBLISHED).orElseThrow(() -> {
            log.error("Published event with ID:{} was not found", eventId);
            return new NotFoundException("Published event with ID:" + eventId + " was not found");
        });
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            log.error("The event date cannot be earlier than 2 hours from the current moment. Requested event date={}", eventDate);
            throw new ValidationException("The event date cannot be earlier than 2 hours from the current moment. Requested event date=" + eventDate);
        }
    }

    private void checkDateTimes(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            log.error("start={} can't be after end={}", start, end);
            throw new ValidationException("start=" + start + " can't be after end=" + end);
        }
    }

    private void checkEventState(EventState state) {
        if (!(state.equals(EventState.CANCELED) || state.equals(EventState.PENDING))) {
            log.error("You can only change cancelled events or events that are awaiting moderation. Event state:{}", state);
            throw new ConflictEventStateException("You can only change cancelled events or events that are awaiting moderation. Event state:" + state);
        }
    }
}
