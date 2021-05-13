package com.github.vadim01er.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@JsonAutoDetect
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotNull
    @Length(min = 1)
    private String name;
}
