package ru.tbcarus.spendingsb.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import ru.tbcarus.spendingsb.model.Note;
import ru.tbcarus.spendingsb.model.NoteType;

import java.util.List;

@Repository
public class DataJpaNoteRepository {

    @Autowired
    JpaNoteRepository noteRepository;

    public List<Note> getAllByEmail(String email) {
        return noteRepository.getAllByEmail(email);
    }

    public List<Note> getAllByUserId(int userId) {
        return noteRepository.getAllByUserId(userId);
    }

    public List<Note> getAllSorted(Specification<Note> spec, Sort sort) {
        return noteRepository.findAll(spec, sort);
    }

    public Note getNote(int id, String email, int userId) { // id записи, email кому адресовано, userId кто отправил
        // поиск уведомления или получателю, или отправителю
        Note note = noteRepository.findById(id).filter(n -> n.getEmail().equals(email)).orElse(null);
        if (note == null) {
            note = noteRepository.findById(id).filter(n -> n.getUser().getId().equals(userId)).orElse(null);
        }
        return note;
    }

    public Note getNoteWithUser(int noteId, String email) {
        return noteRepository.getNoteWithUser(noteId, email);
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public List<Note> saveAll(List<Note> list) {
        return noteRepository.saveAll(list);
    }

    public boolean deleteNote(int id, String email) {
        return noteRepository.deleteNote(id, email) != 0;
    }

    public boolean deleteAllNotesByUserEmail(String email) {
        return noteRepository.deleteAllNotesByUserEmail(email) != 0;
    }

    public boolean deleteAllNotesByUserEmailAndType(String email, NoteType type) {
        return noteRepository.deleteAllNotesByUserEmailAndType(email, type) != 0;
    }

    public boolean deleteOwnInvite(int id, int userId) {
        return noteRepository.deleteOwnInvite(id, userId) != 0;
    }

    public boolean deleteAllOwnNotes(int userId) {
        return noteRepository.deleteAllOwnNotes(userId) != 0;
    }

    public boolean deleteAllOwnInvites(int userId, NoteType type) {
        return noteRepository.deleteAllOwnInvites(userId, type) != 0;
    }

}
