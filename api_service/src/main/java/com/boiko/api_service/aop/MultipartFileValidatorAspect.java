package com.boiko.api_service.aop;


import com.boiko.api_service.aop.annotations.ValidTypes;
import com.boiko.api_service.exception.InvalidFileTypeException;
import org.apache.commons.io.FilenameUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class MultipartFileValidatorAspect {
    @Before("@annotation(com.boiko.api_service.aop.annotations.ValidateMultipartFiles)")
    public void validateFileTypeAdvice(JoinPoint joinPoint) {
        for (Object arg : joinPoint.getArgs()) {
            if (isNotAnnotated(arg)) {
                continue;
            }
            if (isNotMultipartFile(arg)) {
                continue;
            }

            List<String> validTypes = Arrays.asList(arg.getClass().getAnnotation(ValidTypes.class).value());
            MultipartFile file = (MultipartFile) arg;
            if (isNotValidType(file, validTypes)) {
                throw new InvalidFileTypeException("%s isn't valid, list of valid types: %s".formatted(
                        file.getContentType(), validTypes
                ));
            }
        }
    }

    private boolean isNotAnnotated(Object obj) {
        return ! obj.getClass().isAnnotationPresent(ValidTypes.class);
    }

    private boolean isNotMultipartFile(Object obj) {
        return !(obj instanceof MultipartFile);
    }

    private boolean isNotValidType(MultipartFile file, List<String> validTypes) {
        return !validTypes.contains(
                FilenameUtils.getExtension(file.getOriginalFilename())
        );
    }
}
