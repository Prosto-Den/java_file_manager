package controllers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.ListCell;
import javafx.util.Callback;
import java.io.File;

import types.IconTypes;
import resourceHandler.ResourceHandler;
import resourceHandler.IconName;
import utils.FileSystem;

public class PanelController implements Initializable
{
    @FXML
    private ListView<String> fileViewer;
    private final FileSystem fileSystem = new FileSystem();

    @Override
    public void initialize(URL location, ResourceBundle resources)
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
