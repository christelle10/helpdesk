package com.exist;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.exist.EmployeeService.*(..))")
    public void employeeServiceMethods() {}

    @Before("employeeServiceMethods()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        logger.info("Executing: {} with arguments: {}", joinPoint.getSignature(), Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(pointcut = "employeeServiceMethods()", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        logger.info("Completed: {} - Returned: {}", joinPoint.getSignature(), result);
    }

    @AfterThrowing(pointcut = "employeeServiceMethods()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in method: {} - Error: {}", joinPoint.getSignature(), exception.getMessage());
    }
}
