package ru.tbcarus.spendingsb.controller.register;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.NoteService;
import ru.tbcarus.spendingsb.service.UserService;

@Controller
@RequestMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class RegisterUIController extends AbstractRegisterController {

    @Autowired
    UserService userService;

    @Autowired
    NoteService noteService;

    //OK
    @GetMapping()
    public String register(Model model) {
        model.addAttribute("user", super.getRegister());
        return "register";
    }

    //OK
    @PostMapping()
    public String saveRegister(@Valid User user, BindingResult result, Model model, int startDay) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            User created = super.createRegister(user, startDay);
        } catch (DataIntegrityViolationException exc) {
            ObjectError error = new ObjectError("globalError", "Повторяющийся e-mail");
            result.addError(error);
            return "register";
        }
        return "redirect:/login?request=registered&username=" + user.getEmail();
    }
}
