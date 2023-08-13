package ru.tbcarus.spendingsb.controller.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.service.PaymentService;

import java.util.List;

@Slf4j
public abstract class AbstractPaymentController {
    @Autowired
    private PaymentService paymentService;

    public Payment get() {

    }

    public List<Payment> getAll() {
        log.info("get all payments");
        return paymentService.getAll();
    };

    public List<Payment> getAllByUser() {

    }

    public List<Payment> getAllByType() {

    }

    public Payment save(Payment p) {

    }

    public Payment update(Payment p) {

    }

    public void delete(int id) {

    }
}
