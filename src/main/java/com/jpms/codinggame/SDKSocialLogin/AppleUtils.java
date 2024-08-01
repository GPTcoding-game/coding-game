package com.jpms.codinggame.SDKSocialLogin;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;

import java.net.URL;
import java.util.List;

public class AppleUtils {

    private static final String APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys";

    public static JWKSet getApplePublicKeys() throws Exception {
        return JWKSet.load(new URL(APPLE_KEYS_URL));
    }

    public static JWK getApplePublicKey(String kid) throws Exception {
        JWKSet jwkSet = getApplePublicKeys();
        List<JWK> keys = jwkSet.getKeys();

        for (JWK key : keys) {
            if (key.getKeyID().equals(kid)) {
                return key;
            }
        }

        throw new Exception();
    }

    public static SignedJWT verifyAppleToken(String idToken) throws Exception {
        SignedJWT signedJWT = SignedJWT.parse(idToken);
        JWK jwk = getApplePublicKey(signedJWT.getHeader().getKeyID());

        JWSVerifier verifier = new RSASSAVerifier(jwk.toRSAKey());
        if (!signedJWT.verify(verifier)) {
            throw new Exception();
        }

        return signedJWT;
    }
}