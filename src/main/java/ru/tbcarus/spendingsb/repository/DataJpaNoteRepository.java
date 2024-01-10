package ru.tbcarus.spendingsb.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import ru.tbcarus.spendingsb.model.Note;

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

    public Note getNote(int id, String email, int userId) { // id записи, email кому адресовано, usrtId кто отправил
        // поиск уведомления или получателю, или отправителю
        Note note = noteRepository.findById(id).filter(n -> n.getEmail().equals(email)).orElse(null);
        if (note == null) {
            note = noteRepository.findById(id).filter(n -> n.getUser().getId().equals(userId)).orElse(null);
        }
        return note;
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public void deleteNote(int id, String email) {
        noteRepository.deleteNote(id, email);
    }

    public void deleteOwnInvite(int id, int userId) {

    }

}
