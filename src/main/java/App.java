import javafx.application.Application;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.Objects;



public class App extends Application
{
    //private AnchorPane layout;

    @Override
    public void start(Stage stage) throws Exception
    {
        //TODO пути к fxml тоже надо вынести
        BorderPane leftLayout = (BorderPane) buildPanel();
        BorderPane rightLayout = (BorderPane) buildPanel();

        HBox layout = new HBox(leftLayout, rightLayout);
        HBox.setHgrow(leftLayout, Priority.ALWAYS);
        HBox.setHgrow(rightLayout, Priority.ALWAYS);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();
    }

    public static void Launch(String[] args)
    {
        launch(args);
    }

    private Parent buildPanel() throws IOException
    {
        Parent controlPanel = FXMLLoader.load(Objects.requireNonNull(getClass()
                .getResource("layouts/ControlPanel.fxml")));
        Parent panel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/Panel.fxml")));

        BorderPane layout = new BorderPane();
        layout.setTop(controlPanel);
        layout.setCenter(panel);

        return layout;
    }
}