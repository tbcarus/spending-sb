package ru.tbcarus.spendingsb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tbcarus.spendingsb.model.Note;
import ru.tbcarus.spendingsb.repository.DataJpaNoteRepository;

@Service
@Slf4j
public class NoteService {

    @Autowired
    DataJpaNoteRepository noteRepository;

    public Note create(Note note) {
        return noteRepository.save(note);
    }

}
