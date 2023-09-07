package ru.practicum.shareit.service;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StartBeforeEndValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StartBeforeEnd {

    String message() default "{В запросе аренды дата/время возврата должна быть строго позже начала аренды}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
