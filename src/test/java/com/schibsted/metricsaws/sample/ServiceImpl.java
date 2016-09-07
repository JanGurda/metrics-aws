package com.schibsted.metricsaws.sample;


public class ServiceImpl implements Service {

    public String ping(String arg) {
        return "pong:" + arg;
    }

}
