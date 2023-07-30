package ru.tbcarus.spendingsb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbcarus.spendingsb.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    User getByEmail(String email);

}
