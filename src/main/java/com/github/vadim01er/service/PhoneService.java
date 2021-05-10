package com.github.vadim01er.service;

import com.github.vadim01er.entity.Phone;
import com.github.vadim01er.entity.PhoneDTO;
import com.github.vadim01er.entity.User;
import com.github.vadim01er.repository.PhoneRepo;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Data
@Service
public class PhoneService {

    private final PhoneRepo phoneRepo;

    public List<Phone> findAll() {
        return phoneRepo.findAll();
    }

    public Phone findById(Long id) {
        return phoneRepo.findById(id).orElse(null);
    }

    public List<Phone> findByNumber(String number) {
        return phoneRepo.findByNumber(number);
    }

    public Phone addPhone(User user, PhoneDTO phoneDTO) {
        Phone entity = new Phone();
        entity.setName(phoneDTO.getName());
        entity.setNumber(phoneDTO.getNumber());
        entity.setUser(user);
        return phoneRepo.save(entity);
    }

    public Phone replacePhone(Long id, PhoneDTO phoneDTO) {
        Optional<Phone> phone = phoneRepo.findById(id)
                .map(phone1 -> {
                    phone1.setName(phoneDTO.getName());
                    phone1.setNumber(phoneDTO.getNumber());
                    return phoneRepo.save(phone1);
                });
        return phone.orElse(null);
    }

    public boolean deleteById(Long id) {
        if (!phoneRepo.existsById(id)) {
            return false;
        }
        phoneRepo.deleteById(id);
        return true;
    }
}
