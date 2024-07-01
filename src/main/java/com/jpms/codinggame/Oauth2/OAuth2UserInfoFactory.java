package com.jpms.codinggame.Oauth2;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        }
//        else if (registrationId.equalsIgnoreCase("kakao")) {
//            return new KakaoOAuth2UserInfo(attributes);
//        }
        else {
            throw new IllegalArgumentException();
        }

    }
}
