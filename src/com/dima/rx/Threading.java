package com.dima.rx;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by dima on 6/16/16.
 */
public class Threading implements Main.Snippet {

  static Observable<String> create() {
    return Observable.create(new Observable.OnSubscribe<String>() {
      @Override
      public void call(Subscriber<? super String> subscriber) {
        String s = String.format("Produced on: %s", Thread.currentThread().getName());
        subscriber.onNext(s);
        subscriber.onCompleted();
      }
    });
  }

  final Action1<String> action = production -> {
    String consumption = String.format("Consumed on: %s", Thread.currentThread().getName());
    System.out.println(production);
    System.out.println(consumption);
  };

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

    try {
      // take a time
      Thread.sleep(100000);
    } catch (InterruptedException e) {}
  }


}
