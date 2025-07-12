package ru.practicum.ewm.event.comment;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.user.User;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();

        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setEventId(comment.getEvent().getId());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }

    public static Comment mapToComment(NewCommentRequest request, User author, Event event) {
        Comment comment = new Comment();

        comment.setText(request.getText());
        comment.setAuthor(author);
        comment.setEvent(event);

        return comment;
    }
}
