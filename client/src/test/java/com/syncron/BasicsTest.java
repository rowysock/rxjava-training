package com.syncron;

import io.reactivex.Observable;
import io.reactivex.ObservableConverter;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;
import static org.assertj.core.util.Lists.newArrayList;

/**
 * https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/legend.png
 */
public class BasicsTest {

    private static final Logger logger = LoggerFactory.getLogger(BasicsTest.class);

    /**
     * https://github.com/ReactiveX/RxJava/wiki/Creating-Observables
     */
    @Test
    public void creation() {
        Observable<Integer> result = null;

        result.test().assertValues(5, 10, 15);
    }

    /**
     * http://reactivex.io/documentation/operators/map.html
     */
    @Test
    public void map() {
        Observable<Integer> input = Observable.range(1, 3);

        Observable<Integer> result = null;

        result.test().assertValues(5, 10, 15);
    }

    /**
     * http://reactivex.io/documentation/operators/filter.html
     */
    @Test
    public void filter() {
        Observable<Integer> input = Observable.range(1, 15);

        Observable<Integer> result = null;

        result.test().assertValues(5, 10, 15);
    }

    /**
     * http://reactivex.io/documentation/operators/skip.html
     * http://reactivex.io/documentation/operators/take.html
     */
    @Test
    public void skipTake() {
        Observable<String> input = Observable.just("A", "B", "C", "D", "E", "F", "G");

        Observable<String> result = null;

        result.test().assertValues("C", "D", "E");
    }

    /**
     * http://reactivex.io/documentation/operators/flatmap.html
     */
    @Test
    public void flatMap() {
        Observable<Integer> input = Observable.just(1, 2, 3);

        // Should map each element to itself and negative
        Observable<Integer> result = input.flatMap(null);

        result.test().assertValues(1, -1, 2, -2, 3, -3);
    }

    /**
     * http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#flattenAsObservable-io.reactivex.functions.Function-
     */
    @Test
    public void flattenAsObservable() {
        Single<List<Integer>> input = Single.just(Lists.newArrayList(1, 2, 3));

        // Should convert Single holding collection to Observable
        Observable<Integer> result = null;

        result.test().assertValues(1, 2, 3);
    }

    /**
     * http://reactivex.io/documentation/operators/repeat.html
     */
    @Test
    public void repeat() {
        Observable<Integer> input = Observable.just(1, 2, 3);

        Observable<Integer> result = input.flatMap(null);

        result.test().assertValues(1, 2, 2, 3, 3, 3);
    }

    /**
     * http://reactivex.io/documentation/operators/first.html
     */
    @Test
    public void first() throws Exception {
        ObservableConverter<String, Single<String>> firstOrX = null;

        Observable<String> inputWithValues = Observable.just("A", "B", "C");
        //Use as function
        firstOrX.apply(inputWithValues).test().assertValue("A");

        Observable<String> inputEmpty = Observable.empty();
        //Use with .as() operator
        inputEmpty.as(firstOrX).test().assertValue("X");
    }

    /**
     * http://reactivex.io/documentation/operators/zip.html
     */
    @Test
    public void zip() {
        Observable<String> letters = Observable.just("A", "B", "C");
        Observable<Integer> numbers = Observable.just(12, 13, 14);

        Observable<String> result = Observable.zip(null, null, null);

        // Two observables combined into one
        result.test().assertValues("A12", "B13", "C14");
    }

    /**
     * http://reactivex.io/documentation/operators/delay.html
     */
    @Test
    public void delay() throws InterruptedException {
        Observable<Integer> input = Observable.just(1);

        Observable<Integer> result = null;

        TestObserver<Integer> observer = result.test();
        // Nothing at first
        observer.assertNoValues();
        Thread.sleep(1050);
        // a value after 1 second (with a little bit of a margin)
        observer.assertValues(1);
    }

    /**
     * http://reactivex.io/documentation/operators/subscribeon.html
     */
    @Test
    public void subscribeOn() throws InterruptedException {
        Observable<Integer> input = Observable.just(1, 2, 3);

        // Different implementation od delay
        Function<Integer, Observable<Integer>> delay = i -> Observable.fromCallable(() -> {
            logger.debug(Thread.currentThread().getName());
            Thread.sleep(1000);
            return i;
        });

        Observable<Integer> result = null;

        long timeBefore = System.currentTimeMillis();
        result.test().await();
        long timeAfter = System.currentTimeMillis();
        long difference = timeAfter - timeBefore;
        // All 3 operations should be evaluated at the same time
        assertThat(difference).isCloseTo(1000L, withPercentage(50));
    }

