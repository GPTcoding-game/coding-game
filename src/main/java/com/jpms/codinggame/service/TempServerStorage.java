package com.jpms.codinggame.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TempServerStorage {
    private final Map<String, Integer> verificationCodes = new ConcurrentHashMap<>();

    public void saveVerificationCode(String email, int code) {
        verificationCodes.put(email, code);
    }

    public int getVerificationCode(String email) {
        return verificationCodes.getOrDefault(email, -1);
    }

    public void removeVerificationCode(String email) {
        verificationCodes.remove(email);
    }
}
