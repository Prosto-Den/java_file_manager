package widgets;


import javafx.fxml.FXMLLoader;
import resourceHandler.ResourceHandler;

import java.io.IOException;
import java.net.URL;

/**
 * Интерфейс для создания кастомных виджетов
 * */
public interface IWidget
{
    /**
     * Метод для загрузки интерфейса виджета из fxml. В целом изменений под конкретный виджет не требуется
     * (т.е. метод универсальный), но в целом не возбраняется делать его реализацию в виджете.
     * @param url URL fxml файла
     * */
    default void load(URL url) throws RuntimeException
    {
        FXMLLoader loader = new FXMLLoader(url, ResourceHandler.getStringBundle());
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
