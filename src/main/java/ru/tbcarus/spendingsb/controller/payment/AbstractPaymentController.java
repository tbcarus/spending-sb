package ru.tbcarus.spendingsb.controller.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;
import ru.tbcarus.spendingsb.service.PaymentService;
import ru.tbcarus.spendingsb.util.SecurityUtil;
import ru.tbcarus.spendingsb.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public abstract class AbstractPaymentController {
    @Autowired
    protected PaymentService paymentService;

    public Payment get(int id) {
        log.info("get payment {}", id);
        int userId = SecurityUtil.authUserId();
        return paymentService.get(id, userId);
    }

    public List<Payment> getAll() {
        log.info("get all payments");
        return paymentService.getAll();
    };

    public List<Payment> getAllByUserId(int userId) {
        log.info("get all by user {}", userId);
        Specification<Payment> specification = Specification.where(paymentService.filterByUserId(userId));
        return paymentService.getPayments(specification);
    }

    public List<Payment> getAllByUserIdAndDateBetween(int userId, LocalDate after, LocalDate before) {
        log.info("get all by user {} and date {}-{}", userId, after, before);
        Specification<Payment> specification = Specification.where(paymentService.filterByUserId(userId)
        .and(paymentService.filterByDate(after, before)));
        return paymentService.getPayments(specification);
    }

    public List<Payment> getPaymentsByTypeUserIdBetween(PaymentType type, Integer userId, LocalDate after, LocalDate before) {
        log.info("get filtered payments by type {}, userID {} and between {} - {}", type, userId, after, before);
        Specification<Payment> specification = Specification.where(paymentService.filterByType(type)
                .and(paymentService.filterByUserId(userId))
                .and(paymentService.filterByDate(after, before)));
        return paymentService.getPayments(specification);
    }

    public Payment create(Payment p) {
        int userId = SecurityUtil.authUserId();
        log.info("save payment {} by user {}", p, userId);
        ValidationUtil.checkNew(p);
        return paymentService.create(p, userId);
    }

    public Payment update(Payment p, int id) {
        int userId = SecurityUtil.authUserId();
        log.info("update payment {} by user {}", p, userId);
        return paymentService.update(p, userId);
    }

    public void delete(int id) {
        int userId = SecurityUtil.authUserId();
        log.info("delete payment {} for user {}", id, userId);
        paymentService.delete(id, userId);
    }
}
