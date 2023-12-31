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

    public Note getNote(int id, String email, User user) {
        log.info("get note {} for {} or from {}", id, email, user.getId());
        return noteService.getNote(id, email, user.getId());
    }
}
