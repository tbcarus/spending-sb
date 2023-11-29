package ru.tbcarus.spendingsb.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.UserService;
import ru.tbcarus.spendingsb.util.DateUtil;
import ru.tbcarus.spendingsb.util.SecurityUtil;

import java.time.LocalDate;

@Controller
@RequestMapping(value = "/payments/profile", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ProfileController extends AbstractUserController {

    @Autowired
    UserService userService;

    @GetMapping
    public String get(Model model) {
        User user = super.get(SecurityUtil.authUserId());
        model.addAttribute("user", user);
        return "settings";
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
                                  HttpServletRequest request) {
        log.info("change start date");
        int id = SecurityUtil.authUserId();
        User user = super.get(id);
        LocalDate newLD = LocalDate.of(year, month, day);
        user.setStartPeriodDate(newLD);
        super.update(user, user.id());
        String[] uri = request.getHeader("referer").split("payments"); // Для перехода на страницу запроса при изменении начально даты
        return uri.length == 1 ? "redirect:/payments" : "redirect:/payments" + uri[1];

    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String saveRegister(@Valid User user, BindingResult result, Model model) {
        if(result.hasErrors()) {
            return "register";
        }

        User created = super.create(user);
        return "redirect:/login?registered&username=" + user.getName();
    }

//    @PostMapping("/register")
//    public String saveRegister(@RequestParam(value = "name", required = false) String name,
//                               @RequestParam(value = "email", required = false) String email,
//                               @RequestParam(value = "pass", required = false) String pass,
//                               @RequestParam(value = "start_day", required = false) int startDay) {
//        User user = new User(name, email, pass, LocalDate.now().withDayOfMonth(startDay));
//        super.create(user);
//        return "redirect:/login?registered&username=" + user.getEmail();
//    }
}
