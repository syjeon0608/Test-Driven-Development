package io.hhplus.tdd.domain.point;

import io.hhplus.tdd.point.PointValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PointValidatorTest {

    private final PointValidator validator = new PointValidator();

    @Test
    public void shouldPassValidationForValidInputs() {
        assertDoesNotThrow(() -> validator.validateCharge(1L, 50L));
    }

    @Test
    public void shouldThrowExceptionForInvalidUserId() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateUserId(null));
        assertThrows(IllegalArgumentException.class, () -> validator.validateUserId(0L));
        assertThrows(IllegalArgumentException.class, () -> validator.validateUserId(-1L));
    }

    @Test
    public void shouldThrowExceptionForInvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateCharge(1L, null));
    }

    @Test
    public void shouldValidateUseSuccessfully() {
        assertDoesNotThrow(() -> validator.validateUse(1L, 50L));
    }


    @Test
    public void shouldThrowExceptionWhenAmountIsNull() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateUse(1L, null));
    }
}