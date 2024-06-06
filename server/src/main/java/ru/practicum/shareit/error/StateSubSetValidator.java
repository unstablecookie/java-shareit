package ru.practicum.shareit.error;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StateSubSetValidator implements ConstraintValidator<StateSubset, CharSequence> {
    private List<String> acceptedValues;

    @Override
    public void initialize(StateSubset annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (!acceptedValues.contains(value.toString())) {
            throw new UnsupportedStatusException();
        }
        return true;
    }
}
