package ru.tbcarus.spendingsb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.Note;
import ru.tbcarus.spendingsb.model.Role;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.repository.DataJpaNoteRepository;
import ru.tbcarus.spendingsb.repository.JpaUserRepository;

import java.util.List;

@Service
@Slf4j
public class NoteService {
    Sort sort = Sort.by("dateTime");

    @Autowired
    DataJpaNoteRepository noteRepository;

    @Autowired
    JpaUserRepository userRepository;

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

    public Note getNoteWithUser(int noteId, String email) {
        return noteRepository.getNoteWithUser(noteId, email);
    }

    public void deleteNote(int id, String email) {
        if (!noteRepository.deleteNote(id, email)) {
            throw new NotFoundException();
        }
    }

    public void deleteOwnInvite(int id, int userId) {
        if (!noteRepository.deleteOwnInvite(id, userId)) {
            throw new NotFoundException();
        }
    }

    public void inviteAccept(int noteId, User recipient) {
        Note note = getNoteWithUser(noteId, recipient.getEmail());
        User sender = note.getUser();

        recipient.setFriends(sender.getFriends()); // скопировать список друзей пользователю от приглашающего + сам приглашающий
        recipient.setFriendsId(sender.getFriendsId());
        recipient.addFriend(sender.getEmail());
        recipient.addFriendId(sender.getId());
        recipient.removeRole(Role.SUPERUSER); // убрать суперюзера
        recipient.setStartPeriodDate(sender.getStartPeriodDate()); // синхронизация начальной даты
        userRepository.save(recipient); // сохранить пользователя

        for (String email : sender.getFriendsList()) {
            // пройтись по списку друзей приглашающего и всем добавить нового пользователя
            User user = userRepository.getByEmail(email);
            user.addFriend(recipient.getEmail());
            user.addFriendId(recipient.getId());
            userRepository.save(user);
        }
        sender.addFriend(recipient.getEmail());
        sender.addFriendId(recipient.getId());
        userRepository.save(sender);

        noteRepository.deleteNote(noteId, recipient.getEmail()); // удалить уведомление
    }

}
