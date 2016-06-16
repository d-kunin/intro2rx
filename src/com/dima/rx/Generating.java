package com.dima.rx;

import rx.Observable;
import rx.Subscription;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Generating implements Main.Snippet {
  @Override
  public void run() {
    // produce 2 3 4 5 6 stop
    Observable<Integer> range = Observable.range(2, 5);
    RxHelp.writeStreamToConsole(range);

    final CountDownLatch intervalLatch = new CountDownLatch(1);
    Subscription subscription = Observable.interval(50, TimeUnit.MILLISECONDS)
            .subscribe(val -> {
              System.out.println("val = [" + val + "]");
              if (val > 10)
                intervalLatch.countDown();
            });
    try {
      intervalLatch.await();
    } catch (InterruptedException ignored) {
    } finally { subscription.unsubscribe(); }


    final long startTime = System.currentTimeMillis();
    final CountDownLatch timerLatch = new CountDownLatch(1);
    Observable.timer(100, TimeUnit.MILLISECONDS)
            .subscribe(val -> {
              System.out.println("Time elapsed=" + (System.currentTimeMillis() - startTime));
              timerLatch.countDown();
            });
    try {
      timerLatch.await();
    } catch (InterruptedException ignored) {}
  }
}
