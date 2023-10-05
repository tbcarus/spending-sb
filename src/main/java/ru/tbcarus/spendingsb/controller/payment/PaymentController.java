package ru.tbcarus.spendingsb.controller.payment;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.tbcarus.spendingsb.exception.IllegalRequestDataException;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.UserService;
import ru.tbcarus.spendingsb.util.DateUtil;
import ru.tbcarus.spendingsb.util.PaymentsUtil;
import ru.tbcarus.spendingsb.util.SecurityUtil;
import ru.tbcarus.spendingsb.util.UtilsClass;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping(value = "/payments")
public class PaymentController extends AbstractPaymentController {

    @Autowired
    UserService userService;

    // Записи за выбранный период (месяц от начальной даты)
    @GetMapping("")
    public String getAll(Model model) {
        User user = userService.getByEmail("l2@og.in");
        Map<PaymentType, List<Payment>> paymentsMap = PaymentsUtil.getPaymentsMap(
                super.getAllByUserIdAndDateBetween(user.id(), user.getStartPeriodDate(), user.getEndPeriodDate()));
        addStandardAttr(model, user, paymentsMap);
        return "list";
    }

    @GetMapping("/byType")
    public String getByType(Model model, @RequestParam("type") PaymentType type) {
        User user = userService.getByEmail("l2@og.in");
        int userId = SecurityUtil.authUserId();
        List<Payment> list = super.getPaymentsByTypeUserIdBetween(type, userId, user.getStartPeriodDate(), user.getEndPeriodDate());
        model.addAttribute("user", user);
        model.addAttribute("list", list);
        model.addAttribute("paymentType", type);
        model.addAttribute("sum", PaymentsUtil.getSumByType(list));
        return "typedList";
    }

    // Записи от выбранной даты до текущего момента
    @GetMapping("/toCurrentDate")
    public String getAllToCurrentDate(Model model) {
        User user = userService.getByEmail("l2@og.in");
        Map<PaymentType, List<Payment>> paymentsMap = PaymentsUtil.getPaymentsMap(
                super.getAllByUserIdAndDateBetween(user.id(), user.getStartPeriodDate(), DateUtil.getLocalDateTimeNow().toLocalDate()));
        addStandardAttr(model, user, paymentsMap);
        return "list";
    }

    // Все записи за всё время
    @GetMapping("/allTime")
    public String getAllSelectedPeriod(Model model) {
        User user = userService.getByEmail("l2@og.in");
        Map<PaymentType, List<Payment>> paymentsMap = PaymentsUtil.getPaymentsMap(
                super.getAllByUserId(user.id()));
        addStandardAttr(model, user, paymentsMap);
        return "list";
    }

    // Записи всех пользователей за всё время
    @GetMapping("/allUsersPayments")
    public String getAllPayments(Model model) {
        User user = userService.getByEmail("l2@og.in");
        Map<PaymentType, List<Payment>> paymentsMap = PaymentsUtil.getPaymentsMap(
                super.getAll());
        addStandardAttr(model, user, paymentsMap);
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
        Payment payment = new Payment(LocalDate.now());
        model.addAttribute("payment", payment);
        return "edit";
    }

    @PostMapping
    public String updateOrCreate(HttpServletRequest request,
                                 @RequestParam(value = "id", required = false) String id,
                                 @RequestParam("payment_type") PaymentType type,
                                 @RequestParam("price") int price,
                                 @RequestParam("description") String description,
                                 @RequestParam("date") LocalDate date) {
        try {
            if (price == 0) {
                // Если трата нулевая, то это считается ошибкой
                throw new IllegalRequestDataException("Сумма траты не должна быть нулевой");
            }
        } catch (IllegalRequestDataException exc) {
            if (id.isEmpty()) {
                return "redirect:/payments/create";
            } else {
                return "redirect:/payments/" + id;
            }
        }
        Payment payment = new Payment(type, price, description, date);
        if (id.isEmpty()) {
            super.create(payment);
        } else {
            int paymentId = Integer.parseInt(id);
            payment.setId(paymentId);
            super.update(payment, paymentId);
        }
        return "redirect:/payments";
    }

    @PostMapping("/addSeveral")
    public String addSeveralPayments(HttpServletRequest request) {
        LocalDate date = LocalDate.parse(request.getParameter("chosen_date"));
        List<Payment> payments = new ArrayList<>();
        for (PaymentType pt : PaymentType.values()) {
            String[] strPrice = request.getParameterValues(pt.name());
            String[] descriptions = request.getParameterValues(pt.name() + "_description");
            for (int i = 0; i < strPrice.length; i++) {
                if (strPrice[i] != null && !strPrice[i].isEmpty()) {
                    int price = UtilsClass.toInt(strPrice[i]);
                    if (price == Integer.MIN_VALUE) {
                        continue;
                    }
                    String description = "";
                    if (pt.isDescriptionOutput()) {
                        description = UtilsClass.checkDescription(descriptions[i]);
                    }
                    Payment p = new Payment(pt, price, description, date);
                    payments.add(p);
                }
            }
        }
        super.createAll(payments);
        return "redirect:/payments";
    }

    @GetMapping("/delete")
    public String deleteStr(@RequestParam String id, HttpServletRequest request) {
        super.delete(getId(id));
        if (request.getHeader("referer").contains("byType")) {
            return "redirect:" + request.getHeader("referer");
        }
        return "redirect:/payments";
    }

    private void addStandardAttr(Model model, User user, Map<PaymentType, List<Payment>> paymentsMap) {
        Map<PaymentType, Integer> sumMapByType = PaymentsUtil.getSumMapByType(paymentsMap);
        model.addAttribute("PaymentsMap", paymentsMap);
        model.addAttribute("paymentTypes", PaymentType.values());
        model.addAttribute("maxSize", PaymentsUtil.maxSize(paymentsMap));
        model.addAttribute("sumMapByType", sumMapByType);
        model.addAttribute("sumAll", PaymentsUtil.getSumAll(sumMapByType));
        model.addAttribute("user", user);
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    private int getId(String id) {
        String paramId = Objects.requireNonNull(id);
        return Integer.parseInt(paramId);
    }
}
