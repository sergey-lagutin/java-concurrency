package collection.copyonwritelist;

interface EventListener {
    void onEvent(Event ev);
}

class Event {
    private final Object message;

    Event(Object message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message.toString();
    }
}
