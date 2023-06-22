package ru.practicum.shareit.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

    @JsonProperty("error")
    private final String msg;

    public ErrorResponse(String msg) {
        this.msg = msg;
    }
}
