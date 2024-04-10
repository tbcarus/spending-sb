package ru.tbcarus.spendingsb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tbcarus.spendingsb.repository.JpaFriendRepository;

@Service
@Slf4j
public class FriendService {

    @Autowired
    JpaFriendRepository friendRepository;

    public void deleteFriend(int userId, int friendId) {
        log.info("delete friend {} by user {}", friendId, userId);
        friendRepository.deleteByUserIdAndFriendId(userId, friendId);
    }

    public void deleteAll(int userId) {
        friendRepository.deleteAllByUserId(userId);
    }

}
