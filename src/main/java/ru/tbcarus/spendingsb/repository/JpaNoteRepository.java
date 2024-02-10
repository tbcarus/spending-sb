package ru.tbcarus.spendingsb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.tbcarus.spendingsb.model.Note;
import ru.tbcarus.spendingsb.model.NoteType;

import java.util.List;

@Transactional
public interface JpaNoteRepository extends JpaRepository<Note, Integer>, JpaSpecificationExecutor<Note> {

    List<Note> findAllByEmailOrderByDateTime(String email);

    @Query("SELECT n FROM Note n JOIN FETCH n.user u WHERE n.email=:email")
    List<Note> getAllByEmail(@Param("email") String email);

    List<Note> getAllByUserId(int userId);

    @Query("SELECT n FROM Note n JOIN FETCH n.user u WHERE n.id=:noteId AND n.email=:email")
    Note getNoteWithUser(@Param("noteId") int noteId, @Param("email") String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM Note n WHERE n.id=:id AND n.email=:email")
    int deleteNote(@Param("id") int id, @Param("email") String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM Note n WHERE n.email=:email")
    int deleteAllNotesByUserEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM Note n WHERE n.email=:email AND n.type=:type")
    int deleteAllNotesByUserEmailAndType(@Param("email") String email, @Param("type") NoteType type);

    @Modifying
    @Transactional
    @Query("DELETE FROM Note n WHERE n.id=:noteId AND n.user.id=:userId")
    int deleteOwnInvite(@Param("noteId") int noteId, @Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Note n WHERE n.user.id=:userId")
    int deleteAllOwnNotes(@Param("userId") int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Note n WHERE n.user.id=:userId AND n.type=:type")
    int deleteAllOwnInvites(@Param("userId") int userId, @Param("type") NoteType type);
}
