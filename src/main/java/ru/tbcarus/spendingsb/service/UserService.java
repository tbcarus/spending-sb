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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.tbcarus.spendingsb.exception.BadRegistrationRequest;
import ru.tbcarus.spendingsb.exception.IllegalRequestDataException;
import ru.tbcarus.spendingsb.exception.IncorrectAddition;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.*;
import ru.tbcarus.spendingsb.repository.JpaUserRepository;
import ru.tbcarus.spendingsb.util.UserUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Validated
public class UserService implements UserDetailsService {
    private static final Sort SORT_NAME_EMAIL = Sort.by(Sort.Direction.ASC, "name", "email");
    public final JpaUserRepository userRepository;

    @Autowired
    private NoteService noteService;

    @Autowired
    EmailActionService emailActionService;

    @Autowired
    FriendService friendService;

    public UserService(JpaUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User create(User user) {
        user.setEmail(user.getEmail().toLowerCase());
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
        Optional<User> opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) {
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

    @Transactional
    public void levelDates(User user) {
        for (Friend f : user.getFriendsList()) {
            User u = getById(f.getFriendId());
            u.setStartPeriodDate(user.getStartPeriodDate());
            userRepository.save(u);
        }
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
        User user = getById(userId);
        if (user.isBanned()) {
            throw new IllegalRequestDataException("User banned!");
        }
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    @Transactional
    public User ban(int userId, boolean ban) {
        User user = getById(userId);
        user.setBanned(ban);
        if (ban) {
            user.setEnabled(false);
        }
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UsernameNotFoundException("User ‘" + username + "’ not found");
    }

    public List<User> getFriends(User user) {
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

    public Map<User, Friend> getFriendsMap(User user) {
        List<User> users = getFriends(user);
        List<Friend> copy = new ArrayList<>(user.getFriendsList());
        Map<User, Friend> userMap = new HashMap<>();
        for (User u : users) {
            Friend f = copy.stream().filter(friend -> friend.getFriendEmail().equals(u.getEmail())).findFirst().get();
            userMap.put(u, f);
        }
        return userMap;
    }

    public Map<Integer, Friend> getFriendsMapId(User user) {
        List<User> users = getFriends(user);
        List<Friend> copy = new ArrayList<>(user.getFriendsList());
        Map<Integer, Friend> userMap = new HashMap<>();
        for (User u : users) {
            Friend f = copy.stream().filter(friend -> friend.getFriendEmail().equals(u.getEmail())).findFirst().get();
            userMap.put(u.getId(), f);
        }
        return userMap;
    }

    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    @Transactional
    public void deleteUserFromGroup(User user, int friendId) {
        User userExcluded = getByIdWithFriends(friendId);
        // Проверить, что пользователь в этой группе
        if (!user.hasFriend(friendId)) {
            throw new NotFoundException("user " + userExcluded.getEmail() + " is not friend for " + user.getEmail());
        }
        // Удалить пользователя у всех в группе
        for (Friend f : userExcluded.getFriendsList()) {
            User u = getById(f.getFriendId());
            Optional<Friend> opt = u.getFriendsList()
                    .stream()
                    .filter(fr -> fr.getFriendEmail().equals(userExcluded.getEmail()))
                    .findFirst();
            opt.ifPresent(u::deleteFriend);
            userRepository.save(u);
        }
        // Очистить список друзей у пользователя, восстановить суперюзера
        userExcluded.removeAllFriends();
        userExcluded.addRole(Role.SUPERUSER);
        userRepository.save(userExcluded);
    }

    @Transactional
    public void deleteGroupGroupSelf(User user) {
        boolean isFirst = true;
        // Удалить себя из списков юзеров группы
        for (Friend f : user.getFriendsList()) {
            User u = getByIdWithFriends(f.getFriendId());
            if (user.isSuperUser() && isFirst) { // Если удаляется суперюзер, то суперюзер даётся первому в списке
                u.addRole(Role.SUPERUSER);
                isFirst = false;
            }
            u.deleteFriend(new Friend(u, user));
            userRepository.save(u);
        }

        user.removeAllFriends();        // Очистить у себя список группы
        user.addRole(Role.SUPERUSER);   // Восстановить суперюзера
        userRepository.save(user);
    }

    public User resetPassword(String email, String code, String password, String passwordReply) {
        if (!password.equals(passwordReply)) {
            throw new BadRegistrationRequest(ErrorType.DO_NOT_MATCH);
        }
        if (password.length() < 4 || password.length() > 128) {
            throw new BadRegistrationRequest(ErrorType.WRONG_LENGTH);
        }

        User user = userRepository.getByEmail(email);
        EmailAction emailAction = emailActionService.get(code);
        user.setPassword(password);
        userRepository.save(user);

        return user;
    }

    @Transactional
    public void sendFriendInvite(User user, String email) {
        if (!email.equals(user.getEmail())) {
            if (user.hasFriend(email)) {
                throw new IncorrectAddition(ErrorType.ALREADY_IN_GROUP);
            }
            if (user.getFriendsList().size() >= UserUtil.DEFAULT_MAX_FRIENDS) {
                throw new IncorrectAddition(ErrorType.TOO_MUCH_FRIENDS);
            }
            if (user.getFriendsList().size() + noteService.getInvitesBySenderId(user.getId()).size() >= 5) {
                throw new IncorrectAddition(ErrorType.TOO_MUCH_INVITES);
            }

            User userDest = getByEmail(email);
            if (userDest.isInGroup()) {
                throw new IncorrectAddition(ErrorType.HAS_GROUP);
            }

            Note note = new Note(NoteType.INVITE, false, LocalDateTime.now(), "Объединение досок",
                    "Пользователь " + user.getEmail() + " объединение досок", email, user);
            noteService.create(note);
            userDest.setNewNotify(true);
            update(userDest, userDest.getId());
        }
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

    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public void addSU(User user, int friendId) {
        if (!user.isSuperUser() && user.hasFriend(friendId)) {
            throw new IllegalRequestDataException("User is not superuser or another user is not friend!");
        }
        User u = getById(friendId);
        u.addRole(Role.SUPERUSER);
        userRepository.save(u);
    }

    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public void removeSU(User user, int friendId) {
        if (!user.isSuperUser() && user.hasFriend(friendId)) {
            throw new IllegalRequestDataException("User is not superuser or another user is not friend!");
        }
        User u = getById(friendId);
        u.removeRole(Role.SUPERUSER);
        userRepository.save(u);
    }

    public User clearNewNotify(User user) {
        User u = userRepository.getByEmail(user.getEmail());
        u.setNewNotify(false);
        return userRepository.save(u);
    }
}
