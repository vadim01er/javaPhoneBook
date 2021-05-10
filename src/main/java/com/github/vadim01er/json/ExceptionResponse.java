package com.github.vadim01er.json;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ExceptionResponse extends JsonResponse {

    private final String message;

    public ExceptionResponse(HttpStatus code) {
        super(code.value());
        this.message = code.name();
    }

    public ExceptionResponse(HttpStatus code, String message) {
        super(code.value());
        this.message = code.name() + ": " + message;
    }
}
