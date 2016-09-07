package com.schibsted.metricsaws;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class TestMetricsContext {

    @Mock
    private MetricRegistry metricRegistry;

    @Mock
    private Timer timer;

    @Mock
    private Counter counter;

    private Class<?> delegateInterface = AmazonSQS.class;

    private String metricPrefix = "testPrefix";

    private MetricsContext metricsContext;

    @Before
    public void setup() throws Exception {
        metricsContext = new MetricsContext(metricRegistry, delegateInterface, metricPrefix);
    }

    @Test
    public void shouldIncrementCounter() throws Exception {
        // given
        given(metricRegistry.counter(anyString())).willReturn(counter);
        // when
        metricsContext.incrementCounter("somePostfix");
        // then
        verify(metricRegistry, times(1)).counter("testPrefix." + delegateInterface.getSimpleName() + ".somePostfix");
        verify(counter, times(1)).inc();
        verifyNoMoreInteractions(metricRegistry);
    }

    @Test
    public void shouldCreateNewTimer() throws Exception {
        // given
        given(metricRegistry.timer(anyString())).willReturn(timer);
        // when
        Timer result = metricsContext.timer("somePostfix");
        // then
        assertNotNull(result);
        assertEquals(timer, result);
        verify(metricRegistry, times(1)).timer("testPrefix." + delegateInterface.getSimpleName() + ".somePostfix");
        verifyNoMoreInteractions(metricRegistry);
    }

    @Test
    public void shouldReturnTrueForMethodDefinedInInterface() throws Exception {
        // when
        boolean shouldBeMetered = metricsContext.shouldBeMetered(AmazonSQSClient.class.getMethod("hashCode"));
        // then
        assertFalse(shouldBeMetered);
    }

    @Test
    public void shouldReturnFalseForMethodNotDefinedInInterface() throws Exception {
        // when
        boolean shouldBeMetered = metricsContext
                .shouldBeMetered(AmazonSQSClient.class.getMethod("sendMessage", String.class, String.class));
        // then
        assertTrue(shouldBeMetered);
    }

}
