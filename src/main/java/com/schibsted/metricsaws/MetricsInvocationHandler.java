package com.schibsted.metricsaws;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.codahale.metrics.Timer;

class MetricsInvocationHandler<T> implements InvocationHandler {

    private DelegateMethodExecutor delegateMethodExecutor;

    private T delegate;

    private ExceptionHandler exceptionHandler;

    private MetricsContext metricsContext;

    MetricsInvocationHandler(MetricsContext metricsContext, T delegate, DelegateMethodExecutor delegateMethodExecutor,
            ExceptionHandler exceptionHandler) {
        this.metricsContext = metricsContext;
        this.delegateMethodExecutor = delegateMethodExecutor;
        this.delegate = delegate;
        this.exceptionHandler = exceptionHandler;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (metricsContext.shouldBeMetered(method)) {
            return invokeInMeasuringMetrics(method, args);
        } else {
            return invokeWithNoMetrics(method, args);
        }
    }

    private Object invokeInMeasuringMetrics(Method method, Object[] args) throws Throwable {
        Timer.Context timer = metricsContext.timer(method.getName()).time();
        try {
            return delegateMethodExecutor.invoke(delegate, method, args);
        } catch (AmazonServiceException e) {
            exceptionHandler.onException(method, e);
            throw e;
        } catch (AmazonClientException e) {
            exceptionHandler.onException(method, e);
            throw e;
        } catch (Throwable e) {
            exceptionHandler.onException(method, e);
            throw e;
        } finally {
            timer.stop();
        }
    }

    private Object invokeWithNoMetrics(Method method, Object[] args) throws Throwable {
        try {
            return delegateMethodExecutor.invoke(delegate, method, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } catch (Exception e) {
            throw e;
        }
    }

}
