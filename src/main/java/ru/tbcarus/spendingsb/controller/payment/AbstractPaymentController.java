package ru.tbcarus.spendingsb.controller.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.PaymentService;
import ru.tbcarus.spendingsb.util.SecurityUtil;
import ru.tbcarus.spendingsb.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public abstract class AbstractPaymentController {
    @Autowired
    protected PaymentService paymentService;

    public Payment get(User user, int payId) {
        log.info("get payment {}", payId);
        int userId = SecurityUtil.authUserId();
        return paymentService.get(user, payId);
    }

    public List<Payment> getAll() {
        log.info("get all payments");
        return paymentService.getAll();
    };

    public List<Payment> getAllByUserId(User user) {
        log.info("get all by user {}", user.getId());
        Specification<Payment> specification = Specification.where(paymentService.filterByUserId(user.getId()));
        return paymentService.getPayments(user, LocalDate.of(2000, 1, 1), LocalDate.now());
    }

    public List<Payment> getAllByUserIdAndDateBetween(User user, int userId, LocalDate after, LocalDate before) {
        log.info("get all by user {} and date {}-{}", userId, after, before);
        return paymentService.getPayments(user, after, before);
    }

    public List<Payment> getPaymentsByTypeUserIdBetween(User user, PaymentType type, Integer userId, LocalDate after, LocalDate before) {
        log.info("get filtered payments by type {}, userID {} and between {} - {}", type, userId, after, before);
        return paymentService.getPaymentsByType(user, type, after, before);
    }

    public Payment create(Payment p) {
        int userId = SecurityUtil.authUserId();
        log.info("save payment {} by user {}", p, userId);
        ValidationUtil.checkNew(p);
        return paymentService.create(p, userId);
    }
    public List<Payment> createAll(List<Payment> list) {
        int userId = SecurityUtil.authUserId();
        log.info("save all payments {} by user {}", list, userId);
        return paymentService.createAll(list, userId);
    }

    public Payment update(User user, Payment p) {
        log.info("update payment {} by user {}", p, user.getId());
        return paymentService.update(user, p);
    }

    public void delete(User user, int id) {
        int userId = SecurityUtil.authUserId();
        log.info("delete payment {} for user {}", id, user.getId());
        paymentService.delete(user, id);
    }
}
