package ru.tbcarus.spendingsb.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rest/profile", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ProfileRestController {

    @GetMapping
    public void get(){
        log.debug("In GET method");
    }
}
