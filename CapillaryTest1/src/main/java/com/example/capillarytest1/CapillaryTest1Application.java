package com.example.capillarytest1;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.capillarytest1.MonitoringService;
import com.example.capillarytest1.DefaultCircuitBreaker;
import com.google.gwt.user.client.rpc.RemoteService;
import com.sun.org.slf4j.internal.LoggerFactory;

@SpringBootApplication
public class CapillaryTest1Application {
    long serverStartTime = System.nanoTime();

    RemoteService delayedService = (RemoteService) new DelayedRemoteService(serverStartTime, 5);
    CircuitBreaker delayedServiceCircuitBreaker = (CircuitBreaker) new DefaultCircuitBreaker(delayedService, 3000, 2,
            2000 * 1000 * 1000);

    RemoteService quickService = new QuickRemoteService();
    CircuitBreaker quickServiceCircuitBreaker = (CircuitBreaker) new DefaultCircuitBreaker(quickService, 3000, 2,
            2000 * 1000 * 1000);

    //Create an object of monitoring service which makes both local and remote calls
    MonitoringService monitoringService = new MonitoringService(delayedServiceCircuitBreaker, quickServiceCircuitBreaker);

    //Fetch response from local resource
     System.out(monitoringService.localResourceResponse());

    //Fetch response from delayed service 2 times, to meet the failure threshold
    System.out(monitoringService.delayedServiceResponse());
    System.out(monitoringService.delayedServiceResponse());

    //Fetch current state of delayed service circuit breaker after crossing failure threshold limit
    //which is OPEN now
    System.out(delayedServiceCircuitBreaker.getState());

    //Meanwhile, the delayed service is down, fetch response from the healthy quick service
    System.out(monitoringService.quickServiceResponse());
    System.out(quickServiceCircuitBreaker.getState());

    //Wait for the delayed service to become responsive
            System.out.("Waiting for delayed service to become responsive");
    try  {
        Thread.sleep(5000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    //Check the state of delayed circuit breaker, should be HALF_OPEN
    System.out(delayedServiceCircuitBreaker.getState());

    //Fetch response from delayed service, which should be healthy by now
    System.out(monitoringService.delayedServiceResponse());
    //As successful response is fetched, it should be CLOSED again.
    System.out(delayedServiceCircuitBreaker.getState());

    public static void main(String[] args) {
        SpringApplication.run(CapillaryTest1Application.class, args);
    }

}
