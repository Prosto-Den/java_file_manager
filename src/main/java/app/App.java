package app;

import javafx.application.Application;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.Objects;


/**
 * Класс приложения. Тут происходит основная настройка
 * */
public final class App extends Application
{
    @Override
    public void start(Stage stage)
    {
        // сначала подготовим всё необходимое для работы, потом запустимся
        prepareApp(stage);
        stage.show();
    }

    @Override
    public void stop()
    {
        // при закрытии приложения сохраним настройки. Это нужно, чтобы запомнить последние открытые директории
        AppContext.getSettings().saveSettings();
    }

    /**
     * Метод запуска приложения. Нужен, чтобы Launcher смог запустить приложение
     * */
    public static void Launch(String[] args)
    {
        launch(args);
    }

    /**
     * Предварительная подготовка для запуска приложения
     * */
    private void prepareApp(Stage stage)
    {
        AppContext.init(stage);

        try
        {
            FXMLLoader mainLoader = new FXMLLoader(Objects.requireNonNull(
                    getClass().getResource("/layouts/MainLayout.fxml")));
            VBox layout = mainLoader.load();

            Scene scene = new Scene(layout);
            stage.setMinHeight(600);
            stage.setMinWidth(800);
            stage.setScene(scene);
            stage.setTitle(AppContext.getAppName());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.err.println("Не удалось загрузить приложение :(");
        }
    }
}