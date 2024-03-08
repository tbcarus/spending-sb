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
import ru.tbcarus.spendingsb.model.EmailAction;
import ru.tbcarus.spendingsb.model.ErrorType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.NoteService;
import ru.tbcarus.spendingsb.service.UserService;
import ru.tbcarus.spendingsb.util.DateUtil;

import java.time.LocalDate;
import java.util.List;
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
        super.changeStartDate(user, newLD);
        String[] uri = request.getHeader("referer").split("payments"); // Для перехода на страницу запроса при изменении начально даты
        return uri.length == 1 ? "redirect:/payments" : "redirect:/payments" + uri[1];
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String saveRegister(@Valid User user, BindingResult result, Model model, int startDay) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            User created = super.create(user, startDay);
        } catch (DataIntegrityViolationException exc) {
            ObjectError error = new ObjectError("globalError", "Повторяющийся e-mail");
            result.addError(error);
            return "register";
        }
        return "redirect:/login?request=registered&username=" + user.getEmail();
    }

    @GetMapping("/register/ACTIVATE")
    public String registerConfirm(Model model, @RequestParam String email, @RequestParam String code) {
        // Активация нового пользователя при переходе по ссылке
        try {
            EmailAction emailAction = super.registerConfirm(email, code);
            model.addAttribute("emailAction", emailAction);
        } catch (BadRegistrationRequest exc) {
            ErrorType type = exc.getErrorType();
            model.addAttribute("err", type);
            if (ErrorType.PERIOD_EXPIRED.equals(type)) {
                model.addAttribute("resendRequest", true);
            } else {
                model.addAttribute("resendRequest", false);
            }
//            ObjectError error = new ObjectError("globalError", "Запрос на регистрацию не найден");
//            result.addError(error);
            return "registerConfirm";
        }

        return "registerConfirm";
    }

    @PostMapping("/register/resend-request")
    public String resendRequest(Model model, @RequestParam String email, @RequestParam(required = false) String code) {
        // Запрос новой ссылки, если период действия предыдущей истёк
        try {
            super.resendRequest(email, code);
        } catch (BadRegistrationRequest exc) {
            ErrorType type = exc.getErrorType();
            model.addAttribute("err", type);
            if (ErrorType.TOO_MUCH_REPEAT_REQUESTS.equals(type)) {
                return "redirect:/login?request=repeat-requests&username=" + email;
            }
        }

        return "redirect:/login?request=resend&username=" + email;
    }

    @PostMapping("/register/password-reset-request")
    public String passwordReset(Model model, @RequestParam String email) {
        // Запрос ссылки на восстановление пароля по почте
        try {
            super.passwordResetRequest(email);
        } catch (NoSuchElementException exc) {
            return "redirect:/login?error=no-user-found&username=" + email;
        } catch (BadRegistrationRequest exc) {
            ErrorType type = exc.getErrorType();
            model.addAttribute("err", type);
            if (ErrorType.TOO_MUCH_REPEAT_REQUESTS.equals(type)) {
                return "redirect:/login?request=repeat-requests&username=" + email;
            }
        }
        return "redirect:/login?request=reset&username=" + email;
    }

    @GetMapping("/register/RESET_PASSWORD")
    public String passwordResetGet(Model model, @RequestParam String email, @RequestParam String code) {
        // Замена пароля при переходе по ссылке
        try {
            EmailAction emailAction = super.passwordResetGet(email, code);
            model.addAttribute("emailAction", emailAction);
        } catch (BadRegistrationRequest exc) {
            ErrorType type = exc.getErrorType();
            model.addAttribute("err", type);
            if (ErrorType.PERIOD_EXPIRED.equals(type)) {
                model.addAttribute("resendRequest", true);
            } else {
                model.addAttribute("resendRequest", false);
            }
        }
        return "resetPassword";

//        return "redirect:/login?request=reset&username=" + email;
    }

    @PostMapping("/register/reset-password")
    public String resetPassword(Model model,
                                String email,
                                String code,
                                String password,
                                String passwordReply) {
        // Смена пароля
        User user;
        try {
            user = super.resetPassword(email, code, password, passwordReply);
        } catch (BadRegistrationRequest exc) {
            ErrorType type = exc.getErrorType();
            model.addAttribute("err", type);
            if (ErrorType.DO_NOT_MATCH.equals(type)) {
                model.addAttribute("isDoNotMatch", true);
            } else {
                model.addAttribute("isDoNotMatch", false);
            }
            if (ErrorType.WRONG_LENGTH.equals(type)) {
                model.addAttribute("isWrongLength", true);
            } else {
                model.addAttribute("isWrongLength", false);
            }
            return "resetPassword";
        }
        model.addAttribute("user", user);
        return "redirect:/login?request=reset-success&username=" + user.getEmail();
    }

    @GetMapping("/addfriend")
    public String addFriend(Model model, @AuthenticationPrincipal User user) {
        // Что это за метод?
        model.addAttribute("user", user);
        return "addfriend";
    }

    @PostMapping("/addfriend")
    public String sendFriendInvite(Model model, @AuthenticationPrincipal User user, String email) {
        model.addAttribute("user", user);
        try {
            super.sendFriendInvite(user, email);
        } catch (IncorrectAddition exc) {
            switch (exc.getErrorType()) {
                case ALREADY_IN_GROUP:
                    model.addAttribute("err", ErrorType.ALREADY_IN_GROUP);
                    return "redirect:/payments/profile/error?type=" + ErrorType.ALREADY_IN_GROUP.name();
                case HAS_GROUP:
                    model.addAttribute("err", ErrorType.HAS_GROUP);
                    return "redirect:/payments/profile/error?type=" + ErrorType.HAS_GROUP.name();
                case TOO_MUCH_FRIENDS:
                    model.addAttribute("err", ErrorType.TOO_MUCH_FRIENDS);
                    return "redirect:/payments/profile/error?type=" + ErrorType.TOO_MUCH_FRIENDS.name();
                case TOO_MUCH_INVITES:
                    model.addAttribute("err", ErrorType.TOO_MUCH_INVITES);
                    return "redirect:/payments/profile/error?type=" + ErrorType.TOO_MUCH_INVITES.name();

            }
        }
        return "redirect:/payments";
    }

    @RequestMapping("/error")
    public String IncorrectAddition(@AuthenticationPrincipal User user, Model model, @RequestParam String type) {
        model.addAttribute("err", ErrorType.valueOf(type));
        model.addAttribute("user", user);
        return "error";
    }

    @RequestMapping("/group")
    public String getFriends(@AuthenticationPrincipal User user, Model model) {
        List<User> friends = super.getFriends(user);
        model.addAttribute("user", user);
        model.addAttribute("friends", friends);
        return "group";
    }

    @RequestMapping("/group/{id}/delete")
    // Удалить пользователя из группы. Может только суперпользователь
    public String deleteUserFromGroup(@AuthenticationPrincipal User user, @PathVariable int id, Model model) {
        super.deleteUserFromGroup(user, id);
        return "redirect:/payments/profile/group";
    }

    @RequestMapping("/group/delete")
    // Удалиться из группы самому
    public String deleteFromGroupSelf(@AuthenticationPrincipal User user, Model model) {
        super.deleteFromGroupSelf(user);
        return "redirect:/payments/profile/group";
    }

    @RequestMapping("/group/{id}/addSU")
    public String addSU(@AuthenticationPrincipal User user, @PathVariable int id, Model model) {
        super.addSU(user, id);
        return "redirect:/payments/profile/group";
    }

    @RequestMapping("/group/{id}/removeSU")
    public String removeSU(@AuthenticationPrincipal User user, @PathVariable int id, Model model) {
        super.removeSU(user, id);
        return "redirect:/payments/profile/group";
    }
}
