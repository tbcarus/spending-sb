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

    public List<Note> getAll(Sort sort) {
        return noteRepository.findAll(sort);
    }

    public List<Note> getAllSorted(Specification<Note> spec, Sort sort) {
        return noteRepository.findAll(spec, sort);
    }

    public Note getNote(int id, String email) {
        return noteRepository.findById(id).filter(note -> note.getEmail().equals(email)).orElse(null);
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public void delete(int id, String email) {
        noteRepository.delete(id, email);
    }

}
