package com.github.vadim01er.json;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class ListJsonResponse extends JsonResponse {

    private final List<?> response;

    public ListJsonResponse(List<?> response) {
        super(HttpStatus.OK.value());
        this.response = response;
    }
}
