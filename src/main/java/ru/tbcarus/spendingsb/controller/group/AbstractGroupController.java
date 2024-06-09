package ru.tbcarus.spendingsb.controller.group;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tbcarus.spendingsb.model.Friend;
import ru.tbcarus.spendingsb.model.Note;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.GroupService;
import ru.tbcarus.spendingsb.service.UserService;

import java.util.List;
import java.util.Map;

@Slf4j
public class AbstractGroupController {

    @Autowired
    GroupService groupService;
    @Autowired
    UserService userService;

    protected List<User> getGroupUserList(User user) {
        log.info("get friends list by user {}", user.getId());
        return groupService.getGroupUserList(user);
    }

    protected Map<Integer, Friend> getFriendsMapId(User user) {
        log.info("get friends map by user {}", user.getId());
        return groupService.getFriendsMapId(user);
    }

    protected Note sendFriendInvite(User user, String email) {
        return groupService.sendFriendInvite(user, email.toLowerCase());
    }

    protected void levelDates(User user) {
        groupService.levelDates(user);
    }

    public void deleteUserFromGroup(User user, int id) {
        log.info("superuser {} delete user {} from group", user.getId(), id);
        groupService.deleteUserFromGroup(user, id);
    }

    public void deleteFromGroupSelf(User user) {
        log.info("user {} deleting from the group by self", user.getId());
        groupService.deleteFromGroupSelf(user);
    }

    protected void addSU(User user, int id) {
        log.info("superuser {} add SU role for user {}", user.getId(), id);
        groupService.addSU(user, id);
    }

    protected void removeSU(User user, int id) {
        log.info("superuser {} remove SU role for user {}", user.getId(), id);
        groupService.removeSU(user, id);
    }
}
