package ru.tbcarus.spendingsb.controller.friend;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tbcarus.spendingsb.model.Friend;
import ru.tbcarus.spendingsb.model.User;

@RestController
@RequestMapping(value = FriendRestController.REST_URL)
public class FriendRestController extends AbstractFriendController{
    static final String REST_URL = "/rest/friends";

    @PostMapping("/{id}/change-color")
    public Friend changeColor(@PathVariable int id,
                              @RequestParam String color,
                              @AuthenticationPrincipal User user) {
        return super.changeColor(id, user, color);
    }

}
