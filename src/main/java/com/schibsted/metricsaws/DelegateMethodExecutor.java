package com.schibsted.metricsaws;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class DelegateMethodExecutor {

    public Object invoke(Object delegate, Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(delegate, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
