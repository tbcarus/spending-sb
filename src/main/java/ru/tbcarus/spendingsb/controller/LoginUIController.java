package ru.tbcarus.spendingsb.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tbcarus.spendingsb.controller.emailAction.AbstractEmailActionController;
import ru.tbcarus.spendingsb.service.EmailActionService;

@Controller
@RequestMapping("")
public class LoginUIController extends AbstractEmailActionController {

    @Autowired
    EmailActionService emailActionService;

    @GetMapping("/login-error")
    public String LoginError(HttpServletRequest request, Model model) {

        return "login-error";
    }

}
