package com.schibsted.metricsaws;

import java.lang.reflect.Method;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.InvalidIdFormatException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TestExceptionHandler {

    @Mock
    private MetricsContext metricsContext;

    @InjectMocks
    private ExceptionHandler exceptionHandler;

    @Test
    public void shouldIncrementMetricForClientException() throws Exception {
        // given
        Method method = AmazonSQS.class.getMethod("sendMessage", String.class, String.class);
        // when
        exceptionHandler.onException(method, new AmazonClientException("Some message"));
        // then
        verify(metricsContext, times(1)).incrementCounter("sendMessage.clientError[type: AmazonClientException]");
    }

    @Test
    public void shouldIncrementMetricForServerException() throws Exception {
        // given
        Method method = AmazonSQS.class.getMethod("sendMessage", String.class, String.class);
        // when
        exceptionHandler.onException(method, new InvalidIdFormatException("Some message"));
        // then
        verify(metricsContext, times(1)).incrementCounter("sendMessage.serviceError[type: InvalidIdFormatException]");
    }

    @Test
    public void shouldIncrementMetricForOtherException() throws Exception {
        // given
        Method method = AmazonSQS.class.getMethod("sendMessage", String.class, String.class);
        // when
        exceptionHandler.onException(method, new NullPointerException("Some message"));
        // then
        verify(metricsContext, times(1)).incrementCounter("sendMessage.otherError[type: NullPointerException]");
    }

}
