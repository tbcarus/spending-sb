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
import java.util.stream.Collectors;

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
        List<Note> notes = noteRepository.getAllByEmail(email);
        List<Note> notShown = notes.stream().filter(n -> !n.isShown()).toList();
        notShown.forEach(n -> n.setShown(true));
        noteRepository.saveAll(notShown);
        return notes;
    }

    public List<Note> getInvitesBySenderId(int userId) {
        return noteRepository.getAllByUserId(userId);
    }

    public Note getNote(int id, String email, int userId) {
        Note note = noteRepository.getNote(id, email, userId);
        if (note == null) {
            log.warn("Уведомление {} для пользователя {} или от {} не найдено", id, email, userId);
            throw new NotFoundException("Уведомление не найдено");
        }
        note.setRead(true);
        noteRepository.save(note);
        return note;
    }

    public Note getNoteWithUser(int noteId, String email) {
        // Вытаскивает уведомление с юзером из базы
        return noteRepository.getNoteWithUser(noteId, email);
    }

    public void deleteNote(int id, User user) {
        if (!noteRepository.deleteNote(id, user.getEmail())) {
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
            // Проверка, что принимающий приглашение не в группе, хотя такого теоретически быть не должно
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
        recipient.setNewNotify(false);
        noteRepository.deleteAllNotesByUserEmailAndType(sender.getEmail(), NoteType.INVITE); // удалить все полученные приглашения у приглашающего
        sender.setNewNotify(false);
        noteRepository.deleteAllOwnInvites(recipient.getId(), NoteType.INVITE); // удалить все отправленные приглашения у приглашаемого

        userRepository.save(recipient); // сохранить пользователя
        userRepository.save(sender);
    }

}
