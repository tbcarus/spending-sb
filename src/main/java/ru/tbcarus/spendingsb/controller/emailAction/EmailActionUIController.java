package ru.tbcarus.spendingsb.controller.emailAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tbcarus.spendingsb.service.EmailActionService;

@Controller
@RequestMapping("/profile")
public class EmailActionUIController extends AbstractEmailActionController {

    @Autowired
    EmailActionService emailActionService;

//    @GetMapping("/avtivate")
//    public String activate(Model model, @RequestParam String email) {
//        super.createActivate(email);
//        model.addAttribute("email", email);
//        return "emailAction";
//    }

//    @GetMapping("/reset-password")
//    public String ResetPasswordRequest(Model model, @RequestParam String email) {
//        EmailAction emailAction = super.createResetPassword(email);
//        model.addAttribute("email", email);
//        return "emailAction";
//    }
//
//    @PostMapping("/reset-password")
//    public String ResetPassword(Model model, @RequestParam String email, @RequestParam String code) {
//        EmailAction emailAction = super.createResetPassword(email);
//        model.addAttribute("email", email);
//        return "emailAction";
//    }
}
