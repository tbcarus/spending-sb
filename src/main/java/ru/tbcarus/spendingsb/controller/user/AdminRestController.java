package ru.tbcarus.spendingsb.controller.user;

import org.springframework.web.bind.annotation.*;
import ru.tbcarus.spendingsb.model.User;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/admin/users")
public class AdminRestController extends AbstractUserController {

    @GetMapping
    public List<User> getAll() {
        return super.getALL();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable int id) {
        return super.get(id);
    }

    @GetMapping("/by-email")
    public User getByEmail(@RequestParam String email) {
        return super.getByEmail(email);
    }

}
