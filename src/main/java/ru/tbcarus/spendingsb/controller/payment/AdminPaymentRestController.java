package ru.tbcarus.spendingsb.controller.payment;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = AdminPaymentRestController.REST_URL)
public class AdminPaymentRestController extends AbstractPaymentController {
    static final String REST_URL = "/rest/admin/payments";

    @GetMapping("/{id}")
    public Payment get(@PathVariable int id) {
        return super.get(id);
    }

    @GetMapping
    public List<Payment> getAll() {
        return super.getAll();
    }

    @GetMapping("/by")
    public List<Payment> getPaymentsByTypeUserIdBetween(@RequestParam(required = false) PaymentType type,
                                                        @RequestParam(required = false) Integer userId,
                                                        @RequestParam(required = false) LocalDate after,
                                                        @RequestParam(required = false) LocalDate before) {
        return super.getPaymentsByTypeUserIdBetween(type, userId, after, before);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Payment create(@RequestBody Payment payment) {
        return super.create(payment);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Payment update(@RequestBody Payment payment, @PathVariable int id) {
        return super.update(payment, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

}
