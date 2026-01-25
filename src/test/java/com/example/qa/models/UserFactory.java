
package com.example.qa.models;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public class UserFactory {

    private static final SecureRandom SEC = new SecureRandom();
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String ALNUM_LOWER = LOWER + DIGITS;
    private static final String ALNUM_MIXED = LOWER + UPPER + DIGITS;

    private static String randFrom(String alphabet, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(alphabet.charAt(SEC.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    private static String alnumLower(int len) {
        return randFrom(ALNUM_LOWER, len);
    }

    private static String alnumMixed(int len) {
        return randFrom(ALNUM_MIXED, len);
    }

    private static String digits(int len) {
        return randFrom(DIGITS, len);
    }

    private static String upperLetters(int len) {
        return randFrom(UPPER, len);
    }

    private static String truncate(String s, int maxLen) {
        return s.length() <= maxLen ? s : s.substring(0, maxLen);
    }

    private static String firstName() { return truncate(alnumMixed(10), 10); }
    private static String lastName()  { return truncate(alnumMixed(10), 10); }
    private static String city()      { return truncate(alnumMixed(10), 10); }

    private static String address() {
        return "Addr " + digits(3) + " " + upperLetters(4);
    }

    private static String state() { return upperLetters(2); }

    private static String zip()   { return digits(5); }
    private static String phone() { return digits(10); }
    private static String ssn()   { return digits(9); }

    private static String username() { return "u" + alnumLower(9); }
    private static String password() { return alnumMixed(12); }

    public static User validRandomUser() {
        return new User(
                firstName(),
                lastName(),
                address(),
                city(),
                state(),
                zip(),
                phone(),
                ssn(),
                username(),
                password()
        );
    }

    public static User invalidRandomUser() {
        long n = System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000);
        return new User(
                "randomFirstName" + n,
                "randomLastName" + n,
                "randomAddress" + n,
                "randomCity" + n,
                "randomState" + n,
                "randomZipCode" + n,
                "randomPhoneNumber" + n,
                "randomSsn" + n,
                "randomUniqueUserName" + n,
                "randomPassword" + n
        );
    }
}
