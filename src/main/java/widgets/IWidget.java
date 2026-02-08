package widgets;


import javafx.fxml.FXMLLoader;
import java.io.IOException;


public interface IWidget
{
    default void load(String resource) throws RuntimeException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resource));
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

    void initUI();
}
