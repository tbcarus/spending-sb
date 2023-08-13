package ru.tbcarus.spendingsb.controller.payment;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbcarus.spendingsb.model.Payment;

import java.util.List;

@RestController
@RequestMapping(value = AdminPaymentRestController.REST_URL)
public class AdminPaymentRestController extends AbstractPaymentController {
    static final String REST_URL = "/rest/admin/payments";


    @GetMapping
    public List<Payment> getAll() {
        return super.getAll();
    }
}
