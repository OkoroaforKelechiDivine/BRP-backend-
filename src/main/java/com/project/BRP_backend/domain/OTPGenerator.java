package com.project.BRP_backend.domain;

import java.util.Random;


public class OTPGenerator {

    public static String generateOTP() {
        Random random = new Random();
        return String.valueOf(random.nextInt(1000000));
    }
}
