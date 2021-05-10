package com.github.vadim01er.json;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;


@Data
@EqualsAndHashCode(callSuper = true)
public class ObjectJsonResponse extends JsonResponse {
    private Object object;

    public ObjectJsonResponse(Object object) {
        super(HttpStatus.OK.value());
        this.object = object;
    }
}
