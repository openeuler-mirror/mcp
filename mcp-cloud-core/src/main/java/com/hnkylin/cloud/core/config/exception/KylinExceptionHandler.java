package com.hnkylin.cloud.core.config.exception;


import com.hnkylin.cloud.core.common.BaseResult;
import com.hnkylin.cloud.core.common.HttpCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class KylinExceptionHandler {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = KylinException.class)
    @ResponseBody
    public ResponseEntity<Object> custKylinTokenExpireException(HttpServletRequest request, KylinException exception) {
        log.error(request.getRequestURI(), exception);
        return new ResponseEntity<Object>(BaseResult.error(exception.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(value = KylinTokenException.class)
    @ResponseBody
    public ResponseEntity<Object> kylinTokenExpireException(HttpServletRequest request, KylinTokenException exception) {
        log.error(request.getRequestURI(), exception);
        return new ResponseEntity<Object>(BaseResult.tokenExpire(exception.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(value = KylinParamException.class)
    @ResponseBody
    public ResponseEntity<Object> validateParameterException(HttpServletRequest request, KylinParamException pe) {
        log.error(request.getRequestURI(), pe);
        return new ResponseEntity<Object>(BaseResult.paramError(pe.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(value = KylinMcRequestException.class)
    @ResponseBody
    public ResponseEntity<Object> kylinMcRequestException(HttpServletRequest request, KylinMcRequestException pe) {
        log.error(request.getRequestURI(), pe);
        return new ResponseEntity<Object>(BaseResult.error(pe.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<Object> notReadable(HttpServletRequest request, HttpMessageNotReadableException re) {
        log.error(request.getRequestURI(), re);
        return new ResponseEntity<Object>(BaseResult.paramError(re.getMessage()), HttpStatus.OK);
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<Object> serverErr(HttpServletRequest request, Exception re) {
        log.error(request.getRequestURI(), re);
        return new ResponseEntity<Object>(BaseResult.error(HttpCode.CodeEnum.SERVER_ERROR.getDesc()), HttpStatus.OK);
    }
}
