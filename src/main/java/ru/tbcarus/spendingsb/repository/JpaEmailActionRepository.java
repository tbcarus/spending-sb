package ru.tbcarus.spendingsb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.tbcarus.spendingsb.model.EmailAction;

import java.util.Optional;

@Transactional
public interface JpaEmailActionRepository extends JpaRepository<EmailAction, Integer> {

    EmailAction getByCode(String code);

    Optional<EmailAction> findByCode(String code);
}
