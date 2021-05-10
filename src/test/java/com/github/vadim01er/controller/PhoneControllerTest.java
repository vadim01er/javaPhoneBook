package com.github.vadim01er.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vadim01er.entity.Phone;
import com.github.vadim01er.entity.PhoneDTO;
import com.github.vadim01er.entity.User;
import com.github.vadim01er.service.PhoneService;
import com.github.vadim01er.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PhoneControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private PhoneService phoneService;

    @Test
    void getAll() throws Exception {
        mockMvc.perform(get("/phones"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())));
    }

    @Test
    void getByIdNotFound() throws Exception {
        mockMvc.perform(get("/phones/-1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    void getByIdSuccess() throws Exception {
        User testUser = userService.addUser("test name");
        Phone testPhone = phoneService.addPhone(testUser, new PhoneDTO("phone name", "88888"));
        mockMvc.perform(get("/phones/" + testPhone.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.object.name", is(testPhone.getName())))
                .andExpect(jsonPath("$.object.number", is(testPhone.getNumber())));
        phoneService.deleteById(testPhone.getId());
        userService.deleteById(testUser.getId());
    }

    @Test
    void getByNumberNotFound() throws Exception {
        mockMvc.perform(get("/phones").param("number", "-1111111111"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    void getByNumberBadRequest() throws Exception {
        mockMvc.perform(get("/phones").param("number", "8"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())));
    }

    @Test
    void getByNumberSuccess() throws Exception {
        User testUser = userService.addUser("test name");
        Phone testPhone = phoneService.addPhone(testUser, new PhoneDTO("phone name", "88888888888"));
        mockMvc.perform(get("/phones").param("number", testPhone.getNumber()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.response[0].name", is(testPhone.getName())))
                .andExpect(jsonPath("$.response[0].number", is(testPhone.getNumber())));
        phoneService.deleteById(testPhone.getId());
        userService.deleteById(testUser.getId());
    }

    @Test
    void replacePhoneBadRequest() throws Exception {
        PhoneDTO phoneDTO = new PhoneDTO();
        String request = objectMapper.writeValueAsString(phoneDTO);
        mockMvc.perform(
                put("/phones/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())));
    }

    @Test
    void replacePhoneNotFound() throws Exception {
        PhoneDTO phoneDTO = new PhoneDTO("new test name phone", "111111");
        String request = objectMapper.writeValueAsString(phoneDTO);
        mockMvc.perform(
                put("/phones/-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    void replacePhoneSuccess() throws Exception {
        User testUser = userService.addUser("test name");
        Phone testPhone = phoneService.addPhone(testUser, new PhoneDTO("test name phone", "8888888"));
        PhoneDTO phoneDTO = new PhoneDTO("new test name phone", "111111");
        String request = objectMapper.writeValueAsString(phoneDTO);
        mockMvc.perform(
                put("/phones/" + testPhone.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())))
                .andExpect(jsonPath("$.object.name", is(phoneDTO.getName())))
                .andExpect(jsonPath("$.object.number", is(phoneDTO.getNumber())));
        phoneService.deleteById(testPhone.getId());
        userService.deleteById(testUser.getId());
    }

    @Test
    void deletePhoneNotFound() throws Exception {
        mockMvc.perform(
                delete("/phones/-1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(HttpStatus.NOT_FOUND.value())));
    }

    @Test
    void deletePhoneSuccess() throws Exception {
        User testUser = userService.addUser("test name");
        Phone testPhone = phoneService.addPhone(testUser, new PhoneDTO("new test name phone", "111111"));
        mockMvc.perform(delete("/phones/" + testPhone.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(HttpStatus.OK.value())));
        assertNull(phoneService.findById(testPhone.getId()));
        userService.deleteById(testUser.getId());
    }
}