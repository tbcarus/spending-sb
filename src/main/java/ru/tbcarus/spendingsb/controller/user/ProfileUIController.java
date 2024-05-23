package ru.tbcarus.spendingsb.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import ru.tbcarus.spendingsb.exception.BadRegistrationRequest;
import ru.tbcarus.spendingsb.exception.IncorrectAddition;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.EmailAction;
import ru.tbcarus.spendingsb.model.ErrorType;
import ru.tbcarus.spendingsb.model.Friend;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.NoteService;
import ru.tbcarus.spendingsb.service.UserService;
import ru.tbcarus.spendingsb.util.DateUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Controller
@RequestMapping(value = "/payments/profile", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ProfileUIController extends AbstractUserController {

    @Autowired
    UserService userService;

    @Autowired
    NoteService noteService;

    @GetMapping
    public String get(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        return "settings";
    }

    @PostMapping
    public String update(@RequestParam("name") String name,
                         @RequestParam("start_day") int startDay,
                         @RequestParam("old_pass") String passOld,
                         @RequestParam("new_pass") String passNew,
                         @RequestParam("new_pass_reply") String passNewReply,
                         @AuthenticationPrincipal User user) {
        if (!name.isEmpty()) {
            user.setName(name);
        }
        LocalDate ld = DateUtil.setDay(startDay, user.getStartPeriodDate());
        user.setStartPeriodDate(ld);
        if (user.getPassword().equals(passOld) && passNew.equals(passNewReply)) {
            user.setPassword(passNew);
        }
        super.update(user, user.id());
        int x = 5;
        return "redirect:/payments";
    }

    @PostMapping("/changeStartDate")
    public String changeStartDate(@RequestParam("start_day") int day,
                                  @RequestParam("start_month") int month,
                                  @RequestParam("start_year") int year,
                                  HttpServletRequest request,
                                  @AuthenticationPrincipal User user) {
        LocalDate newLD = LocalDate.of(year, month, day);
        user = userService.getByIdWithFriends(user.getId());
        super.changeStartDate(user, newLD);
        String[] uri = request.getHeader("referer").split("payments"); // Для перехода на страницу запроса при изменении начально даты
        return uri.length == 1 ? "redirect:/payments" : "redirect:/payments" + uri[1];
    }

    @GetMapping("/addfriend")
    public String addFriend(Model model, @AuthenticationPrincipal User user) {
        // Что это за метод?
        model.addAttribute("user", user);
        return "addfriend";
    }

    @PostMapping("/addfriend")
    public String sendFriendInvite(Model model, @AuthenticationPrincipal User user, String email) {
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

    @RequestMapping("/error")
    public String IncorrectAddition(@AuthenticationPrincipal User user, Model model, @RequestParam String type) {
        user = userService.getByIdWithFriends(user.getId());
        model.addAttribute("err", ErrorType.valueOf(type).getTitle());
        model.addAttribute("user", user);
        return "error";
    }

    @RequestMapping("/group")
    public String getFriends(@AuthenticationPrincipal User user, Model model) {
        user = userService.getByIdWithFriends(user.getId());
        List<User> friends = super.getFriends(user);
        Map<Integer, Friend> friendsMapId = super.getFriendsMapId(user);
        model.addAttribute("user", user);
        model.addAttribute("friends", friends);
        model.addAttribute("friendsMapId", friendsMapId);
        return "group";
    }

    @GetMapping("/group/level-dates")
    public String levelDates(@AuthenticationPrincipal User user, Model model) {
        user = userService.getByIdWithFriends(user.getId());
        super.levelDates(user);
        return "redirect:/payments/profile/group";
    }

    @RequestMapping("/group/{id}/delete")
    // Удалить пользователя из группы. Может только суперпользователь
    public String deleteUserFromGroup(@AuthenticationPrincipal User user, @PathVariable int id, Model model) {
        user = userService.getByIdWithFriends(user.getId());
        super.deleteUserFromGroup(user, id);
        return "redirect:/payments/profile/group";
    }

    @RequestMapping("/group/delete")
    // Удалиться из группы самому
    public String deleteFromGroupSelf(@AuthenticationPrincipal User user, Model model) {
        user = userService.getByIdWithFriends(user.getId());
        super.deleteFromGroupSelf(user);
        return "redirect:/payments/profile/group";
    }

    @RequestMapping("/group/{id}/addSU")
    public String addSU(@AuthenticationPrincipal User user, @PathVariable int id, Model model) {
        user = userService.getByIdWithFriends(user.getId());
        super.addSU(user, id);
        return "redirect:/payments/profile/group";
    }

    @RequestMapping("/group/{id}/removeSU")
    public String removeSU(@AuthenticationPrincipal User user, @PathVariable int id, Model model) {
        user = userService.getByIdWithFriends(user.getId());
        super.removeSU(user, id);
        return "redirect:/payments/profile/group";
    }
}
