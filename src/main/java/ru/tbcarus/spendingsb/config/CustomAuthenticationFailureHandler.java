package ru.tbcarus.spendingsb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import ru.tbcarus.spendingsb.exception.NotFoundException;
import ru.tbcarus.spendingsb.model.User;
import ru.tbcarus.spendingsb.service.EmailActionService;
import ru.tbcarus.spendingsb.service.UserService;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    UserService userService;

    @Autowired
    EmailActionService emailActionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        String email = request.getParameter("username");
        String password = request.getParameter("password");
        String param = "?error";
        User user = null;
        try {
            user = userService.getByEmail(email);
        } catch (NotFoundException exc) {
            // Пользователь не существует
            param = param + "=no-user-found";
            param = param + "&email=" + email;
        } catch (ConstraintViolationException exc) {
            // Неверный формат почты
            param = param + "=wrong=email-format";
        }
        if (user != null) {
            if (user.getPassword().equals(passwordEncoder.encode(password))) {
                if (user.isBanned()) {
                    // Пользователь забанен
                    param = param + "=banned";
                } else {
                    if (!user.isEnabled()) {
                        param = param + "&activate=true";
                    } else {
                        param = param + "&activate=false";
                    }
                }
            } else {
                if (!user.isBanned()) {
                    param = param + "&reset";
                }
            }
            param = param + "&email=" + email;
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.sendRedirect("login-error" + param);
    }
}
