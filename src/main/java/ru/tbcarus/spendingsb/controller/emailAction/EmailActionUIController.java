package ru.tbcarus.spendingsb.controller.emailAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tbcarus.spendingsb.exception.BadRegistrationRequest;
import ru.tbcarus.spendingsb.model.EmailAction;
import ru.tbcarus.spendingsb.model.ErrorType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.EmailActionService;

import java.util.NoSuchElementException;

@Controller
@RequestMapping("/register")
public class EmailActionUIController extends AbstractEmailActionController {

    @Autowired
    EmailActionService emailActionService;

    //OK
    @GetMapping("/ACTIVATE")
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
            return "registerConfirm";
        }
        return "registerConfirm";
    }

    //OK
    @PostMapping("/password-reset-request")
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

    //OK
    @GetMapping("/RESET_PASSWORD")
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
    }

    //OK
    @PostMapping("/reset-password")
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

    //Ok
    @PostMapping("/resend-request")
    public String resendRequest(Model model, @RequestParam String email, @RequestParam(required = false) String code) {
        // Запрос новой ссылки, если период действия предыдущей истёк
        try {
            super.resendRequest(email);
        } catch (BadRegistrationRequest exc) {
            ErrorType type = exc.getErrorType();
            model.addAttribute("err", type);
            if (ErrorType.TOO_MUCH_REPEAT_REQUESTS.equals(type)) {
                return "redirect:/login?request=repeat-requests&username=" + email;
            }
        }
        return "redirect:/login?request=resend&username=" + email;
    }
}
