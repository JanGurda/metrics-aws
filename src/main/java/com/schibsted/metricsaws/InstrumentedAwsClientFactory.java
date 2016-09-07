package com.schibsted.metricsaws;

import java.lang.reflect.Proxy;

import com.codahale.metrics.MetricRegistry;

public class InstrumentedAwsClientFactory {

    private MetricRegistry metricRegistry;

    public InstrumentedAwsClientFactory(MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @SuppressWarnings("unchecked")
    public <T> T instrument(Class<T> clientInterface, T clientImplementation, String metricsPrefix) {
        checkParams(clientInterface);
        MetricsContext metricsContext = new MetricsContext(metricRegistry, clientInterface, metricsPrefix);
        return ((T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class<?>[] {clientInterface}, 
                new MetricsInvocationHandler<T>(metricsContext , 
                        clientImplementation, 
                        new DelegateMethodExecutor(), 
                        new ExceptionHandler(metricsContext))));
    }

    private void checkParams(Class<?> clientInterface) {
        if (!clientInterface.isInterface()) {
            throw new IllegalArgumentException(String.format("%s is not an interface.", clientInterface.getSimpleName()));
        }
    }
}
