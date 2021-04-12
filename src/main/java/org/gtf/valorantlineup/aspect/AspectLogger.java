package org.gtf.valorantlineup.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AspectLogger {

    private boolean LOG_REQUESTBODY;
    private boolean LOG_RESPONSEBODY;

    //Custom Aspect-based (AOP) slf4j LOGGER to audit all the HTTP request calls.
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private HttpServletRequest request;

    @Autowired
    public AspectLogger(@Value("${log.request.body}") boolean LOG_REQUESTBODY, @Value("${log.response.body}") boolean LOG_RESPONSEBODY, HttpServletRequest request) {
        this.LOG_REQUESTBODY = LOG_REQUESTBODY;
        this.LOG_RESPONSEBODY = LOG_RESPONSEBODY;
        this.request = request;
    }

    //Extract passing parameter as payload from controller
    private String getPayload(JoinPoint joinPoint) {
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < joinPoint.getArgs().length; i++) {
            String parameterName = signature.getParameterNames()[i];
            builder.append(parameterName);
            builder.append(": ");
            builder.append(joinPoint.getArgs()[i].toString());
            builder.append(", ");
        }
        return builder.toString();
    }

    //Logs all method call from controller package
    @Around("execution(* org.gtf.valorantlineup.controllers..*.*(..))")
    public Object apiInterceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        try {
            stopWatch.start();
            LOGGER.info("Starting: " + joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName());
            LOGGER.info("Method: " + request.getMethod());
            if(LOG_REQUESTBODY) {
                if ("POST".equalsIgnoreCase(request.getMethod()) || "PUT".equalsIgnoreCase(request.getMethod())) LOGGER.info("Payload " + getPayload(joinPoint));
            }
            return joinPoint.proceed();
            //LOGGER.info("Response: " + result.toString());
        } finally {
            stopWatch.stop();
            LOGGER.info("Execution time : " + stopWatch.getTotalTimeMillis() + " ms");
        }
    }
    //Logs return object from controller package
    @AfterReturning(pointcut = "execution(* org.gtf.valorantlineup.controllers..*.*(..))",
            returning = "retVal")
    public void afterReturningAdvice(JoinPoint jp, Object retVal){
        if(LOG_RESPONSEBODY) LOGGER.info("Response: " + retVal.toString());
        LOGGER.info("Completed: " + jp.getSignature().getName() + "\n");
    }
}
