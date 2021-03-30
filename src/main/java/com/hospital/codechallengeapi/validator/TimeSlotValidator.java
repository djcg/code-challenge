package com.hospital.codechallengeapi.validator;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TimeSlotValidator implements ConstraintValidator<TimeSlotValidation, Instant> {

  @Override
  public void initialize(TimeSlotValidation constraintAnnotation) {}

  @Override
  public boolean isValid(Instant value, ConstraintValidatorContext context) {
    List<String> errors = new ArrayList<>();
    if (value.isBefore(Instant.now())) {
      errors.add("Picked date cannot be in the past");
    }

    ZonedDateTime pickedValue = ZonedDateTime.ofInstant(value, ZoneId.of("UTC"));

    if (pickedValue.getHour() < 9 || pickedValue.getHour() > 19) {
      errors.add("Picked hour must be between 9 and 19");
    }

    if (!errors.isEmpty()) {
      context.disableDefaultConstraintViolation();
      for (String error : errors) {
        context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
      }
      return false;
    }

    return true;
  }
}
