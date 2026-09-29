package widgets;

import javafx.scene.layout.BorderPane;

import java.util.UUID;

import events.EventBus;
import javafx.beans.property.StringProperty;

/**
 * Класс-контейнер для виджетов вкладки
 * TabBody
 */
public final class TabBody extends BorderPane
{
    private final ControlPanel controlPanel; // контрольная панель вкладки
    private final Panel panel; // панель с информацией по текукщей директории

    /**
     * Конструктор
     * @param fileSystemId идентификатор файловой системы. Идентификатор можно получить через {@link utils.filesystem.FileSystemController}
     * @param helper помощник для связи пути файловой системы с настройками
     */
    public TabBody(UUID fileSystemId)
    {
        super();
        
        controlPanel = new ControlPanel(fileSystemId);
        panel = new Panel(fileSystemId);

        setTop(controlPanel);
        setCenter(panel);
    }

    /**
     * Выдать property с именем текущей директории
     * @return property с именем текущей директории
     */
    public StringProperty currentDirProperty()
    {
        return panel.getCurrentDirProperty();
    }

    /**
     * Отписаться от событий
     */
    public void unsubscribe()
    {
        EventBus.unsubscribe(controlPanel);
        EventBus.unsubscribe(panel);
    }

    @Override
    public void requestFocus()
    {
        super.requestFocus();
        panel.requestFocus();
    }
}