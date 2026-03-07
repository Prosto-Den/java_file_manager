package events;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Реализация шины событий
 * */
public class EventBus
{
    private static final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

    public static <T> void subscribe(Class<T> eventType, Consumer<T> listener)
    {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(listener);
    }

    public static <T> void publish(T event)
    {
        List<Consumer<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            eventListeners.forEach(listener ->
                    ((Consumer<T>) listener).accept(event)
            );
        }
    }
}
