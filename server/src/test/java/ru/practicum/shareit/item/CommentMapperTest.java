package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentMapperTest {
    private Comment comment;
    private CommentDto commentDto;
    private User author;

    @BeforeEach
    void setup() {
        author = User.builder()
                .id(1L)
                .name("author")
                .email("email@ya.ru")
                .build();
        commentDto = CommentDto.builder()
                .id(1L)
                .authorName("author")
                .text("text")
                .build();
        comment = Comment.builder()
                .id(1L)
                .author(author)
                .text("text")
                .build();
    }

    @Test
    void toComment() {

        Comment mappedCommentDto = CommentMapper.toComment(commentDto);

        assertEquals(comment.getId(), mappedCommentDto.getId());
        assertEquals(comment.getText(), mappedCommentDto.getText());

    }

    @Test
    void toCommentDto() {

        CommentDto mappedComment = CommentMapper.toCommentDto(comment);

        assertEquals(commentDto.getId(), mappedComment.getId());
        assertEquals(commentDto.getText(), mappedComment.getText());
        assertEquals(commentDto.getAuthorName(), mappedComment.getAuthorName());

    }
}