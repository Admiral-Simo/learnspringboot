package com.simo.learnspringboot.learnspringboot.validation;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

/**
 * Defines the order of validation for DTOs.
 * 1. First, check if fields are not blank (FirstChecks).
 * 2. Then, perform format/size checks (SecondChecks).
 */
public interface ValidationGroups {
    interface FirstChecks {}
    interface SecondChecks {}

    // The @GroupSequence annotation defines the order of execution.
    // Validation stops if a group in the sequence fails.
    @GroupSequence({Default.class, FirstChecks.class, SecondChecks.class})
    interface All {}
}
