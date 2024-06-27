package com.jpms.codinggame.config;

import java.util.Arrays;

public enum PermitAllEndpoint {
    SIGNUP("/signup"),
    ROOT("/"),
    LOGIN("/login"),
    VERIFY_EMAIL("/verify-email"),
    USERS("/users/**"),
    SWAGGER("/swagger-ui/**"),
    SOCIAL_LOGIN("/auth"),
    V3("v3/**"),
    GPT("/bot/**");

    private final String url;

    PermitAllEndpoint(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public static String[] getUrls() {
        return Arrays.stream(values())
                .map(PermitAllEndpoint::getUrl)
                .toArray(String[]::new);
    }

    public static boolean isPermitAll(String requestURI) {
        return Arrays.stream(values())
                .anyMatch(endpoint -> requestURI.equals(endpoint.getUrl()) || requestURI.startsWith(endpoint.getUrl()));
    }
}
