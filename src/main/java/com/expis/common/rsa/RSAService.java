package com.expis.common.rsa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class RSAService {

    private final RSAConfig rsaConfig;

    /**
     * RSA 개인키를 사용해 암호화된 데이터를 복호화합니다.
     * @param encryptedData 암호화된 데이터
     * @return 복호화된 문자열
     */
    public String decrypt(String encryptedData) {
        log.debug("Received encrypted data: {}", encryptedData);

        if (encryptedData == null || encryptedData.isEmpty()) {
            log.error("Encrypted data is null or empty");
            return null;
        }

        try {
            // Base64 디코딩
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, rsaConfig.getPrivateKey());

            // 복호화
            byte[] decryptedBytes = cipher.doFinal(decodedData);
            return new String(decryptedBytes);
        } catch (IllegalArgumentException e) {
            log.error("Illegal base64 input: {}", encryptedData, e);
        } catch (Exception e) {
            log.error("Error decrypting data", e);
        }
        return null;
    }

    /**
     * RSA 공개키를 문자열로 반환합니다.
     * @return RSA 공개키 문자열
     */
    public String getPublicKeyAsString() {
        return rsaConfig.getPublicKeyAsString();
    }
}
