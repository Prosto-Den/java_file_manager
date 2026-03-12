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
    // события и слушатели
    private static final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

    /**
     * Подписаться на событие
     * @param eventType событие
     * @param listener слушатель для этого события
     * */
    public static <T> void subscribe(Class<T> eventType, Consumer<T> listener)
    {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(listener);
    }

    /**
     * Опубликовать событие (все, кто подписан на событие начнут выполнять свои действия)
     * @param event событие
     * */
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
