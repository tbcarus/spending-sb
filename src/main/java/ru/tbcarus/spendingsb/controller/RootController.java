package ru.tbcarus.spendingsb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tbcarus.spendingsb.model.PaymentType;

@Slf4j
@Controller
public class RootController {

    @GetMapping("/")
    public String root() {
        log.info("Get root");
        return "index";
    }

    @GetMapping("/list")
    public String list() {
        log.info("Get list");
        return "list";
    }
    @GetMapping("/typedList")
    public String typedList(@RequestParam PaymentType type) {
        log.info("Get typed list {}", type);
        return "typedList";
    }
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id) {
        log.info("Edit payment {}", id);
        return "edit";
    }
    @GetMapping("/settings")
    public String settings() {
        log.info("Get settings");
        return "settings";
    }
}
