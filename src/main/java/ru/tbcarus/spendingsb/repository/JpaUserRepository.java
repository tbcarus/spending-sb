package ru.tbcarus.spendingsb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.tbcarus.spendingsb.model.Friend;
import ru.tbcarus.spendingsb.model.User;

import java.util.List;
import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u WHERE u.email = LOWER(:email)")
    User getByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findAllByOrderByName();

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.friendsList WHERE u.id=:userId")
    User getWithFriends(int userId);

}
