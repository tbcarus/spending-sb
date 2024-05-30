package ru.tbcarus.spendingsb.controller.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tbcarus.spendingsb.exception.IncorrectAddition;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.ErrorType;
import ru.tbcarus.spendingsb.model.Friend;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.UserService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/group")
public class GroupUIController extends AbstractGroupController {
    @Autowired
    UserService userService;

    @RequestMapping()
    public String getFriends(@AuthenticationPrincipal User user, Model model) {
        user = userService.getByIdWithFriends(user.getId());
        List<User> friends = super.getGroupUserList(user);
        Map<Integer, Friend> friendsMapId = super.getFriendsMapId(user);
        model.addAttribute("user", user);
        model.addAttribute("friends", friends);
        model.addAttribute("friendsMapId", friendsMapId);
        return "group";
    }

    @PostMapping("/addfriend")
    public String sendFriendInvite(Model model, @AuthenticationPrincipal User user, @RequestParam String email) {
        user = userService.getByIdWithFriends(user.getId());
        model.addAttribute("user", user);
        try {
            super.sendFriendInvite(user, email);
        } catch (IncorrectAddition exc) {
            switch (exc.getErrorType()) {
                case ALREADY_IN_GROUP:
                    model.addAttribute("err", ErrorType.ALREADY_IN_GROUP.getTitle());
//                    return "redirect:/payments/profile/error?type=" + ErrorType.ALREADY_IN_GROUP.name();
                    return "error";
                case HAS_GROUP:
                    model.addAttribute("err", ErrorType.HAS_GROUP.getTitle());
//                    return "redirect:/payments/profile/error?type=" + ErrorType.HAS_GROUP.name();
                    return "error";
                case TOO_MUCH_FRIENDS:
                    model.addAttribute("err", ErrorType.TOO_MUCH_FRIENDS.getTitle());
//                    return "redirect:/payments/profile/error?type=" + ErrorType.TOO_MUCH_FRIENDS.name();
                    return "error";
                case TOO_MUCH_INVITES:
                    model.addAttribute("err", ErrorType.TOO_MUCH_INVITES.getTitle());
//                    return "redirect:/payments/profile/error?type=" + ErrorType.TOO_MUCH_INVITES.name();
                    return "error";
            }
        } catch (NotFoundException exc) {
            model.addAttribute("err", exc.getMessage());
            return "error";
        }
        return "redirect:/payments";
    }

    @GetMapping("/level-dates")
    public String levelDates(@AuthenticationPrincipal User user, Model model) {
        user = userService.getByIdWithFriends(user.getId());
        super.levelDates(user);
        return "redirect:/group";
    }

    @RequestMapping("/{id}/delete")
    // Удалить пользователя из группы. Может только суперпользователь
    public String deleteUserFromGroup(@AuthenticationPrincipal User user, @PathVariable int id, Model model) {
        user = userService.getByIdWithFriends(user.getId());
        super.deleteUserFromGroup(user, id);
        return "redirect:/group";
    }

    @RequestMapping("/delete")
    // Удалиться из группы самому
    public String deleteFromGroupSelf(@AuthenticationPrincipal User user, Model model) {
        user = userService.getByIdWithFriends(user.getId());
        super.deleteFromGroupSelf(user);
        return "redirect:/payments";
    }

    @RequestMapping("/{id}/addSU")
    public String addSU(@AuthenticationPrincipal User user, @PathVariable int id, Model model) {
        user = userService.getByIdWithFriends(user.getId());
        super.addSU(user, id);
        return "redirect:/group";
    }

    @RequestMapping("/{id}/removeSU")
    public String removeSU(@AuthenticationPrincipal User user, @PathVariable int id, Model model) {
        user = userService.getByIdWithFriends(user.getId());
        super.removeSU(user, id);
        return "redirect:/group";
    }
}
