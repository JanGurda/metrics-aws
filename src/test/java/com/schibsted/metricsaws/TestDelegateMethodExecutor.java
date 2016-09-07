package com.schibsted.metricsaws;

import java.lang.reflect.Method;

import com.schibsted.metricsaws.sample.Service;
import com.schibsted.metricsaws.sample.ServiceImpl;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
}
