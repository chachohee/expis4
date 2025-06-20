package com.expis.login.interceptor;

import com.expis.common.CommonConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    // 로그인 여부만 확인
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
//        log.info("인증체크 인터셉터: {}", requestURI);

        // 세션에서 로그인 정보 확인
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(CommonConstants.LOGIN_MEMBER) == null) {
            log.info("not have session");

            // Ajax 호출인 경우 session expired
            boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
            if (isAjax) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"SESSION_EXPIRED\"}");
                return false;
            }

            // 로그인했던 bizCode의 로그인 페이지로 리다이렉트
            if(requestURI.startsWith("/EXPIS/")) {
                String[] parts = requestURI.split("/");
                String bizCode = parts[2];
                response.sendRedirect("/EXPIS/%s/login".formatted(bizCode));
                return false;
            }

            // 로그인 페이지로 리다이렉트
            response.sendRedirect("/");
            return false; // 요청을 처리하지 않고 리다이렉트
        }

        String[] parts = requestURI.split("/");
        String bizCode = parts[2];
//        log.info("bizCode={}", bizCode);
        if (requestURI.contains(".well-known") ) {
            return false;
        }
        if(session.getAttribute(CommonConstants.BIZ_CODE) == null || !session.getAttribute(CommonConstants.BIZ_CODE).toString().equalsIgnoreCase(bizCode)) {
            log.info("bizCode={} 미인증 사용자, 로그인 페이지로 리다이렉트", bizCode);
            // 해당 bizCode 로그인 페이지로 리다이렉트
            response.sendRedirect("/EXPIS/"+bizCode+"/login");
            return false; // 요청을 처리하지 않고 리다이렉트
        }

        return true; // 인증된 사용자라면 요청을 계속 진행
    }

}
