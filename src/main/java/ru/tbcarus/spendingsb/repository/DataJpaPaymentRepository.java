package ru.tbcarus.spendingsb.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import ru.tbcarus.spendingsb.model.Payment;

import java.util.List;

@Repository
public class DataJpaPaymentRepository {

    private final JpaPaymentRepository paymentRepository;
    private final JpaUserRepository userRepository;


    public DataJpaPaymentRepository(JpaPaymentRepository paymentRepository, JpaUserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public List<Payment> getPayments(Specification<Payment> specification, Sort sort) {
        return paymentRepository.findAll(specification, sort);
    }

    public List<Payment> getAll() {
        return paymentRepository.getAll();
    }

    public Payment get(int id, int userId) {
        return paymentRepository.findById(id).filter(payment -> payment.getUserID() == userId).orElse(null);
    }

    public Payment save(Payment p, int userId) {
        if (!p.isNew() && get(p.getId(), p.getUserID()) == null) {
            return null;
        }
        return paymentRepository.save(p);
    }

    public boolean delete(int id, int userId) {
        return paymentRepository.delete(id, userId) != 0;
    }
}
