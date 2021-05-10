package com.github.vadim01er.service;

import com.github.vadim01er.entity.User;
import com.github.vadim01er.entity.UserDTO;
import com.github.vadim01er.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepo userRepo;

    public User addUser(String name) {
        User user = new User(name);
        return userRepo.save(user);
    }

    public User findById(Long id) {
        Optional<User> byId = userRepo.findById(id);
        if (byId.isEmpty()) {
            return null;
        }
        return byId.get();
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public boolean deleteById(Long id) {
        if (!userRepo.existsById(id)){
            return false;
        }
        userRepo.deleteById(id);
        return true;
    }

    public User replaceUser(Long id, UserDTO userRequest) {
        Optional<User> user = userRepo.findById(id)
                .map(user1 -> {
                    user1.setName(userRequest.getName());
                    return userRepo.save(user1);
                });
        return user.orElse(null);
    }
}
