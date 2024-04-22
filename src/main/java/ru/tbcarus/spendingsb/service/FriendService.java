package ru.tbcarus.spendingsb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.Friend;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.repository.JpaFriendRepository;

import java.util.Optional;

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

    public Friend changeColor(int id, User user, String color) {
        Optional<Friend> opt = friendRepository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException("Friend " + id + " not found");
        }
        Friend f = opt.get();
        if (!f.getUserEmail().equals(user.getEmail())) {
            throw new NotFoundException("Friend " + id + " is not your friend");
        }
        f.setColor(color);
        return friendRepository.save(f);
    }

}
