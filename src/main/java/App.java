import com.sun.tools.javac.Main;
import controllers.MainController;
import javafx.application.Application;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import utils.FileSystem;

import java.io.File;
import java.io.IOException;
import java.util.Objects;



public class App extends Application
{
    //private AnchorPane layout;

    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/layouts/MainLayout.fxml"));
        Parent layout = mainLoader.load();
        MainController controller = mainLoader.getController();

        if (controller != null)
        {
            controller.setLeftFileSystem(new FileSystem());
            controller.setRightFileSystem(new FileSystem());
        }

        Scene scene = new Scene(layout);
        stage.setMinHeight(600);
        stage.setMinWidth(800);
        stage.setScene(scene);
        stage.show();
    }

    public static void Launch(String[] args)
    {
        launch(args);
    }
}