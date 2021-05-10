package com.github.vadim01er.repository;

import com.github.vadim01er.entity.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneRepo extends JpaRepository<Phone, Long> {

    List<Phone> findByNumber(String number);
}
