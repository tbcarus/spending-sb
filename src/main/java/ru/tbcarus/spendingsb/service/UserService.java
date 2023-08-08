package ru.tbcarus.spendingsb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
public class UserService {
    public final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User create(User user) {
        user.setEmail(user.getEmail().toLowerCase());
        return userRepository.save(user);
    }

    public List<User> getAll() {
        return userRepository.findAllByOrderByName();
    }

    public User getById(int id) {
        return userRepository.findById(id).get();
    }

    public User getByEmail(String email) {
        return userRepository.getByEmail(email);
    }

    public User update(User update, int id) {
        if(!userRepository.existsById(id)){
            throw new NotFoundException("User with id = " + id + " not found");
        }
        User user = userRepository.findById(id).get();
        if (update.getName() != null) {
            user.setName(update.getName());
        }
        if (update.getEmail() != null) {
            user.setEmail(update.getEmail().toLowerCase());
        }
        if (update.getPassword() != null) {
            user.setPassword(update.getPassword());
        }
        if (update.getStartPeriodDate() != null) {
            user.setStartPeriodDate(update.getStartPeriodDate());
        }
        return userRepository.save(user);
    }

    public void delete(int id) {
        userRepository.deleteById(id);
    }

}
