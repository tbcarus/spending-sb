package ru.tbcarus.spendingsb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbcarus.spendingsb.exception.IncorrectAddition;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.*;
import ru.tbcarus.spendingsb.repository.DataJpaNoteRepository;
import ru.tbcarus.spendingsb.repository.JpaFriendRepository;
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

    @Autowired
    JpaFriendRepository friendRepository;

    public Note create(Note note) {
        return noteRepository.save(note);
    }

    public List<Note> getAll(String email) {
        // сделать все полученные сообщения не новыми, сбросить флаг у юзера
        return noteRepository.getAllByEmail(email);
    }

    public List<Note> getInvitesBySenderId(int userId) {
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
        // Вытаскивает уведомление с юзером из базы
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

    @Transactional
    public void inviteAccept(int noteId, User recipient) {
        recipient = userRepository.getWithFriends(recipient.getId());
        if(recipient.isInGroup()) {
            throw new IncorrectAddition(ErrorType.HAS_GROUP);
        }
        Note note = getNoteWithUser(noteId, recipient.getEmail());
        User sender = note.getUser();

        for (Friend friend : sender.getFriendsList()) {
            // пройтись по списку друзей приглашаемого
            // добавление приглашаемого в друзья всем пользователям группы
            User user = userRepository.getWithFriends(friend.getFriendId());
            user.addFriend(new Friend(user, recipient));
            userRepository.save(user);
            recipient.addFriend(new Friend(recipient, user)); // добавление в друзья всех из списка приглашающего
        }

        recipient.addFriend(new Friend(recipient, sender)); // добавить приглашающего в друзья
        recipient.removeRole(Role.SUPERUSER); // убрать суперюзера
        recipient.setStartPeriodDate(sender.getStartPeriodDate()); // синхронизация начальной даты
        sender.addFriend(new Friend(sender, recipient)); // добавить приглашаемого в друзья

        noteRepository.deleteAllNotesByUserEmailAndType(recipient.getEmail(), NoteType.INVITE); // удалить все полученные приглашения у приглашаемого
        noteRepository.deleteAllNotesByUserEmailAndType(sender.getEmail(), NoteType.INVITE); // удалить все полученные приглашения у приглашающего
        noteRepository.deleteAllOwnInvites(recipient.getId(), NoteType.INVITE); // удалить все отправленные приглашения у приглашаемого
        recipient.setNewNotify(false);
        userRepository.save(recipient); // сохранить пользователя
        sender.setNewNotify(false);
        userRepository.save(sender);
    }

}
