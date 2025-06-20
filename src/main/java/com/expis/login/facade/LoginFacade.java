package com.expis.login.facade;

import com.expis.common.controller.RSAController;
import com.expis.manage.service.SystemOptionService;
import com.expis.user.dto.UserDto;
import com.expis.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoginFacade {

    private final MessageSource messageSource;

    private final RSAController rsaController;
    private final UserService userService;
    private final SystemOptionService systemOptionService;

    public UserDto authenticate(String postData, HttpServletRequest request, Locale locale) {
        // 암호화된 로그인 데이터를 RSA 개인키로 복호화
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = null;
        try {
            String decData = rsaController.decrypt(postData);
            log.info("decData={}", decData);

            map = objectMapper.readValue(decData, Map.class);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("login.error.badKey", null, locale));
        }

        if (map == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("login.error.badKey", null, locale));
        }

        String decryptedLoginId = map.get("loginId");
        String decryptedPassword = map.get("password");

        if (decryptedLoginId == null || decryptedPassword == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("login.error.empty", null, locale));
        }

//        log.info("복호화된 loginId? {}", decryptedLoginId);
//        log.info("복호화된 password? {}", decryptedPassword);

        // 복호화된 로그인 데이터를 사용하여 로그인 인증
        UserDto findUserDto = userService.getUserInfo(decryptedLoginId);
//        log.info("findUserDto={}", findUserDto);

        // validation
        if (findUserDto == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, messageSource.getMessage("login.error.empty", null, locale));
        }

        //로그인 실패 사유 - jingi.kim
        if( !findUserDto.getUserPw().equals(decryptedPassword)) {
            userService.saveFailConnHistory(decryptedLoginId, request);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, messageSource.getMessage("login.error.empty", null, locale));
        }
        if ( findUserDto.getStatusKind().equalsIgnoreCase("20") ) {
            userService.saveFailConnHistory(decryptedLoginId, request);
            throw new ResponseStatusException(HttpStatus.LOCKED, messageSource.getMessage("login.error.locked", null, locale));
        }
        String accountEndDate = String.valueOf(findUserDto.getUserAccountEndDate());
        // log.info("accountEndDate={}",accountEndDate);
        if ( findUserDto.getUserAccountEndDate() != null && !"".equalsIgnoreCase(accountEndDate)) {
            try {
                LocalDate today = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate parseDate = LocalDate.parse(accountEndDate, formatter);
//                log.info("today={}, parseDate={}",today, parseDate);
                if (parseDate.isBefore(today)) {
                    userService.saveFailConnHistory(decryptedLoginId, request);
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, messageSource.getMessage("login.error.account.expired", null, locale));
                }
            } catch (DateTimeParseException dte) {
                userService.saveFailConnHistory(decryptedLoginId, request);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, messageSource.getMessage("login.error.account.expired", null, locale));
            }
        }

        return findUserDto;
    }

    public long calcPasswordExpired(String pwdChangeDate) {
        if (pwdChangeDate == null) return -1;

        long dateDiff = -1;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date pwChangeDat = sdf.parse(pwdChangeDate);
            Date nowDate = new Date();
            dateDiff = ((nowDate.getTime() - pwChangeDat.getTime()) /60000)/(24*60);
            log.info("userDetails pwChangeDat:{}, nowDate:{}, dateDiff:{}",pwChangeDat, nowDate, dateDiff);
        } catch (ParseException _) {
        }
        return dateDiff;
    }

}
