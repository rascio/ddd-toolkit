package it.r.dddtoolkit.es.support.guava;

import it.r.dddtoolkit.ddd.DomainEvent;
import it.r.dddtoolkit.es.ApplicationEvent;
import it.r.dddtoolkit.es.support.EventPublisher;
import it.r.dddtoolkit.es.support.Subscription;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class GuavaEventPublisher implements EventPublisher {

    public static GuavaEventPublisher sync() {
        return new GuavaEventPublisher(new EventBus());
    }

    public static GuavaEventPublisher async(Executor executor) {
        return new GuavaEventPublisher(new AsyncEventBus(executor));
    }

    public static GuavaEventPublisher async(int threadNumber) {
        return async(Executors.newFixedThreadPool(threadNumber));
    }

    private final EventBus eventBus;

    @Override
    public void publish(ApplicationEvent<?> event) {
        eventBus.post(event);
    }

    @Override
    public <D extends DomainEvent> Subscription subscribe(@NonNull Class<D> eventType, @NonNull EventCallback<D> callback) {
        final ApplicationEventListener<D> listener = new ApplicationEventListener<>(callback, eventType);

        eventBus.register(listener);

        return new GuavaSubscription(listener);
    }

    @RequiredArgsConstructor
    private class ApplicationEventListener<D extends DomainEvent> {

        private final EventCallback<D> callback;
        private final Class<D> eventType;
        private final AtomicBoolean cancelAfterUse = new AtomicBoolean(false);

        @SuppressWarnings("unchecked")
        @Subscribe
        public void on(ApplicationEvent<?> event) {
            if (eventType.isInstance(event.getEvent())) {
                if (log.isTraceEnabled()) {
                    log.trace("Forwarding event {} to {}", event.getEvent().getClass(), callback.getClass());
                }
                callback.on((ApplicationEvent<D>) event);
            }
            if (cancelAfterUse.get()) {
                eventBus.unregister(this);
            }
        }

        public void cancelAfterUse() {
            cancelAfterUse.set(true);
        }
    }

    @RequiredArgsConstructor
    private class GuavaSubscription implements Subscription {

        private final ApplicationEventListener<?> listener;

        @Override
        public void cancel() {
            eventBus.unregister(listener);
        }

        @Override
        public void cancelAfterUse() {
            listener.cancelAfterUse();
        }
    }
}
