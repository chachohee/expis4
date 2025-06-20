package com.expis.common.rsa;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@Component
public class RSAConfig {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    @PostConstruct
    public void initKeys() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        this.publicKey = pair.getPublic();
        this.privateKey = pair.getPrivate();
    }

    public String getPublicKeyAsString() {
        if (publicKey == null) {
            throw new IllegalStateException("Public key is not initialized");
        }
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public PrivateKey getPrivateKey() {
        if (privateKey == null) {
            throw new IllegalStateException("Private key is not initialized");
        }
        return privateKey;
    }
}
