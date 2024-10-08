package ru.tbcarus.spendingsb.controller.payment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.tbcarus.spendingsb.exception.IllegalRequestDataException;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.Payment;
import ru.tbcarus.spendingsb.model.PaymentType;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.util.DateUtil;
import ru.tbcarus.spendingsb.util.PaymentsUtil;
import ru.tbcarus.spendingsb.util.UtilsClass;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping(value = "/payments")
public class PaymentUIController extends AbstractPaymentController {

    // Записи за выбранный период (месяц от начальной даты)
    // OK
    @GetMapping("")
    public String getAll(Model model, @AuthenticationPrincipal User user) {
        user = super.getByIdWithFriends(user.getId());
        Map<PaymentType, List<Payment>> paymentsMap = super.getAllMapByUserAndDateBetween(user, user.getStartPeriodDate(), user.getEndPeriodDate());
        // создание мапы цветов трат
        Map<Integer, String> colorMap = super.getColorMap(user);
        model.addAttribute("colorMap", colorMap);
        addStandardAttr(model, user, paymentsMap);
        return "list";
    }

    // OK
    @GetMapping("/byType")
    public String getByType(Model model, @RequestParam("type") PaymentType type, @AuthenticationPrincipal User user) {
        user = super.getByIdWithFriends(user.getId());
        List<Payment> list = super.getPaymentsByTypeUserBetween(user, type, user.getStartPeriodDate(), user.getEndPeriodDate());
        model.addAttribute("user", user);
        model.addAttribute("list", list);
        model.addAttribute("paymentType", type);
        model.addAttribute("sum", PaymentsUtil.getSumByType(list));
        Map<Integer, String> colorMap = super.getColorMap(user);
        model.addAttribute("colorMap", colorMap);
        return "typedList";
    }

    // Записи от выбранной даты до текущего момента
    // OK
    @GetMapping("/toCurrentDate")
    public String getAllToCurrentDate(Model model, @AuthenticationPrincipal User user) {
        user = super.getByIdWithFriends(user.getId());
        Map<PaymentType, List<Payment>> paymentsMap =
                super.getAllMapByUserAndDateBetween(user, user.getStartPeriodDate(), DateUtil.getLocalDateTimeNow().toLocalDate());
        // создание мапы цветов трат
        Map<Integer, String> colorMap = super.getColorMap(user);
        model.addAttribute("colorMap", colorMap);
        addStandardAttr(model, user, paymentsMap);
        return "list";
    }

    // Все записи за всё время
    // OK
    @GetMapping("/allTime")
    public String getAllSelectedPeriod(Model model, @AuthenticationPrincipal User user) {
        user = super.getByIdWithFriends(user.getId());
        Map<PaymentType, List<Payment>> paymentsMap = super.getAllMapByUserId(user);
        // создание мапы цветов трат
        Map<Integer, String> colorMap = super.getColorMap(user);
        model.addAttribute("colorMap", colorMap);
        addStandardAttr(model, user, paymentsMap);
        return "list";
    }

    // Записи всех пользователей за всё время
    // OK
    @GetMapping("/allUsersPayments")
    public String getAllPayments(Model model, @AuthenticationPrincipal User user) {
        user = super.getByIdWithFriends(user.getId());
        Map<PaymentType, List<Payment>> paymentsMap = super.getAllMap();
        // создание мапы цветов трат друзей
        Map<Integer, String> colorMap = super.getColorMap(user);
        model.addAttribute("colorMap", colorMap);
        addStandardAttr(model, user, paymentsMap);
        return "list";
    }

    // OK
    @GetMapping("/{id}")
    public String get(Model model, @PathVariable int id, @AuthenticationPrincipal User user) {
        Payment payment;
        user = super.getByIdWithFriends(user.getId());
        model.addAttribute("user", user);
        try {
            payment = super.get(user, id);
        } catch (NotFoundException exc) {
            model.addAttribute("err", exc.getMessage());
            return "error";
        }
        model.addAttribute("payment", payment);
        return "edit";
    }

    // OK
    @GetMapping("/create")
    public String create(Model model, @AuthenticationPrincipal User user) {
        user = super.getByIdWithFriends(user.getId());
        Payment payment = new Payment(LocalDate.now());
        model.addAttribute("payment", payment);
        model.addAttribute("user", user);
        return "edit";
    }

    @PostMapping
    public String updateOrCreate(@Valid Payment p, BindingResult result, @AuthenticationPrincipal User user, Model model) {
        if (result.hasErrors()) {
            user = super.getByIdWithFriends(user.getId());
            model.addAttribute("user", user);
            if (p.isNew() || p.getId() == 0) {
                return "edit";
            } else {
                Payment payment = super.get(user, p.getId());
                p.setUser(payment.getUser());
                return "edit";
            }
        }
        user = super.getByIdWithFriends(user.getId());
        try {
            if (p.getPrice() == 0) {
                // Если трата нулевая, то это считается ошибкой
                throw new IllegalRequestDataException("Сумма траты не должна быть нулевой");
            }
        } catch (IllegalRequestDataException exc) {
            if (p.isNew() || p.getId() == 0) {
                return "redirect:/payments/create";
            } else {
                return "redirect:/payments/" + p.getId();
            }
        }
        if (p.isNew() || p.getId() == 0) {
            super.create(p);
        } else {
            super.update(user, p);
        }
        return "redirect:/payments";
    }

    @PostMapping("/addSeveral")
    public String addSeveralPayments(HttpServletRequest request, @AuthenticationPrincipal User user) {
        LocalDate date = LocalDate.parse(request.getParameter("chosen_date"));
        List<Payment> payments = new ArrayList<>();
        for (PaymentType pt : PaymentType.values()) {
            String[] strPrice = request.getParameterValues(pt.name());
            String[] descriptions = request.getParameterValues(pt.name() + "_description");
            for (int i = 0; i < strPrice.length; i++) {
                if (strPrice[i] != null && !strPrice[i].isEmpty()) {
                    int price = UtilsClass.toInt(strPrice[i]);
                    if (price == Integer.MIN_VALUE || price == 0) {
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
    public String deleteStr(@RequestParam String id, HttpServletRequest request, @AuthenticationPrincipal User user) {
        user = super.getByIdWithFriends(user.getId());
        super.delete(user, getId(id));
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
