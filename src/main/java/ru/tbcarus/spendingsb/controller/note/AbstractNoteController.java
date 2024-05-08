package ru.tbcarus.spendingsb.controller.note;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.tbcarus.spendingsb.model.Note;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.NoteService;
import ru.tbcarus.spendingsb.service.UserService;

import java.util.List;

@Slf4j
public class AbstractNoteController {

    @Autowired
    NoteService noteService;

    @Autowired
    UserService userService;

    public List<Note> getAll(User user) {
        log.info("get all notes for user {}", user.getEmail());
        user = getByIdWithFriends(user.getId());
        if (user.isNewNotify()) {
            // сбросить флаг новых уведомлений при входе на страницу
            clearNewNotify(user);
        }
        return noteService.getAll(user.getEmail());
    }

    public List<Note> getInvites(User user) {
        log.info("get all invites from user {}", user.getId());
        user = getByIdWithFriends(user.getId());
        return noteService.getInvitesBySenderId(user.id());
    }

    public Note getNote(int id, String email, User user) {
        log.info("get note {} for {} or from {}", id, email, user.getId());
        return noteService.getNote(id, email, user.getId());
    }

    public void deleteNote(int id, User user) {
        log.info("delete note {} for user {}", id, user.getEmail());
        noteService.deleteNote(id, user);
        userService.checkMyNotify(user);
    }

    public void deleteOwnInvite(int noteId, int userId, String email) {
        log.info("delete own invite {} by user {}", noteId, userId);
        noteService.deleteOwnInvite(noteId, userId);
        userService.checkUserNotify(email);
    }

    public void inviteAccept(int noteId, User recipient) {
        log.info("accept invite {} by user {}", noteId, recipient.getEmail());
        List<Note> recipientInvites = noteService.getInvitesBySenderId(recipient.getId());
        noteService.inviteAccept(noteId, recipient);
        int x = 5;
        userService.checkUsersNotify(recipientInvites);
    }

    public User clearNewNotify(User user) {
        return userService.clearNewNotify(user);
    }

    public User getByIdWithFriends(int id) {
        return userService.getByIdWithFriends(id);
    }
}
