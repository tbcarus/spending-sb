package ru.tbcarus.spendingsb.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.tbcarus.spendingsb.exception.BadRegistrationRequest;
import ru.tbcarus.spendingsb.exception.IllegalRequestDataException;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.*;
import ru.tbcarus.spendingsb.repository.DataJpaNoteRepository;
import ru.tbcarus.spendingsb.repository.JpaUserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Validated
public class UserService implements UserDetailsService {
    private static final Sort SORT_NAME_EMAIL = Sort.by(Sort.Direction.ASC, "name", "email");
    public final JpaUserRepository userRepository;

    @Autowired
    private DataJpaNoteRepository noteRepository;

    @Autowired
    EmailActionService emailActionService;

    @Autowired
    FriendService friendService;

    @Autowired
    PasswordEncoder encoder;

    public UserService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User create(User user) {
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        emailActionService.activationRequest(savedUser);
        return savedUser;
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

    public User getByIdWithFriends(int id) {
        User user = userRepository.getWithFriends(id);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    public User getByEmail(@Email @NotEmpty @NotNull String email) {
        Optional<User> opt = userRepository.findByEmailIgnoreCase(email);
        if (opt.isEmpty()) {
            throw new NotFoundException("Пользователь " + email + " не найден.");
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
        if (update.getPassword() != null && update.getPassword().length() >= 4 && update.getPassword().length() <= 128) {
            user.setPassword(update.getPassword());
        }
        user.setEnabled(update.isEnabled());
        user.setBanned(update.isBanned());
        if (update.getStartPeriodDate() != null) {
            user.setStartPeriodDate(update.getStartPeriodDate());
        }
        if (update.getRoles() != null) {
            user.setRoles(update.getRoles());
        }
        user.setNewNotify(update.isNewNotify());

        return userRepository.save(user);
    }

    @Transactional
    public void changeStartDate(User user, LocalDate startDate) {
        user.setStartPeriodDate(startDate);
        userRepository.save(user);
//        for (Friend f : user.getFriendsList()) {
//            User u = getById(f.getFriendId());
//            u.setStartPeriodDate(startDate);
//            userRepository.save(u);
//        }
    }

    public void delete(int id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public User enable(int userId, boolean enabled) {
        User user = getByIdWithFriends(userId);
        if (user.isBanned()) {
            throw new IllegalRequestDataException("User banned!");
        }
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    @Transactional
    public User ban(int userId, boolean ban) {
        User user = getByIdWithFriends(userId);
        user.setBanned(ban);
        if (ban) {
            user.setEnabled(false);
        }
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmailIgnoreCase(username);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UsernameNotFoundException("User ‘" + username + "’ not found");
    }

    public List<User> getGroupUserList(User user) {
        if (user.getFriendsList().isEmpty()) {
            return new ArrayList<User>();
        }
        List<Friend> copy = new ArrayList<>(user.getFriendsList());
        Specification<User> sp = filterByEmail(copy.get(0).getFriendEmail());
        copy.remove(0);
        for (Friend f : copy) {
            sp = sp.or(filterByEmail(f.getFriendEmail()));
        }
        return userRepository.findAll(sp);
    }


    public User resetPassword(String email, String code, String password, String passwordReply) {
        if (!password.equals(passwordReply)) {
            throw new BadRegistrationRequest(ErrorType.DO_NOT_MATCH);
        }
        if (password.length() < 4 || password.length() > 128) {
            throw new BadRegistrationRequest(ErrorType.WRONG_LENGTH);
        }

        User user = userRepository.findByEmailIgnoreCase(email).get();
        EmailAction emailAction = emailActionService.get(code);
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
        return user;
    }



    public Specification<User> filterByEmail(String email) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (email == null) {
                    return null;
                }
                return criteriaBuilder.equal(root.get("email"), email);
            }
        };
    }

    public User clearNewNotify(User user) {
        user.setNewNotify(false);
        return userRepository.save(user);
    }

    public void checkMyNotify(User user) {
        // Проверка наличия непоказанных уведомлений (например, если удалять уведомление запросом,
        // не заходя на страницу уведомлений, или без запроса всех уведомлений)
        List<Note> notes = noteRepository.getAllByEmail(user.getEmail());
        List<Note> notShown = notes.stream().filter(n -> !n.isShown()).toList();
        if (notShown.isEmpty()) {
            user.setNewNotify(false);
            userRepository.save(user);
        }
    }

    public void checkUserNotify(String email) {
        // Проверка наличия уведомлений у юзера, у которого удаляется отправленное приглашение
        List<Note> notes = noteRepository.getAllByEmail(email);
        List<Note> notShown = notes.stream().filter(n -> !n.isShown()).toList();
        if (notShown.isEmpty()) {
            User user = userRepository.getByEmail(email);
            user.setNewNotify(false);
            userRepository.save(user);
        }
    }

    public void checkUsersNotify(List<Note> noteList) {
        for (Note note : noteList) {
            checkUserNotify(note.getEmail());
        }
    }
}
