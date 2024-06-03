package ru.tbcarus.spendingsb.controller.group;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbcarus.spendingsb.exception.IncorrectAddition;
import ru.tbcarus.spendingsb.model.Note;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.UserService;

@RestController
@RequestMapping("/rest/group")
public class GroupRestController extends AbstractGroupController {
    @Autowired
    UserService userService;

    @PostMapping("/addfriend")
    public ResponseEntity<?> sendFriendInvite(Model model, @AuthenticationPrincipal User user, String email) {
        user = userService.getByIdWithFriends(user.getId());
        Note note = null;
        try {
            note = super.sendFriendInvite(user, email);
        } catch (IncorrectAddition e) {
            return new ResponseEntity<>(e.errorType.getTitle(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(note, HttpStatus.OK);
    }
}
