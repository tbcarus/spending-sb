package ru.tbcarus.spendingsb.controller.friend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tbcarus.spendingsb.model.Friend;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.FriendService;

@Slf4j
public class AbstractFriendController {

    @Autowired
    FriendService friendService;

    public Friend changeColor(int id, User user, String color) {
        log.info("Change color for id {} userId {} to {}", id, user.getId(), color);
        return friendService.changeColor(id, user, color);
    }
}
