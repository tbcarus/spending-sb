package ru.tbcarus.spendingsb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.repository.JpaUserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    private static final Sort SORT_NAME_EMAIL = Sort.by(Sort.Direction.ASC, "name", "email");
    public final JpaUserRepository userRepository;

    public UserService(JpaUserRepository userRepository) {
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
        Optional<User> opt = userRepository.findById(id);
        if (!opt.isPresent()) {
            throw new NotFoundException();
        }
        return opt.get();
    }

    public User getByEmail(String email) {
        Optional<User> opt = userRepository.findByEmail(email);
        if (!opt.isPresent()) {
            throw new NotFoundException();
        }
        return opt.get();
    }

    public User update(User update, int id) {
        if (!userRepository.existsById(id)) {
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
        user.setEnabled(update.isEnabled());
        if (update.getStartPeriodDate() != null) {
            user.setStartPeriodDate(update.getStartPeriodDate());
        }
        if (update.getRoles() != null) {
            user.setRoles(update.getRoles());
        }

        return userRepository.save(user);
    }

    public void delete(int id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UsernameNotFoundException("User ‘" + username + "’ not found");
    }
}
