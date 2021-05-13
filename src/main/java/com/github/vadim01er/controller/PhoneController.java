package com.github.vadim01er.controller;

import com.github.vadim01er.entity.Phone;
import com.github.vadim01er.entity.PhoneDTO;
import com.github.vadim01er.json.ExceptionResponse;
import com.github.vadim01er.json.JsonResponse;
import com.github.vadim01er.json.ListJsonResponse;
import com.github.vadim01er.json.ObjectJsonResponse;
import com.github.vadim01er.service.PhoneService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;

@Validated
@RestController
@RequestMapping("/phones")
@AllArgsConstructor
public class PhoneController {

    private final PhoneService phoneService;

    private static final String regexp = "^\\+?([0-9])?\\(?[0-9]{3}\\)?[0-9]{3}\\-?[0-9]{2}\\-?[0-9]{2}$";

    @GetMapping()
    public ResponseEntity<JsonResponse> getAll() {
        return ResponseEntity.ok()
                .body(new ListJsonResponse(phoneService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonResponse> getById(@PathVariable("id") Long id) {
        Phone byId = phoneService.findById(id);
        return byId != null
                ? ResponseEntity.ok().body(new ObjectJsonResponse(byId))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ExceptionResponse(HttpStatus.NOT_FOUND, "No such Phone"));
    }

    @GetMapping(params = {"number"})
    public ResponseEntity<JsonResponse> getByNumber(@Valid @Pattern(regexp= regexp) @RequestParam("number") String number) {
        List<Phone> byId = phoneService.findByNumber(number);
        return !byId.isEmpty()
                ? ResponseEntity.ok().body(new ListJsonResponse(byId))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ExceptionResponse(HttpStatus.NOT_FOUND, "No such Phone"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JsonResponse> replacePhone(@PathVariable("id") Long id, @Valid @RequestBody PhoneDTO phoneDTO) {
        Phone phone = phoneService.replacePhone(id, phoneDTO);
        return phone != null
                ? ResponseEntity.ok().body(new ObjectJsonResponse(phone))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JsonResponse> deletePhone(@PathVariable("id") Long id) {
        return phoneService.deleteById(id)
                ? ResponseEntity.ok().body(new ObjectJsonResponse(phoneService.deleteById(id)))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(HttpStatus.NOT_FOUND));
    }
}
