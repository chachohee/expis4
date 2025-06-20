package com.expis.login.service;

import com.expis.user.dao.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public void saveLoginConnHistory(String loginId, HttpServletRequest request) throws ParseException {

//        // 세션 ID
//        String sessionId = request.getSession().getId();
//        // 클라이언트 IP 주소
//        String connIp = request.getHeader("X-Forwarded-For");
//        if (connIp == null || "".equalsIgnoreCase(connIp)) {
//            connIp = request.getRemoteAddr();
//            log.info("connIp: " + connIp);
//        }
//        // statusKind
//        String statusKind = "20";
//
//        LoginConnHistory loginConnHistory = new LoginConnHistory(
//                0,
//                sessionId,
//                loginId,
//                connIp,
//                statusKind,
//                LocalDateTime.now(),
//                null
//        );
//        loginConnHistoryRepository.save(loginConnHistory);
//        log.info("로그인 성공 히스토리 추가 완료");
    }

    public void saveFailConnHistory(String loginId, HttpServletRequest request) {

//        // 클라이언트 IP 주소
//        String connIp = request.getHeader("X-Forwarded-For");
//        if (connIp != null && connIp.contains(",")) {
//            connIp = connIp.split(",")[0].trim(); // 첫 번째 IP만 가져오기
//        }
//        if (connIp == null || connIp.isEmpty()) {
//            connIp = request.getRemoteAddr();
//        }
//        // statusKind
//        String statusKind = "20";
//
//        LoginFailHistory loginFailHistory = new LoginFailHistory(
//                0,
//                loginId,
//                connIp,
//                statusKind,
//                LocalDateTime.now(),
//                LocalDateTime.now(),
//                null
//        );
//        loginFailHistoryRepository.save(loginFailHistory);
//        log.info("로그인 실패 히스토리 추가 완료");
    }

    public void updateDisconnDate(String sessionId) {
//        LoginConnHistory loginConnHistory = loginConnHistoryRepository.findBySessionId(sessionId);
//        log.info("loginConnHistory sessionId : " + loginConnHistory.getSessionId());
//        if (loginConnHistory != null) {
//            loginConnHistory.setDisConnDate(LocalDateTime.now());
//            loginConnHistoryRepository.save(loginConnHistory);
//        }
//        log.info("로그인 로그아웃 시간 업뎃 완료");
    }
}