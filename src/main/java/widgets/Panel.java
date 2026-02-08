package widgets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.util.Callback;
import resourceHandler.IconName;
import resourceHandler.ResourceHandler;
import types.IconTypes;
import utils.FileSystem;

import java.io.File;

public class Panel extends VBox implements IWidget
{
    @FXML
    private ListView<String> fileViewer;

    private FileSystem fileSystem;

    public Panel()
    {
        load("/layouts/Panel.fxml");
        initUI();
    }

    public void setFileSystem(FileSystem fileSystem) {this.fileSystem = fileSystem;}

    @Override
    public void initUI()
    {
        File dir = new File("C:\\");
        File[] files = dir.listFiles();
        ObservableList<String> data = FXCollections.observableArrayList();

        if (files != null)
            for (File file : files)
                data.add(file.getName());

        fileViewer.setItems(data);
        fileViewer.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView)
            {
                ImageCell cell = new ImageCell();
                cell.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (!cell.isEmpty()))
                    {
                        String name = cell.getItem();
                        ObservableList<String> newData = FXCollections.observableArrayList();
                        fileViewer.getItems().clear();

                        //TODO вынести строковые ресурсы
                        if (name.equals(".."))
                            fileSystem.goBack();
                        else
                            fileSystem.goForward(name);

                        if (!fileSystem.isCurrentPathRoot())
                            newData.add("..");
                        newData.addAll(fileSystem.listCurrentPath(true));
                        fileViewer.setItems(newData);
                        fileViewer.refresh();
                    }
                });

                return cell;
            }
        });
    }

    private class ImageCell extends ListCell<String>
    {
        @Override
        public void updateItem(String item, boolean empty)
        {
            super.updateItem(item, empty);
            ImageView imageView = new ImageView();

            if (empty || item == null)
            {
                setText(null);
                setGraphic(null);
            }
            else
            {
                if (item.equals(".."))
                {
                    Image image = ResourceHandler.getIcon(IconTypes.FILE_VIEWER, "24x24", IconName.BACK);
                    imageView.setImage(image);
                    setGraphic(imageView);
                    setText(item);
                }
                else
                {
                    File file = new File(fileSystem.buildPath(item));
                    Image image;

                    //TODO пока захардкожено, потом надо будет поправить
                    if (file.isDirectory())
                        image = ResourceHandler.getIcon(IconTypes.FILE_VIEWER, "24x24", IconName.FOLDER);
                    else
                        image = ResourceHandler.getIcon(IconTypes.FILE_VIEWER, "24x24", IconName.FILE);

                    if (image != null)
                    {
                        imageView.setImage(image);
                        setGraphic(imageView);
                        setText(item);
                    }
                }
            }
        }
    }
}
