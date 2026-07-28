package widgets.interfaces;


import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

import app.AppContext;

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
        FXMLLoader loader = new FXMLLoader(url, AppContext.getLanguageManager().getBundle());
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
