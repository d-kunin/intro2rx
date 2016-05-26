package com.dima.rx;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.dima.rx.RxHelp.writeSequenceToConsole;
import static com.dima.rx.RxHelp.writeStreamToConsole;


public class CreatingSequence implements Main.Snippet {
  @Override
  public void run() {

    writeStreamToConsole(Observable.just("one"));

    writeStreamToConsole(Observable.empty());

    writeStreamToConsole(Observable.never()); // never does anything

    writeStreamToConsole(Observable.error(new RuntimeException("Boom!")));

    // empty
    writeStreamToConsole(Observable.create(new Observable.OnSubscribe<String>() {
      @Override
      public void call(Subscriber<? super String> subscriber) {
        subscriber.onCompleted();
      }
    }));

    // just
    writeStreamToConsole(Observable.create(new Observable.OnSubscribe<String>() {
      @Override
      public void call(Subscriber<? super String> subscriber) {
        subscriber.onNext("The value");
        subscriber.onCompleted();
      }
    }));

    // Never
    writeStreamToConsole(Observable.create(new Observable.OnSubscribe<String>() {
      @Override
      public void call(Subscriber<? super String> subscriber) {
        System.out.println("I do nothing!");
      }
    }));

    // Throw
    writeStreamToConsole(Observable.create(new Observable.OnSubscribe<String>() {
      @Override
      public void call(Subscriber<? super String> subscriber) {
        subscriber.onError(new RuntimeException());
      }
    }));


    Observable<String> timerObservable = createTimerWithSubscriberToDispose();
    Subscription timerSubscription = writeSequenceToConsole(timerObservable);

    try {
      System.in.read();
      timerSubscription.unsubscribe();
      System.in.read();
    } catch (IOException e) {
      e.printStackTrace();
    }

    Observable<String> timerObservable1 = createTimerWithUsingToDispose();
    Subscription timerSubscription1 = writeSequenceToConsole(timerObservable1);

    try {
      System.in.read();
      timerSubscription1.unsubscribe();
      System.in.read();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Observable<String> createTimerWithSubscriberToDispose() {
    return Observable.create(new Observable.OnSubscribe<String>() {
      @Override
      public void call(Subscriber<? super String> subscriber) {
        Timer timer = new Timer("the timer");
        timer.scheduleAtFixedRate(new TimerTask() {
          @Override
          public void run() {
            subscriber.onNext("tick! " + new Date());
          }
        },
        0,
        1000);

        // This is used to clean up resources
        subscriber.add(Subscriptions.create( () -> timer.cancel() ));
      }
    });
  }

  private Observable<String> createTimerWithUsingToDispose() {
    return Observable.using(
            () -> new Timer(),
            timer -> Observable.create(subscriber -> {
              TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                  subscriber.onNext("tick! fuck!");
                }
              };
              timer.scheduleAtFixedRate(timerTask, 0, 1000);
            }),
            timer -> timer.cancel());
  }
}
