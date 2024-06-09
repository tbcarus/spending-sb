package ru.tbcarus.spendingsb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.tbcarus.spendingsb.exception.IllegalRequestDataException;
import ru.tbcarus.spendingsb.exception.IncorrectAddition;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.*;
import ru.tbcarus.spendingsb.repository.JpaUserRepository;
import ru.tbcarus.spendingsb.util.UserUtil;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@Validated
public class GroupService {

    @Autowired
    UserService userService;

    @Autowired
    NoteService noteService;

    @Autowired
    JpaUserRepository userRepository;

    public List<User> getGroupUserList(User user) {
        return userService.getGroupUserList(user);
    }

    public Map<Integer, Friend> getFriendsMapId(User user, List<User> users) {
        List<Friend> copy = new ArrayList<>(user.getFriendsList());
        Map<Integer, Friend> userMap = new HashMap<>();
        for (User u : users) {
            Friend f = copy.stream().filter(friend -> friend.getFriendEmail().equals(u.getEmail())).findFirst().get();
            userMap.put(u.getId(), f);
        }
        return userMap;
    }

    public Map<Integer, Friend> getFriendsMapId(User user) {
        List<User> users = getGroupUserList(user);
        List<Friend> copy = new ArrayList<>(user.getFriendsList());
        Map<Integer, Friend> userMap = new HashMap<>();
        for (User u : users) {
            Friend f = copy.stream().filter(friend -> friend.getFriendEmail().equals(u.getEmail())).findFirst().get();
            userMap.put(u.getId(), f);
        }
        return userMap;
    }

    public Map<User, Friend> getFriendsMap(User user) {
        List<User> users = getGroupUserList(user);
        List<Friend> copy = new ArrayList<>(user.getFriendsList());
        Map<User, Friend> userMap = new HashMap<>();
        for (User u : users) {
            Friend f = copy.stream().filter(friend -> friend.getFriendEmail().equals(u.getEmail())).findFirst().get();
            userMap.put(u, f);
        }
        return userMap;
    }

    @Transactional
    public Note sendFriendInvite(User user, String email) {
        Note note = null;
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

            User userDest = userService.getByEmail(email);
            if (userDest.isInGroup()) {
                throw new IncorrectAddition(ErrorType.HAS_GROUP);
            }

            note = new Note(NoteType.INVITE, false, false, LocalDateTime.now(), "Объединение досок",
                    "Пользователь " + user.getEmail() + " объединение досок", email, user);
            noteService.create(note);
            userDest.setNewNotify(true);
            userRepository.save(userDest);
        }
        return note;
    }

    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    @Transactional
    public void levelDates(User user) {
        for (Friend f : user.getFriendsList()) {
            User u = userService.getById(f.getFriendId());
            u.setStartPeriodDate(user.getStartPeriodDate());
            userRepository.save(u);
        }
    }

    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    @Transactional
    public void deleteUserFromGroup(User user, int friendId) {
        User userExcluded = userService.getByIdWithFriends(friendId);
        // Проверить, что пользователь в этой группе
        if (!user.hasFriend(friendId)) {
            throw new NotFoundException("user " + userExcluded.getEmail() + " is not friend for " + user.getEmail());
        }
        // Удалить пользователя у всех в группе
        for (Friend f : userExcluded.getFriendsList()) {
            User u = userService.getById(f.getFriendId());
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
    public void deleteFromGroupSelf(User user) {
        boolean isFirst = true;
        // Удалить себя из списков юзеров группы
        for (Friend f : user.getFriendsList()) {
            User u = userService.getByIdWithFriends(f.getFriendId());
            if (user.isSuperUser() && isFirst) { // Если удаляется суперюзер, то суперюзер даётся первому в списке
                u.addRole(Role.SUPERUSER);
                isFirst = false;
            }
            Optional<Friend> opt = u.getFriendsList()
                    .stream()
                    .filter(fr -> fr.getFriendEmail().equals(user.getEmail()))
                    .findFirst();
            opt.ifPresent(u::deleteFriend);
            userRepository.save(u);
        }

        user.removeAllFriends();        // Очистить у себя список группы
        user.addRole(Role.SUPERUSER);   // Восстановить суперюзера
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public void addSU(User user, int friendId) {
        if (!user.isSuperUser() && user.hasFriend(friendId)) {
            throw new IllegalRequestDataException("User is not superuser or another user is not friend!");
        }
        User u = userService.getById(friendId);
        u.addRole(Role.SUPERUSER);
        userRepository.save(u);
    }

    @PreAuthorize("hasRole('ROLE_SUPERUSER')")
    public void removeSU(User user, int friendId) {
        if (!user.isSuperUser() && user.hasFriend(friendId)) {
            throw new IllegalRequestDataException("User is not superuser or another user is not friend!");
        }
        User u = userService.getById(friendId);
        u.removeRole(Role.SUPERUSER);
        userRepository.save(u);
    }

}
