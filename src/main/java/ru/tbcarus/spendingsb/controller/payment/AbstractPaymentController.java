package ru.tbcarus.spendingsb.controller.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;
import ru.tbcarus.spendingsb.service.PaymentService;
import ru.tbcarus.spendingsb.util.SecurityUtil;

import java.time.LocalDate;
import java.util.List;

@Slf4j
public abstract class AbstractPaymentController {
    @Autowired
    protected PaymentService paymentService;

    public List<Payment> getPayments(PaymentType type, Integer userId, LocalDate after, LocalDate before) {
        log.info("get filtered payments by type {}, userID {} and between {} - {}", type, userId, after, before);
        Specification<Payment> specification = Specification.where(paymentService.filterByType(type)
                .and(paymentService.filterByUser(userId))
                .and(paymentService.filterByDate(after, before)));
        return paymentService.getPayments(specification);
    }

    public Payment get(int id) {
        log.info("get payment {}", id);
        return paymentService.get(id, 100000);
    }

    public List<Payment> getAll() {
        log.info("get all payments");
        return paymentService.getAll();
    };

//    public List<Payment> getAllByUser(int userId) {
//        log.info("get all payments by user {}", userId);
//        return paymentService.getAllByUser(userId);
//    }

//    public List<Payment> getAllByUserAndDate(LocalDate after, LocalDate before, int userId) {
//        log.info("get all by user {} and date {}-{}", userId, after, before);
//        return paymentService.getBetween(after, before, userId);
//    }

//    public List<Payment> getAllByTypeBetween(PaymentType type, LocalDate after, LocalDate before, int userId) {
//        log.info("get all by type {}", type.getTitle());
//        return paymentService.getByTypeBetween(type, after, before, userId);
//    }

    public Payment create(Payment p) {
        int userId = SecurityUtil.authUserId();
        log.info("save payment {} by user {}", p, userId);
        return paymentService.create(p, userId);
    }

    public Payment update(int id, Payment p) {
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
