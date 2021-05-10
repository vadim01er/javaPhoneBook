package com.github.vadim01er.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vadim01er.entity.Phone;
import com.github.vadim01er.entity.PhoneDTO;
import com.github.vadim01er.entity.User;
import com.github.vadim01er.entity.UserDTO;
import com.github.vadim01er.service.PhoneService;
import com.github.vadim01er.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private PhoneService phoneService;

    @Test
    void getAll() throws Exception {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())));
    }

    @Test
    void addUserSuccess() throws Exception {
        UserDTO user = new UserDTO("it is test mock");
        String request = objectMapper.writeValueAsString(user);
        MvcResult mock = mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.object.name", is("it is test mock")))
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(mock.getResponse().getContentAsString());
        Long id = jsonNode.get("object").get("id").asLong();
        userService.deleteById(id);
    }

    @Test
    void addUserBadRequest() throws Exception {
        UserDTO user = new UserDTO();
        String request = objectMapper.writeValueAsString(user);
        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())));
    }

    @Test
    void getByIdBadRequest() throws Exception {
        mockMvc.perform(get("/users/-1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    void getByIdSuccess() throws Exception {
        User testUser = userService.addUser("test name");
        mockMvc.perform(get("/users/" + testUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.object.name", is(testUser.getName())));
        userService.deleteById(testUser.getId());
    }

    @Test
    void getByNameNotFound() throws Exception {
        mockMvc.perform(get("/users").param("name", "-1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    void getByNameBadRequest() throws Exception {
        mockMvc.perform(get("/users").param("name", ""))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())));
    }

    @Test
    void getByNameSuccess() throws Exception {
        User testUser = userService.addUser("test name");
        mockMvc.perform(get("/users").param("name", testUser.getName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.response[0].name", is(testUser.getName())));
        userService.deleteById(testUser.getId());
    }

    @Test
    void getUserPhonesByIdNotFound() throws Exception {
        mockMvc.perform(get("/users/-1/phones"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    void getUserPhonesByIdSuccess() throws Exception {
        User testUser = userService.addUser("test name");
        Phone testPhone = phoneService.addPhone(testUser, new PhoneDTO("phone name", "888888"));
        mockMvc.perform(get("/users/" + testUser.getId() + "/phones"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.object[0].name", is(testPhone.getName())))
                .andExpect(jsonPath("$.object[0].number", is(testPhone.getNumber())));
        phoneService.deleteById(testPhone.getId());
        userService.deleteById(testUser.getId());
    }

    @Test
    void replaceUserBadRequest() throws Exception {
        UserDTO user = new UserDTO();
        String request = objectMapper.writeValueAsString(user);
        mockMvc.perform(
                put("/users/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    void replaceUserNotFound() throws Exception {
        UserDTO user = new UserDTO("new test name");
        String request = objectMapper.writeValueAsString(user);
        mockMvc.perform(
                put("/users/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    void replaceUserSuccess() throws Exception {
        User testUser = userService.addUser("test name");
        UserDTO user = new UserDTO("new test name");
        String request = objectMapper.writeValueAsString(user);
        mockMvc.perform(
                put("/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.object.name", is(user.getName())));
        userService.deleteById(testUser.getId());
    }

    @Test
    void deleteUserNotFound() throws Exception {
        mockMvc.perform(
                delete("/users/-1"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())));
    }

    @Test
    void deleteUserSuccess() throws Exception {
        User testUser = userService.addUser("test name");
        mockMvc.perform(delete("/users/" + testUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())));
        assertNull(userService.findById(testUser.getId()));
    }

    @Test
    void saveContactBadRequest() throws Exception {
        User testUser = userService.addUser("it is test mock");
        PhoneDTO testPhone = new PhoneDTO();
        String request = objectMapper.writeValueAsString(testPhone);
        mockMvc.perform(
                post("/users/" + testUser.getId() + "/phones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())));
        userService.deleteById(testUser.getId());
    }

    @Test
    void saveContactBadRequestNoFindUser() throws Exception {
        PhoneDTO testPhone = new PhoneDTO("phone name", "888888");
        String request = objectMapper.writeValueAsString(testPhone);
        mockMvc.perform(
                post("/users/-1/phones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())));
    }

    @Test
    void saveContactSuccess() throws Exception {
        User testUser = userService.addUser("it is test mock");
        PhoneDTO testPhone = new PhoneDTO("phone name", "888888");
        String request = objectMapper.writeValueAsString(testPhone);
        MvcResult mvcResult = mockMvc.perform(
                post("/users/" + testUser.getId() + "/phones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.object.name", is(testPhone.getName())))
                .andReturn();
        phoneService.deleteById(
                objectMapper.readTree(mvcResult.getResponse().getContentAsString())
                        .get("object")
                        .get("id")
                        .asLong());
        userService.deleteById(testUser.getId());
    }
}