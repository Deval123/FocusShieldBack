package com.dev.focusshield.utils;


public class PasswordValidator {

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).+$";
        return password.matches(regex);
    }

}
