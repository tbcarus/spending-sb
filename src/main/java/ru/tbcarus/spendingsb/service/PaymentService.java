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
import ru.tbcarus.spendingsb.repository.DataJpaPaymentRepository;
import ru.tbcarus.spendingsb.repository.JpaUserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class PaymentService {
    @Autowired
    DataJpaPaymentRepository paymentRepository;
    @Autowired
    JpaUserRepository userRepository;

    Sort sort = Sort.by(Sort.Direction.DESC, "date", "userID", "price");

    public List<Payment> getPayments(Specification<Payment> specification) {
        return paymentRepository.getPayments(specification, sort);
    }

    public Payment get(int id, int userId) {
        Payment payment = paymentRepository.get(id, userId);
        if (payment == null) {
            throw new NotFoundException();
        }
        return payment;
    }

    public List<Payment> getAll() {
        return paymentRepository.getAll();
    }

    public Payment create(Payment payment, int userId) {
        payment.setUserID(userId);
        payment.setUser(userRepository.getByEmail("l2@og.in"));
        return paymentRepository.save(payment, userId);
    }

    public Payment update(Payment payment, int userId) {
        payment.setUserID(userId);
        payment.setUser(userRepository.getByEmail("l2@og.in"));
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

//
//    public int getSumType(PaymentType type, int userId) {
//
//    }
}
