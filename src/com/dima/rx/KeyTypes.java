package com.dima.rx;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;
import rx.subjects.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 *  Based on <a href="http://www.introtorx.com/content/v1.0.10621.0/02_KeyTypes.html">http://www.introtorx.com/content/v1.0.10621.0/02_KeyTypes.html</a>
 */
public class KeyTypes implements Main.Snippet {

  public static class MyConsoleObserver<T> implements Observer<T> {

    @Override
    public void onCompleted() {
      System.out.println("Sequence terminated");
    }

    @Override
    public void onError(Throwable e) {
      System.out.printf("Sequence faulted with %s\n", e);
    }

    @Override
    public void onNext(T t) {
      System.out.printf("Received value %s\n", t);
    }
  }

  public static class MySequenceOfNumbers extends Observable<Integer> {

    public MySequenceOfNumbers() {
      super(subscriber -> {
        subscriber.onNext(1);
        subscriber.onNext(2);
        subscriber.onNext(3);
        subscriber.onCompleted();
      });
    }


  }

  @Override
  public void run() {
    MySequenceOfNumbers numbers = new MySequenceOfNumbers();
    MyConsoleObserver<Integer> observer = new MyConsoleObserver<>();
    numbers.subscribe(observer);


    Subject<String, String> subject = PublishSubject.create();
    subject.onNext("a0");
    writeSequenceToConsole(subject);
    subject.onNext("b0");
    subject.onNext("c0");


    Subject<String, String> replaySubject = ReplaySubject.create();
    replaySubject.onNext("a1");
    writeSequenceToConsole(replaySubject);
    replaySubject.onNext("b1");
    replaySubject.onNext("c1");


    int bufferSize = 2;
    Subject<String, String> replayWithBuffer = ReplaySubject.createWithSize(bufferSize);
    replayWithBuffer.onNext("a2");
    replayWithBuffer.onNext("b2");
    replayWithBuffer.onNext("c2");
    replayWithBuffer.onNext("d2");
    writeSequenceToConsole(replayWithBuffer);
    replayWithBuffer.onNext("e2");
    replayWithBuffer.onCompleted();
    // This guy will have 2 values received, even though the sequence is closed.
    writeSequenceToConsole(replayWithBuffer);


    int timeWindow = 20;
    Subject<String, String> replayWithTime =
            ReplaySubject.createWithTime(timeWindow, TimeUnit.MILLISECONDS, Schedulers.immediate());
    replayWithTime.onNext("a3");
    waiter(10);
    replayWithTime.onNext("b3");
    waiter(10);
    writeSequenceToConsole(replayWithTime);
    replayWithTime.onNext("c3");

    BehaviorSubject<String> behaviorSubject = BehaviorSubject.create("boom!");
    writeSequenceToConsole(behaviorSubject);
    behaviorSubject.onNext("bada boom!");
    behaviorSubject.onCompleted();
    writeSequenceToConsole(behaviorSubject);
    behaviorSubject.onNext("Will not be printed, as the sequence was closed.");


    AsyncSubject<String> asyncSubject = AsyncSubject.create();
    writeSequenceToConsole(asyncSubject);
    asyncSubject.onNext("This won't ");
    asyncSubject.onNext(" be ");
    asyncSubject.onNext(" written ");
    asyncSubject.onNext(" before onComplete ");
    asyncSubject.onNext(" onComplete !");
    asyncSubject.onCompleted();

    whiskey();
  }

  private void waiter(long time) {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void whiskey() {
    try {
      System.in.read();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void writeSequenceToConsole(Observable<String> observable) {
    observable.subscribe(System.out::println);
  }
}
