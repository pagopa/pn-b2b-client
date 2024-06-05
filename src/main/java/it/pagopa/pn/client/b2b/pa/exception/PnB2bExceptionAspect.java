package it.pagopa.pn.client.b2b.pa.exception;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;


@Aspect
@Component
@Slf4j
public class PnB2bExceptionAspect {

    @AfterThrowing(pointcut = "execution(* it.pagopa.pn..*.*(..))", throwing = "ex")
    public void handleExceptions(Exception ex) {
        log.error("Exception intercepted: "+ex);
    }


}
