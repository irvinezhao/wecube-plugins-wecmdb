package com.webank.plugins.wecmdb.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.webank.plugins.wecmdb.dto.JsonResponse;
import com.webank.plugins.wecmdb.dto.OperateCiJsonResponse;
import com.webank.plugins.wecmdb.support.RemoteCallException;

@ControllerAdvice
public class DefaultExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public JsonResponse handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        if (exception instanceof RemoteCallException) {
            RemoteCallException remoteCallException = (RemoteCallException) exception;
            return JsonResponse.error(remoteCallException.getErrorMessage()).withData(remoteCallException.getErrorData());
        } if (exception instanceof OperationCiException) {
            return OperateCiJsonResponse.error(exception.getMessage());
        } else {
            return JsonResponse.error(exception.getMessage());
        }
    }

}
