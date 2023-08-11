package ru.tbcarus.spendingsb.service;

import lombok.extern.slf4j.Slf4j;
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
    private final PaymentRepository repo;

    public PaymentService(PaymentRepository paymentRepository) {
        this.repo = paymentRepository;
    }

    public Payment get(int id, int userId) {
        Optional<Payment> opt = repo.findById(id);
        if (!opt.isEmpty()) {
            throw new NotFoundException();
        }
        return opt.get();
    }

    public List<Payment> getAll() {
        return repo.findAll();
    }

    public List<Payment> getAllByUser(int userId) {
        return repo.findAllByUserId(userId);
    }

    public List<Payment> getAllByType(PaymentType type, int userId) {
        return repo.findAllByUserIdAndType(type, userId);
    }

    public List<Payment> getBetween(LocalDate after, LocalDate before, int userId) {
        return repo.getBetween(after, before, userId);
    }

    public Payment create(Payment payment, int userId) {
        return repo.save(payment);
    }

    public Payment update(Payment payment, int userId) {
        return repo.save(payment);
    }

    public void delete(int id, int userId) {
        repo.deleteById(id);
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
