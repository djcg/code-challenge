package com.hospital.codechallengeapi.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TimeSlotValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeSlotValidation {
    String message() default "Invalid time slot";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}