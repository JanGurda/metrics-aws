package com.schibsted.metricsaws;

import com.codahale.metrics.MetricRegistry;
import com.schibsted.metricsaws.sample.Service;
import com.schibsted.metricsaws.sample.ServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class TestInstrumentedAwsClientFactory {

    @Mock
    private MetricRegistry metricRegistry;

    @InjectMocks
    private InstrumentedAwsClientFactory instrumentedAwsClientFactory;

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenPassingNonInterface() throws Exception {
        // when
        instrumentedAwsClientFactory.instrument(Object.class, null, "somePrefix");
    }

    @Test
    public void shouldConstructProxyInstance() throws Exception {
        // when
        Service instrumented = instrumentedAwsClientFactory.instrument(Service.class, new ServiceImpl(), "somePrefix");
        // then
        assertNotNull(instrumented);
    }

}
