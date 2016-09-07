package com.schibsted.metricsaws;

import java.lang.reflect.Method;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;

class ExceptionHandler {

    private MetricsContext metricsContext;

    ExceptionHandler(MetricsContext metricsContext) {
        this.metricsContext = metricsContext;
    }

    void onException(Method method, AmazonServiceException serverException) {
        metricsContext.incrementCounter(getServiceErrorMetricName(method, serverException));
    }


    private String getServiceErrorMetricName(Method method, AmazonServiceException e) {
        return method.getName() + ".serviceError" +
                String.format("[type: %s, status: %d, errorCode:%s]", e.getClass().getSimpleName(), e.getStatusCode(), e.getErrorCode());
    }

    void onException(Method method, AmazonClientException clientException) {
        metricsContext.incrementCounter(getName("clientError", method, clientException));
    }

    void onException(Method method, Throwable otherException) {
        metricsContext.incrementCounter(getName("otherError", method, otherException));
    }

    private String getName(String errorKind, Method method, Throwable exception) {
        return method.getName() + "." + errorKind + String.format("[type: %s]", exception.getClass().getSimpleName());
    }
}
