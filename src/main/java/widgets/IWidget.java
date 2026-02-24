package widgets;


import javafx.fxml.FXMLLoader;
import resourceHandler.ResourceHandler;

import java.io.IOException;

/**
 * Интерфейс для создания кастомных виджетов
 * */
public interface IWidget
{
    /**
     * Метод для загрузки интерфейса виджета из fxml. В целом изменений под конкретный виджет не требуется
     * (т.е. метод универсальный), но в целом не возбраняется делать его реализацию в виджете.
     * @param resource Путь к fxml файлу в ресурсах
     * */
    default void load(String resource) throws RuntimeException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource), ResourceHandler.getStringBundle());
        loader.setRoot(this);
        loader.setController(this);

        try
        {
            loader.load();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Инициализировать интерфейс
     * */
    void initUI();
}
