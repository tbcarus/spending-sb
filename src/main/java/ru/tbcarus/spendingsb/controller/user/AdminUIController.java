package ru.tbcarus.spendingsb.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tbcarus.spendingsb.model.Role;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.UserService;
import ru.tbcarus.spendingsb.util.SecurityUtil;

import java.time.LocalDate;
import java.util.Objects;

@Controller
@RequestMapping(value = "/admin/users")
@Slf4j
public class AdminUIController extends AbstractUserController {

    @Autowired
    UserService userService;

    @GetMapping("/{id}")
    public String get(Model model, @PathVariable int id) {
        User user = super.get(id);
        model.addAttribute("user", user);
        return "userForm";
    }

    @GetMapping()
    public String getUsers(Model model) {
        User user = super.get(SecurityUtil.authUserId());
        model.addAttribute("user", user);
        model.addAttribute("users", userService.getAll());
        return "users";
    }

    @GetMapping("/create")
    public String addUser(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "userForm";
    }

    @PostMapping()
    public String createOrUpdate(Model model, HttpServletRequest request,
                                 @RequestParam("id") String idStr,
                                 @RequestParam("name") String name,
                                 @RequestParam("email") String email,
                                 @RequestParam("password") String password,
                                 @RequestParam("start_day") String startDay,
                                 @RequestParam(value = "enbld", required = false) String enabled,
                                 @RequestParam(value = "userCheckB", required = false) String userCH,
                                 @RequestParam(value = "superUserCheckB", required = false) String superUserCH,
                                 @RequestParam(value = "adminCheckB", required = false) String adminCH) {
        int day = 1;
        try {
            day = Integer.parseInt(startDay);
        } catch (NumberFormatException exc) {
            log.warn("startDay is not integer and = {}", startDay);
        }
        User user = new User(name, email, password, LocalDate.now().withDayOfMonth(day));
        if ("on".equals(enabled)) {
            user.setEnabled();
        } else {
            user.setDisabled();
        }
        if ("on".equals(userCH)) {
            user.addRole(Role.USER);
        }
        if ("on".equals(superUserCH)) {
            user.addRole(Role.SUPERUSER);
        }
        if ("on".equals(adminCH)) {
            user.addRole(Role.ADMIN);
        }
        if (idStr.isEmpty()) {
            super.create(user);
        } else {
            super.update(user, Integer.parseInt(idStr));
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/delete")
    public String deleteStr(@RequestParam String id, HttpServletRequest request) {
        super.delete(getId(id));
        return "redirect:/admin/users";
    }

    private int getId(String id) {
        String paramId = Objects.requireNonNull(id);
        return Integer.parseInt(paramId);
    }
}
