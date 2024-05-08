package ru.tbcarus.spendingsb.controller.payment;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;
import ru.tbcarus.spendingsb.model.User;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = AdminPaymentRestController.REST_URL)
public class AdminPaymentRestController extends AbstractPaymentController {
    static final String REST_URL = "/rest/admin/payments";

    @GetMapping("/{id}")
    public Payment get(@AuthenticationPrincipal User user, @PathVariable int id) {
        return super.get(user, id);
    }

    @GetMapping
    public List<Payment> getAll() {
        return super.getAll();
    }

    @GetMapping("/by")
    public List<Payment> getPaymentsByTypeUserIdBetween(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) PaymentType type,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) LocalDate after,
            @RequestParam(required = false) LocalDate before) {
        return super.getPaymentsByTypeUserBetween(user, type, after, before);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Payment create(@RequestBody Payment payment) {
        return super.create(payment);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Payment update(User user, @RequestBody Payment payment, @PathVariable int id) {
        return super.update(user, payment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal User user, @PathVariable int id) {
        super.delete(user, id);
    }

}
