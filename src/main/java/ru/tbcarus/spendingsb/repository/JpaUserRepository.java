package ru.tbcarus.spendingsb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.tbcarus.spendingsb.model.User;

import java.util.List;
import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    User getByEmail(String email);

    Optional<User> findByEmail(String email);

    List<User> findAllByOrderByName();

}
