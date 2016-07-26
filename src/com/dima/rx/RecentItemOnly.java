package com.dima.rx;

import rx.subjects.BehaviorSubject;

/**
 * Created by dima on 7/26/16.
 */
public class RecentItemOnly implements Main.Snippet {
  @Override
  public void run() {
    BehaviorSubject<Integer> intSubject = BehaviorSubject.create(1);
    intSubject.onNext(2);
    intSubject.onNext(3);
    intSubject
            .first()
            .subscribe(integer -> {
              System.out.println(integer);
            });
    intSubject.onNext(4);
    intSubject.onNext(5);
  }
}
