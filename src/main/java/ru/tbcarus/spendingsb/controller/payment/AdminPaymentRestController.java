package ru.tbcarus.spendingsb.controller.payment;

import org.springframework.data.jpa.domain.Specification;
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
    public List<Payment> getPayments(@RequestParam(required = false) PaymentType type,
                                      @RequestParam(required = false) int userId,
                                      @RequestParam(required = false) LocalDate after,
                                      @RequestParam(required = false) LocalDate before) {
        return super.getPayments(type, userId, after, before);
    }


}
