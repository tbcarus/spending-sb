package ru.tbcarus.spendingsb.controller.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbcarus.spendingsb.model.Note;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.UserService;

@RestController
@RequestMapping("/rest/group")
public class GroupRestController extends AbstractGroupController {
    @Autowired
    UserService userService;

    @PostMapping("/addfriend")
    public Note sendFriendInvite(Model model, @AuthenticationPrincipal User user, String email) {
        user = userService.getByIdWithFriends(user.getId());
        return super.sendFriendInvite(user, email);
    }
}
