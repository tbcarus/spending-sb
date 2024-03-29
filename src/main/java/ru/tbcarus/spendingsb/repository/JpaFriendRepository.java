package ru.tbcarus.spendingsb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbcarus.spendingsb.model.Friend;

import java.util.List;

public interface JpaFriendRepository extends JpaRepository<Friend, Integer> {

    List<Friend> findAllByUserId(Integer userId);
}
