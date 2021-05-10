package com.github.vadim01er.controller;

import com.github.vadim01er.entity.Phone;
import com.github.vadim01er.entity.PhoneDTO;
import com.github.vadim01er.entity.User;
import com.github.vadim01er.entity.UserDTO;
import com.github.vadim01er.json.ExceptionResponse;
import com.github.vadim01er.json.JsonResponse;
import com.github.vadim01er.json.ListJsonResponse;
import com.github.vadim01er.json.ObjectJsonResponse;
import com.github.vadim01er.service.PhoneService;
import com.github.vadim01er.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final PhoneService phoneService;


    @GetMapping()
    public ResponseEntity<JsonResponse> getAll() {
        return ResponseEntity.ok()
                .body(new ListJsonResponse(userService.findAll()));
    }

    @PostMapping()
    public ResponseEntity<JsonResponse> addUser(@RequestBody UserDTO userRequest) {
        if (userRequest.getName() == null) {
            return ResponseEntity.badRequest().body(new ExceptionResponse(HttpStatus.BAD_REQUEST, "Username must not be null"));
        }
        User user = userService.addUser(userRequest.getName());
        return user != null
                ? ResponseEntity.ok().body(new ObjectJsonResponse(user))
                : ResponseEntity.badRequest().body(new ExceptionResponse(HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JsonResponse> getById(@PathVariable("id") Long id) {
        User byId = userService.findById(id);
        return byId != null
                ? ResponseEntity.ok().body(new ObjectJsonResponse(byId))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ExceptionResponse(HttpStatus.NOT_FOUND, "No such User"));
    }

    @GetMapping(params = {"name"})
    public ResponseEntity<JsonResponse> getByName(@RequestParam("name") String name) {
        if (name.equals("")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ExceptionResponse(HttpStatus.BAD_REQUEST, "Param name must not be 0 lenght"));
        }
        List<User> all = userService.findAll();
        Pattern pattern = Pattern.compile(".*" + name + ".*");
        all = all.stream().filter(user -> pattern.matcher(user.getName()).find()).collect(Collectors.toList());
        return !all.isEmpty()
                ? ResponseEntity.ok().body(new ListJsonResponse(all))
                : ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionResponse(HttpStatus.NOT_FOUND, "No such User"));
    }

    @GetMapping("/{id}/phones")
    public ResponseEntity<JsonResponse> getUserPhonesById(@PathVariable("id") Long id) {
        User byId = userService.findById(id);
        return byId != null
                ? ResponseEntity.ok().body(new ObjectJsonResponse(byId.getPhone()))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JsonResponse> replaceUser(@PathVariable("id") Long id, @RequestBody UserDTO userRequest) {
        User user = userService.replaceUser(id, userRequest);
        return user != null
                ? ResponseEntity.ok().body(new ObjectJsonResponse(user))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JsonResponse> deleteUser(@PathVariable("id") Long id) {
        return userService.deleteById(id)
                ? ResponseEntity.ok().body(new JsonResponse(HttpStatus.OK.value()))
                : ResponseEntity.badRequest().body(new ExceptionResponse(HttpStatus.BAD_REQUEST, "user no deleted"));
    }

    @PostMapping("/{id}/phones")
    public ResponseEntity<JsonResponse> saveContact(@PathVariable("id") Long id, @RequestBody PhoneDTO phoneDTO) {
        if (phoneDTO.getName() == null || phoneDTO.getNumber() == null) {
            return ResponseEntity.badRequest().body(
                    new ExceptionResponse(HttpStatus.BAD_REQUEST, "Name and number must not be null"));
        }
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.badRequest().body(
                    new ExceptionResponse(HttpStatus.BAD_REQUEST, "No find User"));
        }
        Phone phone = phoneService.addPhone(user, phoneDTO);
        return phone != null
                ? ResponseEntity.ok().body(new ObjectJsonResponse(phone))
                : ResponseEntity.badRequest().body(new ExceptionResponse(HttpStatus.BAD_REQUEST));
    }

}
