//package com.demo.spring.ai.retry;
//
//import org.springframework.context.event.EventListener;
//
///**
// * Spring Framework 7 and Spring Boot 4.0.2 introduce the MethodRetryEvent class. https://www.baeldung.com/spring-retry
// */
//public class RetryEventListener {
//    @EventListener
//    public void onRetryEvent(MethodRetryEvent event) {
//        String methodName = event.getMethod()
//                .getName();
//        Throwable exception = event.getFailure();
//
//        if (event.isRetryAborted()) {
//            log.error(
//                    "Retries exhausted for method '{}' after {} attempts. Final exception: {}",
//                    methodName,
//                    exception.getMessage()
//            );
//        } else {
//            log.warn("Retry failed for method '{}'. Exception: {}", methodName, exception.getMessage());
//        }
//    }
//}
