package ru.tbcarus.spendingsb.controller.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.UserService;
import ru.tbcarus.spendingsb.util.DateUtil;
import ru.tbcarus.spendingsb.util.PaymentsUtil;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/payments")
public class PaymentController extends AbstractPaymentController {

    @Autowired
    UserService userService;

    // Все записи за всё время
    @GetMapping("")
    public String getAll(Model model) {
        User user = userService.getByEmail("l2@og.in");
        Map<PaymentType, List<Payment>> paymentsMap = PaymentsUtil.getPaymentsMap(
                super.getAllByUserIdAndDateBetween(user.id(), user.getStartPeriodDate(), user.getEndPeriodDate()));
        addStandartAttr(model, user, paymentsMap);
        return "list";
    }

    // Записи от выбранной даты до текущего момента
    @GetMapping("/toCurrentDate")
    public String getAllToCurrentDate(Model model) {
        User user = userService.getByEmail("l2@og.in");
        Map<PaymentType, List<Payment>> paymentsMap = PaymentsUtil.getPaymentsMap(
                super.getAllByUserIdAndDateBetween(user.id(), user.getStartPeriodDate(), DateUtil.getLocalDateTimeNow().toLocalDate()));
        addStandartAttr(model, user, paymentsMap);
        return "list";
    }

    // Записи за выбранный период (месяц от начальной даты)
    @GetMapping("/allTime")
    public String getAllSelectedPeriod(Model model) {
        User user = userService.getByEmail("l2@og.in");
        Map<PaymentType, List<Payment>> paymentsMap = PaymentsUtil.getPaymentsMap(
                super.getAllByUserId(user.id()));
        addStandartAttr(model, user, paymentsMap);
        return "list";
    }

    // Записи всех пользователей за всё время
    @GetMapping("/allUsersPayments")
    public String getAllPayments(Model model) {
        User user = userService.getByEmail("l2@og.in");
        Map<PaymentType, List<Payment>> paymentsMap = PaymentsUtil.getPaymentsMap(
                super.getAll());
        addStandartAttr(model, user, paymentsMap);
        return "list";
    }

    @GetMapping("/{id}")
    public String get(Model model, @PathVariable int id) {
        Payment payment = super.get(id);
        model.addAttribute("payment", payment);
        return "edit";
    }

    @GetMapping("/create")
    public String create(Model model) {
        Payment payment = new Payment();
        model.addAttribute("payment", payment);
        return "edit";
    }

    private void addStandartAttr(Model model, User user, Map<PaymentType, List<Payment>> paymentsMap) {
        Map<PaymentType, Integer> sumMapByType = PaymentsUtil.getSumMapByType(paymentsMap);
        model.addAttribute("PaymentsMap", paymentsMap);
        model.addAttribute("paymentTypes", PaymentType.values());
        model.addAttribute("maxSize", PaymentsUtil.maxSize(paymentsMap));
        model.addAttribute("sumMapByType", sumMapByType);
        model.addAttribute("sumAll", PaymentsUtil.getSumAll(sumMapByType));
        model.addAttribute("user", user);
    }
}
