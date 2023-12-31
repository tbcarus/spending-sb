package ru.tbcarus.spendingsb.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.repository.DataJpaPaymentRepository;
import ru.tbcarus.spendingsb.repository.JpaUserRepository;
import ru.tbcarus.spendingsb.util.SecurityUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class PaymentService {
    @Autowired
    DataJpaPaymentRepository paymentRepository;
    @Autowired
    JpaUserRepository userRepository;
    @Autowired
    UserService userService;

    Sort sort = Sort.by(Sort.Direction.DESC, "date", "userID", "price");

    public List<Payment> getPayments(Specification<Payment> specification) {
        return sortList(paymentRepository.getPayments(specification, sort));
    }

    public Payment get(int id, int userId) {
        Payment payment = paymentRepository.get(id, userId);
        if (payment == null) {
            throw new NotFoundException();
        }
        return payment;
    }

    public List<Payment> getAll() {
        return sortList(paymentRepository.getAll());
    }

    public Payment create(Payment payment, int userId) {
        payment.setUserID(userId);
        payment.setUser(userService.getById(SecurityUtil.authUserId()));
        return paymentRepository.save(payment, userId);
    }

    public List<Payment> createAll(List<Payment> list, int userId) {
        User user = userService.getById(SecurityUtil.authUserId());
        list.forEach(p -> p.setUserID(userId));
        list.forEach(p -> p.setUser(user));
        return paymentRepository.saveAll(list, userId);
    }

    public Payment update(Payment payment, int userId) {
        payment.setUserID(userId);
        payment.setUser(userService.getById(SecurityUtil.authUserId()));
        return paymentRepository.save(payment, userId);
    }

    public void delete(int id, int userId) {
        if (!paymentRepository.delete(id, userId)) {
            throw new NotFoundException();
        }
    }

    public Specification<Payment> filterByType(PaymentType type) {
        return new Specification<Payment>() {
            @Override
            public Predicate toPredicate(Root<Payment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (type == null) {
                    return null;
                }
                return criteriaBuilder.equal(root.get("type"), type);
            }
        };
    }

    public Specification<Payment> filterByUserId(Integer userId) {
        return new Specification<Payment>() {
            @Override
            public Predicate toPredicate(Root<Payment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (userId == null) {
                    return null;
                }
                return criteriaBuilder.equal(root.get("userID"), userId);
            }
        };
    }

    public Specification<Payment> filterByDate(LocalDate after, LocalDate before) {
        return new Specification<Payment>() {
            @Override
            public Predicate toPredicate(Root<Payment> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                LocalDate tempAfter = after == null ? LocalDate.parse("1900-01-01") : after;
                LocalDate tempBefore = before == null ? LocalDate.parse("3000-01-01") : before;
                return criteriaBuilder.between(root.get("date"), tempAfter, tempBefore);
            }
        };
    }

    private List<Payment> sortList(List<Payment> list) {
        Collections.sort(list);
        return list;
    }

//
//    public int getSumType(PaymentType type, int userId) {
//
//    }
}
