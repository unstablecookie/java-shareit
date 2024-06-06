package ru.practicum.shareit.util;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NotNull
@Email(message = "enter valid email address")
@Pattern(regexp = ".+@.+\\..+", message = "enter valid email address")
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
public @interface CustomEmailValidator {
    String message() default "enter valid email address";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
