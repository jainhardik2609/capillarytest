package com.example.capillarytest1;

public class DelayedRemoteService implements CircuitBreaker {
    public DelayedRemoteService(long serverStartTime, int i) {
    }

    @Override
    public String attemptRequest() {
        return null;
    }
}
