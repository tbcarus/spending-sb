package ru.tbcarus.spendingsb.util;

import ru.tbcarus.spendingsb.exception.IllegalRequestDataException;
import ru.tbcarus.spendingsb.model.HasId;

public class ValidationUtil {
    public static void assureIdConsistent(HasId bean, int id) {
//      conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.id() != id) {
            throw new IllegalRequestDataException(bean + " must be with id=" + id);
        }
    }
}
