package com.dima.rx;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.schedulers.Schedulers;
import rx.util.async.Async;

/**
 * Created by dima on 6/16/16.
 */
public class Threading implements Main.Snippet {

  static Observable<String> create() {
    return Observable.create(new Observable.OnSubscribe<String>() {
      @Override
      public void call(Subscriber<? super String> subscriber) {
        String s = addThreadName("Produced on: %s");
        subscriber.onNext(s);
        subscriber.onCompleted();
      }
    });
  }

  final Action1<String> action = production -> {
    String consumption = addThreadName("Consumed on: %s");
    System.out.println(production);
    System.out.println(consumption);
  };

  private static String addThreadName(String format) {
    return String.format(format, Thread.currentThread().getName());
  }

  @Override
  public void run() {

    create().subscribe(action);

    create()
            .subscribeOn(Schedulers.newThread())
            .subscribe(action);

    create()
            .observeOn(Schedulers.newThread())
            .subscribe(action);

    create()
        .subscribeOn(Schedulers.computation())
        .observeOn(Schedulers.io())
        .subscribe(action);


    Async.start(() -> addThreadName("start() Asynchronously produced on: %s"))
            .subscribe(action);

    Async.runAsync(
            Schedulers.newThread(),
            new Action2<Observer<? super String>, Subscription>() {
              @Override
              public void call(Observer<? super String> observer, Subscription subscription) {
                observer.onNext(addThreadName("runAsync() produced %s"));
                observer.onCompleted();
              }
            })
            .subscribe(action);

    Async
        .fromAction(
            () -> { action.call(addThreadName("fromAction() %s")); }, "fromAction() result", Schedulers.newThread())
        .subscribe(action);

    try {
      // take a time
      Thread.sleep(100000);
    } catch (InterruptedException e) {}
  }


}