    /**
     * http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html#concatMap-io.reactivex.functions.Function-
     * http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html#concatMapEager-io.reactivex.functions.Function-
     */
    @Test
    public void concatMap() throws InterruptedException {
        Observable<Integer> input = Observable.just(3, 2, 1);

        Function<Integer, Observable<Integer>> delay = i -> Observable.fromCallable(() -> {
            // First element is delayed by 300ms, second by 200ms and third by 100ms
            Thread.sleep(100 * i);
            return i;
        });

        Observable<Integer> result = null;

        // All 3 operations should be evaluated at the same time, while order of items should be the same as original
        result.test()
                .awaitDone(350, MILLISECONDS)
                .assertNoTimeout()
                .assertValues(3, 2, 1);
    }

    /**
     * http://reactivex.io/documentation/operators/subscribe.html
     */
    @Test
    public void subscribe() {
        AtomicInteger counter = new AtomicInteger();

        Observable<String> observable = Observable.just("A", "B", "C");

        // should consume this observable

        assertThat(counter).hasValue(3);
    }

    /**
     * http://reactivex.io/documentation/operators/publish.html
     * http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html#publish--
     * http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html#cache--
     */
    @Test
    public void publish() {
        AtomicInteger counter = new AtomicInteger();

        Observable<String> input = Observable.just("A", "B", "C")
                .doOnNext(i -> counter.incrementAndGet());

        // should evaluate source observable only once
        Observable<String> result = null;

        result.subscribe();
        result.subscribe();

        assertThat(counter).hasValue(3);
    }

    /**
     * https://github.com/ReactiveX/RxJava/wiki/Error-Handling-Operators
     */
    @Test
    public void error() {
        Observer<String> mockObserver = Mockito.mock(Observer.class);
        Exception exceptionAtB = new RuntimeException("I hate letter B");

        Observable<String> letters = Observable.just("A", "B", "C");
        // Should fail at letter "B", passing through others
        Function<String, String> failAtB = l -> null;

        letters.map(failAtB)
                .subscribe(mockObserver);

        InOrder inOrder = Mockito.inOrder(mockObserver);
        inOrder.verify(mockObserver).onNext("A");
        inOrder.verify(mockObserver).onError(exceptionAtB);
        // Nothing happened for letter "C"
        inOrder.verifyNoMoreInteractions();
    }

    /**
     * http://reactivex.io/documentation/operators/catch.html
     * http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/Observable.html#onErrorResumeNext-io.reactivex.ObservableSource-
     */
    @Test
    public void onErrorResumeNext() {
        Observable<String> original = Observable.create(emitter -> {
            emitter.onNext("A");
            emitter.onError(new RuntimeException("Some error"));
        });
        Observable<String> fallback = Observable.just("B", "C");

        // Should resume with fallback observable after original failed
        Observable<String> result = null;

        result.test().assertValues("A", "B", "C");
    }

    /**
     * http://reactivex.io/RxJava/javadoc/rx/subjects/PublishSubject.html
     */
    @Test
    public void publishSubject() {
        PublishSubject<Object> subject = PublishSubject.create();

        TestObserver<Object> testObserver1 = subject.test();

        // Something is missing here

        testObserver1.assertValues("A", "B", "C");
    }

    /**
     * http://reactivex.io/documentation/subject.html
     */
    @Test
    public void subjectHoldingState() {
        // What kind of subject should it be?
        Subject<String> subject = null;

        TestObserver<String> testObserver1 = subject.test();

        subject.onNext("A");
        subject.onNext("B");

        TestObserver<String> testObserver2 = subject.test();

        subject.onNext("C");

        testObserver1.assertValues("A", "B", "C");
        testObserver2.assertValues("B", "C");
    }

    /**
     * http://reactivex.io/documentation/operators/buffer.html
     */
    @Test
    public void buffer() throws InterruptedException {
        // numbers from 1 to 4 emitted every 10 ms after 35 ms initial delay
        Observable<Integer> numbers = Observable.intervalRange(1, 4, 30, 10, MILLISECONDS)
                .map(Long::intValue);

        // Should gather numbers every 30 ms
        Observable<List<Integer>> result = null;

        // 30 - gather nothing
        // 35 - emit 1
        // 45 - emit 2
        // 55 - emit 3
        // 60 - gather 1, 2, 3
        // 65 - emit 4
        // complete - gather 4
        result.test()
                .await()
                .assertValues(
                        newArrayList(),
                        newArrayList(1, 2, 3),
                        newArrayList(4));

    }

    /**
     * http://reactivex.io/documentation/operators/merge.html
     * http://reactivex.io/documentation/operators/concat.html
     */
    @Test
    public void combine() throws InterruptedException {
        Observable<Observable<String>> inputs = Observable.just(
                Observable.just("B").delay(200, MILLISECONDS),
                Observable.just("C").delay(300, MILLISECONDS),
                Observable.just("A").delay(100, MILLISECONDS));

        // Should combine Observables as evaluated at the same time
        Observable<String> parallel = null;

        // Should combine Observables as evaluated one after another
        Observable<String> sequential = null;

        parallel.test().await()
                .assertValues("A", "B", "C");

        sequential.test().await()
                .assertValues("B", "C", "A");
    }

}
