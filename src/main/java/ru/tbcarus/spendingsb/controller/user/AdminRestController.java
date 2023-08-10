package ru.tbcarus.spendingsb.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.tbcarus.spendingsb.model.Role;
import ru.tbcarus.spendingsb.model.User;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping(value = AdminRestController.REST_URL)
public class AdminRestController extends AbstractUserController {
    static final String REST_URL = "/rest/admin/users";

    @GetMapping
    public List<User> getAll() {
        return super.getALL();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User get(@PathVariable int id) {
        return super.get(id);
    }

    @GetMapping("/by-email")
    public User getByEmail(@RequestParam String email) {
        return super.getByEmail(email);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        user.setRoles(EnumSet.of(Role.USER));
        return super.create(user);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User update(@PathVariable int id, @RequestBody User user) {
        return super.update(user, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }
}
