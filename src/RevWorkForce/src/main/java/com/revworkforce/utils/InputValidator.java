package com.revworkforce.utils;

import java.util.regex.Pattern;

public class InputValidator {


    public static boolean isValidRating(Double rating) {
        return rating != null && rating >= 1.0 && rating <= 5.0;
    }

    public static boolean isValidPercentage(int percentage) {
        return percentage >= 0 && percentage <= 100;
    }
}