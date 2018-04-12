package com.syncron;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.here.oksse.OkSse;
import com.here.oksse.ServerSentEvent;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class SSEListenerFactory {

    private static final Logger logger = LoggerFactory.getLogger(SSEListenerFactory.class);
    private final String baseURL;
    @Autowired
    ObjectMapper objectMapper;

    public SSEListenerFactory(String baseURL) {
        this.baseURL = baseURL;
    }

    public <T> Observable<T> createListener(String path, Class<T> messageType) {
        Subject<T> resultSubject = PublishSubject.create();

        HttpUrl url = HttpUrl.parse(baseURL).resolve(path);
        Request request = new Request.Builder().url(url).build();
        OkSse sse = new OkSse();
        ServerSentEvent.Listener listener = new ServerSentEvent.Listener() {

            @Override
            public void onOpen(ServerSentEvent sse, Response response) {
                logger.info("Started listening to {}", url);
            }

            @Override
            public void onMessage(ServerSentEvent sse, String id, String event, String message) {
                try {
                    T obj = objectMapper.readValue(message, messageType);
                    resultSubject.onNext(obj);
                } catch (IOException e) {
                    resultSubject.onError(e);
                }
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
                logger.info("Stopped listening to {}", url);
            }

            @Override
            public Request onPreRetry(ServerSentEvent sse, Request originalRequest) {
                return originalRequest;
            }
        };
        ServerSentEvent event = sse.newServerSentEvent(request, listener);

        return resultSubject
                .doOnDispose(event::close)
                .observeOn(Schedulers.io());
    }
}
