package ru.tbcarus.spendingsb.util;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.tbcarus.spendingsb.model.AbstractBaseEntity;
import ru.tbcarus.spendingsb.model.User;

public class SecurityUtil {
        private static int id = AbstractBaseEntity.START_SEQ;

    private SecurityUtil() {
    }

    public static int authUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = (User) principal;
        return user.id();
    }

    public static void setAuthUserId(int id) {
        SecurityUtil.id = id;
    }
}
