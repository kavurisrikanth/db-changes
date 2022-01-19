package d3e.core;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.flowables.ConnectableFlowable;
import javax.annotation.PostConstruct;
import models.AnonymousUser;
import models.OneTimePassword;
import models.RefModel;
import models.Thing;
import models.User;
import models.UserSession;
import org.springframework.scheduling.annotation.Async;
import store.DataStoreEvent;

@org.springframework.stereotype.Service
public class D3ESubscription implements FlowableOnSubscribe<DataStoreEvent> {
  public ConnectableFlowable<DataStoreEvent> flowable;
  private FlowableEmitter<DataStoreEvent> emitter;

  @PostConstruct
  public void init() {
    this.flowable = Flowable.create(this, BackpressureStrategy.BUFFER).publish();
    this.flowable.connect();
    flowable.subscribe((a) -> {});
  }

  @Async
  public void handleContextStart(DataStoreEvent event) {
    this.emitter.onNext(event);
  }

  @Override
  public void subscribe(FlowableEmitter<DataStoreEvent> emitter) throws Throwable {
    this.emitter = emitter;
  }

  public Flowable<D3ESubscriptionEvent<AnonymousUser>> onAnonymousUserChangeEvent() {
    return this.flowable
        .filter((e) -> e.getEntity() instanceof AnonymousUser)
        .map(
            (e) -> {
              D3ESubscriptionEvent<AnonymousUser> event = new D3ESubscriptionEvent<>();
              event.model = ((AnonymousUser) e.getEntity());
              event.changeType = e.getType();
              return event;
            });
  }

  public Flowable<D3ESubscriptionEvent<OneTimePassword>> onOneTimePasswordChangeEvent() {
    return this.flowable
        .filter((e) -> e.getEntity() instanceof OneTimePassword)
        .map(
            (e) -> {
              D3ESubscriptionEvent<OneTimePassword> event = new D3ESubscriptionEvent<>();
              event.model = ((OneTimePassword) e.getEntity());
              event.changeType = e.getType();
              return event;
            });
  }

  public Flowable<D3ESubscriptionEvent<RefModel>> onRefModelChangeEvent() {
    return this.flowable
        .filter((e) -> e.getEntity() instanceof RefModel)
        .map(
            (e) -> {
              D3ESubscriptionEvent<RefModel> event = new D3ESubscriptionEvent<>();
              event.model = ((RefModel) e.getEntity());
              event.changeType = e.getType();
              return event;
            });
  }

  public Flowable<D3ESubscriptionEvent<Thing>> onThingChangeEvent() {
    return this.flowable
        .filter((e) -> e.getEntity() instanceof Thing)
        .map(
            (e) -> {
              D3ESubscriptionEvent<Thing> event = new D3ESubscriptionEvent<>();
              event.model = ((Thing) e.getEntity());
              event.changeType = e.getType();
              return event;
            });
  }

  public Flowable<D3ESubscriptionEvent<User>> onUserChangeEvent() {
    return this.flowable
        .filter((e) -> e.getEntity() instanceof User)
        .map(
            (e) -> {
              D3ESubscriptionEvent<User> event = new D3ESubscriptionEvent<>();
              event.model = ((User) e.getEntity());
              event.changeType = e.getType();
              return event;
            });
  }

  public Flowable<D3ESubscriptionEvent<UserSession>> onUserSessionChangeEvent() {
    return this.flowable
        .filter((e) -> e.getEntity() instanceof UserSession)
        .map(
            (e) -> {
              D3ESubscriptionEvent<UserSession> event = new D3ESubscriptionEvent<>();
              event.model = ((UserSession) e.getEntity());
              event.changeType = e.getType();
              return event;
            });
  }
}
