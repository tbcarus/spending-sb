package ru.tbcarus.spendingsb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.tbcarus.spendingsb.model.EmailAction;
import ru.tbcarus.spendingsb.model.EmailRequestType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
public interface JpaEmailActionRepository extends JpaRepository<EmailAction, Integer> {

    EmailAction getByCode(String code);

    Optional<EmailAction> findByCode(String code);

    List<EmailAction> findAllByUserIdAndDateTimeBetweenAndTypeOrderByDateTimeDesc(Integer userId,
                                                                                  LocalDateTime after,
                                                                                  LocalDateTime before,
                                                                                  EmailRequestType type);
}
