package ru.tbcarus.spendingsb.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tbcarus.spendingsb.model.ErrorType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.NoteService;
import ru.tbcarus.spendingsb.service.UserService;
import ru.tbcarus.spendingsb.util.DateUtil;

import java.time.LocalDate;

@Controller
@RequestMapping(value = "/payments/profile", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ProfileUIController extends AbstractUserController {

    @Autowired
    UserService userService;

    @Autowired
    NoteService noteService;

    @Autowired
    PasswordEncoder encoder;

    @GetMapping
    public String get(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        return "settings";
    }

    @PostMapping
    public String update(@RequestParam("id") int id,
                         @RequestParam("name") String name,
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
        if (encoder.matches(passOld, user.getPassword()) && passNew.equals(passNewReply)) {
            user.setPassword(passNew);
        }
        super.update(user, user.id());
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

    @RequestMapping("/error")
    public String IncorrectAddition(@AuthenticationPrincipal User user, Model model, @RequestParam String type) {
        user = userService.getByIdWithFriends(user.getId());
        model.addAttribute("err", ErrorType.valueOf(type).getTitle());
        model.addAttribute("user", user);
        return "error";
    }
}
