package ru.tbcarus.spendingsb.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;
import ru.tbcarus.spendingsb.repository.PaymentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    Sort sort = Sort.by("date", "userID", "price");

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> getPayments(Specification<Payment> specification) {
        return paymentRepository.findAll(specification, sort);
    }

    public Payment get(int id, int userId) {
        Optional<Payment> opt = paymentRepository.findById(id);
        if (opt.isEmpty()) {
            throw new NotFoundException();
        }
        return opt.get();
    }

    public List<Payment> getAll() {
        return paymentRepository.getAll();
    }

    public Payment create(Payment payment, int userId) {
        return paymentRepository.save(payment);
    }

    public Payment update(Payment payment, int userId) {
        return paymentRepository.save(payment);
    }

    public void delete(int id, int userId) {
        if (!paymentRepository.existsById(id)) {
            throw new NotFoundException();
        }
        paymentRepository.deleteById(id);
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

    public Specification<Payment> filterByUser(Integer userId) {
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

    // Методы ниже, наверное, следует вынести в утильный класс
//    public int size() {
//
//    }
//
//    public int getSumType(PaymentType type, int userId) {
//
//    }
}
