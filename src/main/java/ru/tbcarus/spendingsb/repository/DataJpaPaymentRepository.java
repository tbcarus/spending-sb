package ru.tbcarus.spendingsb.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import ru.tbcarus.spendingsb.model.Payment;

import java.util.List;
import java.util.Optional;

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

    public Payment get(Specification<Payment> specification) {
//        return paymentRepository.findById(id).filter(payment -> payment.getUserID() == userId).orElse(null);
//        Optional<Payment> p = paymentRepository.findById(id);

        return paymentRepository.findOne(specification).orElse(null);
    }

    public Payment save(Payment p, int userId) {
//        if (!p.isNew() && get(p.getId(), p.getUserID()) == null) {
//        if (!p.isNew()) {
//            return null;
//        }
        return paymentRepository.save(p);
    }

    public List<Payment> saveAll(List<Payment> list, int userId) {
        return paymentRepository.saveAll(list);
    }

    public boolean delete(int id, int userId) {
        return paymentRepository.delete(id, userId) != 0;
    }
}
