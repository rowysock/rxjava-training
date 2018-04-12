package com.syncron;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;

public class ReactiveFileWriter {

    private static final Logger log = LoggerFactory.getLogger(ReactiveFileWriter.class);
    private static final byte[] LINE_SEPARATOR = new byte[]{'\r', '\n'};
    @Autowired
    ObjectMapper mapper;

    public <T> ObservableConverter<T, Completable> file(Path destination) {
        return lines -> writeLinesTo(lines, destination);
    }

    private Completable writeLinesTo(Observable<?> objects, Path destination) {
        AtomicInteger lineCount = new AtomicInteger();
        return Completable.using(
                () -> FileChannel.open(destination, StandardOpenOption.WRITE),
                channel -> objects.flatMapCompletable(object -> Completable.fromAction(() -> {
                    log.debug("Writing to file {} line {}", destination.getFileName(), lineCount.getAndIncrement());
                    String line = mapper.writeValueAsString(object);
                    channel.write(StandardCharsets.UTF_8.encode(line));
                    channel.write(ByteBuffer.wrap(LINE_SEPARATOR));
                })),
                FileChannel::close);
    }
}
