import javafx.application.Application;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.util.Objects;
import javafx.scene.layout.StackPane;


public class App extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        Parent leftPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/Panel.fxml")));
        Parent rightPanel = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("layouts/Panel.fxml")));

        HBox layout = new HBox(leftPanel, rightPanel);
        Scene scene = new Scene(layout);

        stage.setScene(scene);
        stage.show();
    }

    public static void Launch(String[] args)
    {
        launch(args);
    }
}