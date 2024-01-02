package ru.tbcarus.spendingsb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.tbcarus.spendingsb.model.Note;

import java.util.List;

@Transactional
public interface JpaNoteRepository extends JpaRepository<Note, Integer>, JpaSpecificationExecutor<Note> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Note n WHERE n.id=:id AND n.email=:email")
    int delete(@Param("id") int id, @Param("email") String email);

    List<Note> findAllByEmailOrderByDateTime(String email);

    @Query("SELECT n FROM Note n JOIN FETCH n.user u WHERE n.email=:email")
    List<Note> getAllByEmail(@Param("email") String email);

}
