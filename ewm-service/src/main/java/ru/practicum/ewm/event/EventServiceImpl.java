package ru.practicum.ewm.event;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.NewEndpointHitRequest;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.exception.*;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserService;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final EventStorage eventStorage;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatsClient statsClient;

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

    @Transactional
    @Override
    public EventDto findPublishedById(HttpServletRequest request, Long eventId) {
        Event event = checkPublishedEventById(eventId);
        logHit(request);
        Long viewsFromStats = getUniqueEventViewsFromStatsServer(request, event);
        if (viewsFromStats - event.getViews() == 1) incrementViews(eventId);
        return EventMapper.mapToEventDto(
                event,
                CategoryMapper.mapToCategoryDto(event.getCategory()),
                UserMapper.mapToShortDto(event.getInitiator()));
    }

    @Override
    public Collection<EventShortDto> findPublishedByParams(HttpServletRequest request,
                                                           String text, Collection<Long> categories, Boolean paid,
                                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                           Boolean onlyAvailable, Pageable pageable) {

        if (rangeEnd != null) checkDateTimes(rangeStart, rangeEnd);
        logHit(request);
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

        if (rangeEnd != null) checkDateTimes(rangeStart, rangeEnd);
        return eventStorage.findByParams(users, states, categories, rangeStart, rangeEnd, pageable)
                .stream()
                .map(event -> EventMapper.mapToEventDto(
                        event,
                        CategoryMapper.mapToCategoryDto(event.getCategory()),
                        UserMapper.mapToShortDto(event.getInitiator())))
                .toList();
    }

    @Override
    public Optional<Event> findFirstByCategoryId(Long catId) {
        return eventStorage.findFirstByCategoryId(catId);
    }

    @Transactional
    @Override
    public EventDto updateAdmin(Long eventId, UpdateEventRequest request) {
        Event eventToUpdate = checkEvent(eventId);
        LocalDateTime publishedOn = eventToUpdate.getPublishedOn();
        Category category = null;
        EventState newEventState = null;
        if (request.getStateAction() != null) {
            newEventState = processStateAction(eventToUpdate.getState(), request.getStateAction());
            if (newEventState != null) {
                if (newEventState == EventState.PUBLISHED) publishedOn = LocalDateTime.now();
                if (newEventState == EventState.CANCELED) publishedOn = null;
            }
        }
        if (request.getEventDate() != null && publishedOn != null) {
            checkEventDateBeforePublishing(request.getEventDate(), publishedOn);
        }
        if (request.getCategory() != null) {
            category = categoryService.checkCategory(request.getCategory());
        }
        Event updatedEvent = EventMapper.updateEventFields(eventToUpdate, request, category, newEventState, publishedOn);
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
        if (StateAction.SEND_TO_REVIEW.equals(request.getStateAction())) eventState = EventState.PENDING;
        Event updatedEvent = EventMapper.updateEventFields(eventToUpdate, request, category, eventState, null);
        updatedEvent = eventStorage.save(updatedEvent);
        log.info("Event has been updated with ID:{} by userId:{}", eventId, userId);
        return EventMapper.mapToEventDto(
                updatedEvent,
                CategoryMapper.mapToCategoryDto(updatedEvent.getCategory()),
                UserMapper.mapToShortDto(updatedEvent.getInitiator()));
    }


    private EventState processStateAction(EventState eventState, StateAction stateAction) {
        if (stateAction.equals(StateAction.PUBLISH_EVENT)) {
            if (!eventState.equals(EventState.PENDING)) {
                log.error("An event can only be published if it is in the waiting state for publication. eventState={}, stateAction={}",
                        eventState, stateAction);
                throw new ConflictStateActionException("An event can only be published if it is in the waiting state for publication. eventState="
                        + eventState + ", stateAction=" + stateAction);
            }
            return EventState.PUBLISHED;
        } else if (stateAction.equals(StateAction.REJECT_EVENT)) {
            if (eventState.equals(EventState.PUBLISHED)) {
                log.error("An event can only be rejected if it has not been published yet. eventState={}, stateAction={}",
                        eventState, stateAction);
                throw new ConflictStateActionException("An event can only be rejected if it has not been published yet. eventState="
                        + eventState + ", stateAction=" + stateAction);
            }
            return EventState.CANCELED;
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

    private void incrementViews(Long eventId) {
        log.info("Incrementing views for eventId:{}", eventId);
        eventStorage.incrementViews(eventId);
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

    private void logHit(HttpServletRequest request) {
        statsClient.saveHit(new NewEndpointHitRequest(
                "ewm-main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now().format(FORMATTER)
        ));
    }

    private Long getUniqueEventViewsFromStatsServer(HttpServletRequest request, Event event) {
        List<String> uris = List.of(request.getRequestURI());
        List<ViewStatsDto> stats = statsClient.getStats(
                event.getCreatedOn(),
                LocalDateTime.now(),
                uris, true);
        return stats.isEmpty() ? 0 : stats.getFirst().getHits();
    }
}
