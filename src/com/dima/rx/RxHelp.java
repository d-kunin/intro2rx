package com.dima.rx;

import rx.Observable;

public class RxHelp {
  public static void writeSequenceToConsole(Observable<String> observable) {
    observable.subscribe(System.out::println);
  }
}
