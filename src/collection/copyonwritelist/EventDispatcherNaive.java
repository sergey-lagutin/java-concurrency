package collection.copyonwritelist;

import java.util.ArrayList;
import java.util.List;

public class EventDispatcherNaive {
    private List<EventListener> listeners = new ArrayList<>();

    public void registerListener(EventListener listener) {
        listeners.add(listener);
    }

    public void fireEvent(Event ev) {
        for (EventListener l : listeners) {
            l.onEvent(ev);
        }
    }

    public static void main(String[] args) {
        class ListenerImpl implements EventListener {
            private final String name;

            ListenerImpl(String name) {
                this.name = name;
            }

            @Override
            public void onEvent(Event ev) {
                System.out.println(String.format("%s received event \"%s\"", name, ev));
            }
        }

        final EventDispatcherNaive dispatcher = new EventDispatcherNaive();

        for (int i = 0; i < 5; i++) {
            final String name = "Listener " + i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dispatcher.registerListener(new ListenerImpl(name));
                    String message = "I'm ready: " + Thread.currentThread().getName();
                    dispatcher.fireEvent(new Event(message));
                }
            }).start();
        }
    }
}

