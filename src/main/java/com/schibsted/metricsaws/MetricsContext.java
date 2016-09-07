package com.schibsted.metricsaws;

import java.lang.reflect.Method;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

class MetricsContext {

    private MetricRegistry metricRegistry;

    private Class<?> delegateInterface;

    private String metricPrefix;

    public MetricsContext(MetricRegistry metricRegistry, Class<?> delegateInterface, String metricPrefix) {
        this.metricRegistry = metricRegistry;
        this.delegateInterface = delegateInterface;
        this.metricPrefix = metricPrefix;
    }

    public void incrementCounter(String postfix) {
        metricRegistry.counter(getName(postfix)).inc();
    }

    private String getName(String postfix) {
        return metricPrefix + "." + delegateInterface.getSimpleName() + "." + postfix;
    }

    public Timer timer(String postfix) {
        return metricRegistry.timer(getName(postfix));
    }

    public boolean shouldBeMetered(Method method) {
        try {
            delegateInterface.getDeclaredMethod(method.getName(), method.getParameterTypes());
            return true;
        } catch (NoSuchMethodException | SecurityException e) {
            return false;
        }
    }

}
