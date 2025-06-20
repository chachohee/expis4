package com.expis.common.controller;

import com.expis.common.rsa.RSAService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/EXPIS/rsa")
public class RSAController {
    private final RSAService rsaService;

    @GetMapping("/publicKey")
    public String getPublicKey() {
        return rsaService.getPublicKeyAsString();
    }

    @PostMapping("/decrypt")
    public String decrypt(@RequestBody String encryptedData) {
        return rsaService.decrypt(encryptedData);
    }
}
