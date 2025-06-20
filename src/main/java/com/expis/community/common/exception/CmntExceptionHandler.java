package com.expis.community.common.exception;

import com.expis.community.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(basePackages = "com.expis.community.controller")
public class CmntExceptionHandler {

    @ExceptionHandler(CmntException.class)
    public Object handleCmntExcption(CmntException ex, HttpServletRequest request, Model model){
        log.error("CmntException - code: {}", ex.getErrorCode().getCode(), ex);

        String uri = request.getRequestURI();

        //Ajax 호출
        if (uri.startsWith("/api")) {
            return ResponseEntity.status(ex.getErrorCode().getStatus())
                    .body(new ErrorResponseDTO(ex.getErrorCode()));
        }

        //타임리프 호출
        model.addAttribute("code", ex.getErrorCode().getCode());
        model.addAttribute("message", ex.getErrorCode().getMessage());
        return "error/customError";
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleUnexpectedException(Exception ex){
        log.error("Unhandle Exception", ex);

        ErrorResponseDTO response = new ErrorResponseDTO(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(response);
    }


}