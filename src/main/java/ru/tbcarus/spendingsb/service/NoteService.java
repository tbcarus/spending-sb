package ru.tbcarus.spendingsb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.Note;
import ru.tbcarus.spendingsb.repository.DataJpaNoteRepository;

import java.util.List;

@Service
@Slf4j
public class NoteService {
    Sort sort = Sort.by("dateTime");

    @Autowired
    DataJpaNoteRepository noteRepository;

    public Note create(Note note) {
        return noteRepository.save(note);
    }

    public List<Note> getAll(String email) {
        // сделать все полученные сообщения не новыми, сбросить флаг у юзера
        return noteRepository.getAllByEmail(email);
    }

    public List<Note> getInvites(int userId) {
        return noteRepository.getAllByUserId(userId);
    }

    public Note getNote(int id, String email, int userId) {
        // сделать полученное сообщение прочитанным
        Note note = noteRepository.getNote(id, email, userId);
        if (note == null) {
            log.warn("Уведомление {} для пользователя {} или от {} не найдено", id, email, userId);
            throw new NotFoundException("Уведомление не найдено");
        }
        return note;
    }

    public void deleteNote(int noteId, String email) {

    }

    public void deleteOwnInvite(int noteId, int userId) {

    }

}
