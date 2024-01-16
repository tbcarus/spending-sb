package ru.tbcarus.spendingsb.controller.note;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tbcarus.spendingsb.model.Note;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.NoteService;

import java.util.List;

@Slf4j
public class AbstractNoteController {

    @Autowired
    NoteService noteService;

    public List<Note> getAll(User user) {
        log.info("get all notes for user {}", user.getEmail());
        return noteService.getAll(user.getEmail());
    }

    public List<Note> getInvites(User user) {
        log.info("get all invites from user {}", user.getId());
        return noteService.getInvites(user.id());
    }

    public Note getNote(int id, String email, User user) {
        log.info("get note {} for {} or from {}", id, email, user.getId());
        return noteService.getNote(id, email, user.getId());
    }

    public void deleteNote(int id, String email) {
        log.info("delete note {} for user {}", id, email);
        noteService.deleteNote(id, email);
    }

    public void deleteOwnInvite(int id, int userId) {
        log.info("delete own invite {} by user {}", id, userId);
        noteService.deleteOwnInvite(id, userId);
    }

    public void inviteAccept(int noteId, User user) {
        log.info("accept invite {} by user {}", noteId, user.getEmail());
        noteService.inviteAccept(noteId, user);
    }
}
