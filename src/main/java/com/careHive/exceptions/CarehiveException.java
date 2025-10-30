package com.careHive.exceptions;
import com.careHive.enums.ExceptionCodeEnum;
import lombok.*;
import org.springframework.web.bind.annotation.ControllerAdvice;

@RequiredArgsConstructor
@ControllerAdvice
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper=false)
@Data
public class CarehiveException extends Exception{
    public ExceptionCodeEnum exceptionCode;
    public String errorMessage;
    public String referenceId;

    public CarehiveException(ExceptionCodeEnum exceptionCode, String errorMessage) {
        this.exceptionCode = exceptionCode;
        this.errorMessage = errorMessage;
    }
}