package com.syncron;

import com.here.oksse.OkSse;
import com.here.oksse.ServerSentEvent;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ClientApplication {

    private static final Logger logger = LoggerFactory.getLogger(ClientApplication.class);


    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(ClientApplication.class, args);
//        Disposable disposable = context.getBean(Tester.class).test();
//        while(!disposable.isDisposed()) {
//            Thread.sleep(1);
//        }

        Request request = new Request.Builder().url("http://localhost:8080/order-stream").build();

        OkSse sse = new OkSse();
        ServerSentEvent.Listener listener = new ServerSentEvent.Listener() {
            @Override
            public void onOpen(ServerSentEvent sse, Response response) {
                System.out.println("Started listening");
            }

            @Override
            public void onMessage(ServerSentEvent sse, String id, String event, String message) {
                System.out.println(message);
            }

            @Override
            public void onComment(ServerSentEvent sse, String comment) {

            }

            @Override
            public boolean onRetryTime(ServerSentEvent sse, long milliseconds) {
                return true;
            }

            @Override
            public boolean onRetryError(ServerSentEvent sse, Throwable throwable, Response response) {
                return false;
            }

            @Override
            public void onClosed(ServerSentEvent sse) {
                System.out.println("Stopped listening");
            }

            @Override
            public Request onPreRetry(ServerSentEvent sse, Request originalRequest) {
                return originalRequest;
            }
        };
        sse.newServerSentEvent(request, listener);
        System.out.println("AA");
        Thread.sleep(10 * 1000);

    }

}
