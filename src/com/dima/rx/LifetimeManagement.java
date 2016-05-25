package com.dima.rx;

import rx.Observable;
import rx.Subscription;
import rx.exceptions.OnErrorNotImplementedException;
import rx.subjects.PublishSubject;


/**
 *  Based on <a href="http://www.introtorx.com/Content/v1.0.10621.0/03_LifetimeManagement.html">http://www.introtorx.com/Content/v1.0.10621.0/03_LifetimeManagement.html</a>
 */
public class LifetimeManagement implements Main.Snippet {
  @Override
  public void run() {

    try {
      // Will throw since onError callback is not provided
      Observable.error(new RuntimeException())
              .subscribe(o -> {/* we don't care */});
    } catch (OnErrorNotImplementedException e) {
      System.out.printf("%s was thrown\n", e);
    }

    // The correct way to handle errors!
    // There should be no try-catch
    // One should always provide an onError callback
    PublishSubject<String> stringPublishSubject = PublishSubject.create();
    stringPublishSubject.subscribe(
            str -> {
              System.out.println(str);
            },
            t -> {
              System.out.println("Error: " + t);
            }
    );
    stringPublishSubject.onNext("The error is coming!");
    stringPublishSubject.onError(new RuntimeException("WtfException"));


    // Unsubscribing
    PublishSubject<String> subject = PublishSubject.create();
    Subscription firstSubscription = subject.subscribe(s -> {
      System.out.println("first: " + s);
    });

    Subscription secondSubscription = subject.subscribe(s -> {
      System.out.println("second: " + s);
    });

    subject.onNext("One");
    subject.onNext("Two");
    subject.onNext("Three");
    subject.onNext("The last one before unsubscribing from the first one");
    firstSubscription.unsubscribe();
    System.out.println("The first unsubscribed: " + firstSubscription.isUnsubscribed());
    subject.onNext("Four");
    subject.onNext("Five");


    System.out.println("The second unsubscribed: " + secondSubscription.isUnsubscribed());
    subject.onNext("Calling onComplete should cause unsubscribtion of the second");
    subject.onCompleted();
    System.out.println("The second unsubscribed: " + secondSubscription.isUnsubscribed());
  }
}
