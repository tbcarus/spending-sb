package ru.tbcarus.spendingsb.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.UserService;
import ru.tbcarus.spendingsb.util.DateUtil;
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
        User user = super.get(SecurityUtil.authUserId());
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

    @PostMapping
    public String update(@RequestParam("name") String name,
                         @RequestParam("start_day") int startDay,
                         @RequestParam("old_pass") String passOld,
                         @RequestParam("new_pass") String passNew,
                         @RequestParam("new_pass_reply") String passNewReply) {
        int id = SecurityUtil.authUserId();
        User user = super.get(id);
        if (!name.isEmpty()) {
            user.setName(name);
        }
        LocalDate ld = DateUtil.setDay(startDay, user.getStartPeriodDate());
        user.setStartPeriodDate(ld);
        if(user.getPassword().equals(passOld) && passNew.equals(passNewReply)) {
            user.setPassword(passNew);
        }
        super.update(user, user.id());
        int x = 5;
        return "redirect:/payments";
    }

    @GetMapping("/delete")
    public String deleteStr(@RequestParam String id, HttpServletRequest request) {
        super.delete(getId(id));
        return "users";
    }

    private int getId(String id) {
        String paramId = Objects.requireNonNull(id);
        return Integer.parseInt(paramId);
    }
}
