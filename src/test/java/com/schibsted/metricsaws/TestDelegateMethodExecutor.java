package com.schibsted.metricsaws;

import java.lang.reflect.Method;

import com.amazonaws.AmazonClientException;
import com.schibsted.metricsaws.sample.Service;
import com.schibsted.metricsaws.sample.ServiceImpl;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;

public class TestDelegateMethodExecutor {

    DelegateMethodExecutor executor = new DelegateMethodExecutor();

    @Test
    public void shouldInvokeMethod() throws Throwable {
        // given
        Method method = Service.class.getMethod("ping", String.class);
        ServiceImpl impl = new ServiceImpl();
        // when
        Object result = executor.invoke(impl, method, new Object[] { "ping" });
        // then
        assertNotNull(result);
        assertEquals(impl.ping("ping"), result);
    }

    @Test(expected = AmazonClientException.class)
    public void shouldUnwrapExceptionFromInvocationTargetException() throws Throwable {
        // given
        Method method = Service.class.getMethod("ping", String.class);
        Service serviceMock = Mockito.mock(Service.class);
        given(serviceMock.ping(anyString())).willThrow(new AmazonClientException("some message"));
        // when
        executor.invoke(serviceMock, method, new Object[] { "ping" });
        // then
    }
}
