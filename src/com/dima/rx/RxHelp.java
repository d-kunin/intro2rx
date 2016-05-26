package com.dima.rx;

import rx.Observable;
import rx.Subscription;

public class RxHelp {
  public static Subscription writeSequenceToConsole(Observable<String> observable) {
    return observable.subscribe(System.out::println);
  }

  public static Subscription writeStreamToConsole(Observable<String> observable) {
    String name = observable.getClass().getSimpleName() + ":" + observable.hashCode();
    return observable.subscribe(
            s -> System.out.printf("%s value=%s\n", name, s),
            t -> System.out.printf("%s error=%s\n", name, t),
            () -> System.out.printf("%s completed\n", name)
    );
  }
}
