package events;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


/**
 * Реализация шины событий
 * */
public final class EventBus
{
    /**
     * Класс подписки на событие
     * Subscription
     * @param owner слушатель события
     * @param consumer действия при происхождении события
     */
    private static record Subscription(Object owner, Consumer<?> consumer) {}

    // события и слушатели
    private static final Map<Class<?>, List<Subscription>> listeners = new HashMap<>();

    /**
     * Подписаться на событие
     * @param eventType событие
     * @param owner слушатель события
     * @param listener действия, которые будут выполнены при срабатывании события
     * */
    public static <T> void subscribe(Class<T> eventType, Object owner, Consumer<T> listener)
    {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(new Subscription(owner, listener));
    }

    /**
     * Отписать объект от всех событий
     * @param owner слушатель события
     */
    public static void unsubscribe(Object owner)
    {
        if (owner == null)
            return;

        for (List<Subscription> subList : listeners.values())
            subList.removeIf(sub -> sub.owner() == owner);
    }

    /**
     * Опубликовать событие (все, кто подписан на событие начнут выполнять свои действия)
     * @param event событие
     * */
    public static <T> void publish(T event)
    {
        List<Subscription> eventSubsciptions = listeners.get(event.getClass());
        if (eventSubsciptions != null) {
            new ArrayList<>(eventSubsciptions).forEach(sub -> callListener(sub.consumer(), event));
        }
    }

    /**
     * Обработать событие
     * @param <T> тип события
     * @param listener действия при данном событии
     * @param event произошедшее событие
     */
    @SuppressWarnings("unchecked")
    private static <T> void callListener(Consumer<?> listener, T event)
    {
        ((Consumer<T>) listener).accept(event);
    }
}
