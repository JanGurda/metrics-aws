package com.schibsted.metricsaws;

import java.lang.reflect.Method;

import com.codahale.metrics.Timer;
import com.schibsted.metricsaws.sample.Service;
import com.schibsted.metricsaws.sample.ServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestMetricsInvocationHandler {

    @Mock
    private MetricsContext metricsContext;

    @Mock
    private ExceptionHandler exceptionHandler;

    @Mock
    private Timer timer;

    @Mock
    private Timer.Context timerContext;

    @Mock
    private Object proxy;

    private ServiceImpl delegate;

    Method method;

    private Object[] args = new Object[0];

    @Mock
    private DelegateMethodExecutor delegateMethodExecutor;

    private MetricsInvocationHandler<Service> metricsInvocationHandler;

    @Before
    public void setup() throws Exception {
        metricsInvocationHandler = new MetricsInvocationHandler<Service>(metricsContext, delegate, delegateMethodExecutor,
                exceptionHandler);
        given(metricsContext.timer(anyString())).willReturn(timer);
        given(timer.time()).willReturn(timerContext);
        given(metricsContext.shouldBeMetered(any(Method.class))).willReturn(true);
        method = Service.class.getMethod("ping", String.class);

    }

    @Test
    public void shouldInvokeMethodOnDelegateObjectWithGivenArguments() throws Throwable {
        // when
        metricsInvocationHandler.invoke(proxy, method, args);
        // then
        verify(delegateMethodExecutor, times(1)).invoke(eq(delegate), eq(method), eq(args));
    }

    @Test
    public void shouldReturnResultFromDelegate() throws Throwable {
        // given
        given(delegateMethodExecutor.invoke(any(), any(Method.class), any(Object[].class))).willReturn("Answer");
        // when
        Object result = metricsInvocationHandler.invoke(proxy, method, args);
        // then
        assertEquals("Answer", result);
    }

    @Test
    public void shouldInvokeMethodOnDelegateObjectWithGivenArgumentsWhenNotLoggingMetrics() throws Throwable {
        // given
        given(metricsContext.shouldBeMetered(any(Method.class))).willReturn(false);
        // when
        metricsInvocationHandler.invoke(proxy, method, args);
        // then
        verify(delegateMethodExecutor, times(1)).invoke(eq(delegate), eq(method), eq(args));
    }

    @Test
    public void shouldReturnResultFromDelegateWhenNotLoggingMetrics() throws Throwable {
        // given
        given(metricsContext.shouldBeMetered(any(Method.class))).willReturn(false);
        given(delegateMethodExecutor.invoke(any(), any(Method.class), any(Object[].class))).willReturn("Answer");
        // when
        Object result = metricsInvocationHandler.invoke(proxy, method, args);
        // then
        assertEquals("Answer", result);
    }

    @Test
    public void shouldMeasureTimeOfInvocation() throws Throwable {
        // when
        metricsInvocationHandler.invoke(proxy, method, args);
        // then
        verify(metricsContext, times(1)).timer("ping");
        verify(timer, times(1)).time();
        verify(timerContext, times(1)).stop();
    }

    @Test
    public void shouldNotMeasureTimeOfInvocation() throws Throwable {
        // when
        given(metricsContext.shouldBeMetered(any(Method.class))).willReturn(false);
        metricsInvocationHandler.invoke(proxy, method, args);
        // then
        verify(metricsContext, times(0)).timer(anyString());
    }

    @Test
    public void shouldMeasureTimeOfInvocationWhenExceptionIsThrown() throws Throwable {
        // given
        given(delegateMethodExecutor.invoke(any(), any(Method.class), any(Object[].class))).willThrow(new RuntimeException());
        // when
        try {
            metricsInvocationHandler.invoke(proxy, method, args);
        // then
        } catch (RuntimeException e) {
            verify(metricsContext, times(1)).timer("ping");
            verify(timer, times(1)).time();
            verify(timerContext, times(1)).stop();
        }
    }

    @Test
    public void shouldHanldeExceptions() throws Throwable {
        // given
        RuntimeException runtimeException = new RuntimeException();
        given(delegateMethodExecutor.invoke(any(), any(Method.class), any(Object[].class))).willThrow(runtimeException);
        // when
        try {
            metricsInvocationHandler.invoke(proxy, method, args);
        // then
        } catch (RuntimeException e) {
            verify(exceptionHandler, times(1)).onException(eq(method), eq(runtimeException));
        }
    }
}
