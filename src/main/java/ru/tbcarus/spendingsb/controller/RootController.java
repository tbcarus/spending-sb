package ru.tbcarus.spendingsb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tbcarus.spendingsb.model.PaymentType;
import ru.tbcarus.spendingsb.service.UserService;
import ru.tbcarus.spendingsb.util.SecurityUtil;

@Slf4j
@Controller
public class RootController {
    @Autowired
    UserService userService;

    @GetMapping("/")
    public String root(Model model) {
        log.info("Get root");
        model.addAttribute("users", userService.getAll());
        return "index";
    }

    @PostMapping("/users")
    public String setUser(@RequestParam int userId) {
        log.info("setUser {}", userId);
        SecurityUtil.setAuthUserId(userId);
        return "redirect:/payments";
    }

    @GetMapping("/list")
    public String list() {
        log.info("Get list");
        return "list";
    }
    @GetMapping("/typedList")
    public String typedList(@RequestParam PaymentType type) {
        log.info("Get typed list {}", type);
        return "typedList";
    }
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id) {
        log.info("Edit payment {}", id);
        return "edit";
    }
    @GetMapping("/settings")
    public String settings() {
        log.info("Get settings");
        return "settings";
    }
}
