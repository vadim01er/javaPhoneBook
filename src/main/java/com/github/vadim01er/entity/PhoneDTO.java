package com.github.vadim01er.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDTO {
    @NotNull
    @Length(min = 1)
    private String name;

    @NotNull
    @Pattern(regexp="^\\+?([0-9])?\\(?[0-9]{3}\\)?[0-9]{3}\\-?[0-9]{2}\\-?[0-9]{2}$")
    private String number;
}
