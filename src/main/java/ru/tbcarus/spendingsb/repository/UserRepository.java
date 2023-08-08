package ru.tbcarus.spendingsb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbcarus.spendingsb.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    User getByEmail(String email);

    List<User> findAllByOrderByName();

}
