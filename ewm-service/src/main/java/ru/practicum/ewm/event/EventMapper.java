package ru.practicum.ewm.event;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserShortDto;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventMapper {

    public static Event mapToEvent(NewEventRequest newEventRequest, Category category, User initiator, EventState state) {
        Event event = new Event();

        event.setAnnotation(newEventRequest.getAnnotation());
        event.setDescription(newEventRequest.getDescription());
        event.setTitle(newEventRequest.getTitle());
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setLocation(newEventRequest.getLocation());
        event.setState(state);
        event.setParticipantLimit(newEventRequest.getParticipantLimit());
        event.setPaid(newEventRequest.getPaid());
        event.setRequestModeration(newEventRequest.getRequestModeration());
        event.setEventDate(newEventRequest.getEventDate());

        return event;
    }

    public static EventDto mapToEventDto(Event event, CategoryDto categoryDto, UserShortDto initiator) {
        EventDto eventDto = new EventDto();

        eventDto.setId(event.getId());
        eventDto.setAnnotation(event.getAnnotation());
        eventDto.setDescription(event.getDescription());
        eventDto.setTitle(event.getTitle());
        eventDto.setCategory(categoryDto);
        eventDto.setInitiator(initiator);
        eventDto.setLocation(event.getLocation());
        eventDto.setState(event.getState());
        eventDto.setParticipantLimit(event.getParticipantLimit());
        eventDto.setConfirmedRequests(event.getConfirmedRequests());
        eventDto.setViews(event.getViews());
        eventDto.setPaid(event.getPaid());
        eventDto.setRequestModeration(event.getRequestModeration());
        eventDto.setEventDate(event.getEventDate());
        eventDto.setPublishedOn(event.getPublishedOn());
        eventDto.setCreatedOn(event.getCreatedOn());

        return eventDto;
    }

    public static EventShortDto mapToShortDto(Event event, CategoryDto categoryDto, UserShortDto initiator) {
        EventShortDto eventShortDto = new EventShortDto();

        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setCategory(categoryDto);
        eventShortDto.setInitiator(initiator);
        eventShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventShortDto.setViews(event.getViews());
        eventShortDto.setPaid(event.getPaid());
        eventShortDto.setEventDate(event.getEventDate());

        return eventShortDto;
    }

    public static Event updateEventFields(Event event, UpdateEventAdminRequest request, Category category, EventState eventState, LocalDateTime publishedOn) {
        if (request.hasAnnotation()) event.setAnnotation(request.getAnnotation());
        if (request.hasCategory() && category != null) event.setCategory(category);
        if (request.hasDescription()) event.setDescription(request.getDescription());
        if (request.hasTitle()) event.setTitle(request.getTitle());
        if (request.hasEventDate())  event.setEventDate(request.getEventDate());
        if (publishedOn != null) event.setPublishedOn(publishedOn);
        if (eventState != null) event.setState(eventState);
        if (request.hasLocation()) event.setLocation(request.getLocation());
        if (request.hasPaid())  event.setPaid(request.getPaid());
        if (request.hasParticipantLimit()) event.setParticipantLimit(request.getParticipantLimit());
        if (request.hasRequestModeration()) event.setRequestModeration(request.getRequestModeration());
        return event;
    }
}
