package widgets;


import events.ButtonClickedEvent;
import events.ClipboardEvent;
import events.EventBus;
import javafx.collections.FXCollections;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.fxml.FXML;
import models.StringKeys;
import resourceHandler.IconName;
import resourceHandler.IconSize;
import resourceHandler.ResourceHandler;
import types.OSType;
import utils.FileSystem;
import utils.FileSystemController;
import utils.FileSystemUtils;

import java.util.ResourceBundle;


/**
 * Панель с элементами управления для текущей директории
 * */
public class ControlPanel extends HBox implements IWidget
{
    @FXML
    private Button createButton; // кнопка добавления файла в директорию

    @FXML
    private ComboBox<String> diskComboBox; // выпадающий список с логическими дисками системы

    @FXML
    private Button backButton; // кнопка "Назад"

    @FXML
    private Button forwardButton; // кнопка "Вперёд"

    @FXML
    private Button insertButton; // кнопка вставки

    @FXML
    private TextField currentPathField; // текстовое поле текущей директории


    public ControlPanel(String fileSystemId)
    {
        load(ResourceHandler.getLayout("ControlPanel.fxml"));
        insertButton.setOnAction(event -> onInsertItemClick());
        currentPathField.textProperty().bind(FileSystemController.get(fileSystemId).getCurrentPathProperty());
        initUI();

        //TODO заменить на шину событий
        ResourceHandler.addStringListener(this::updateText);
    }

    @Override
    public void initUI()
    {
        Image addIcon = ResourceHandler.getIcon(IconSize.BIG, IconName.ADD);
        if (addIcon != null)
        {
            ImageView addImage = new ImageView();
            addImage.setImage(addIcon);
            createButton.setGraphic(addImage);
        }

        diskComboBox.setVisible(FileSystemUtils.checkOS(OSType.WINDOWS));

        insertButton.setDisable(true);
        EventBus.subscribe(ClipboardEvent.class, event -> {
            insertButton.setDisable(!event.isHasFiles());
        });

        updateDiskCombo();
        // вызывать updateText при инициализации не требуется, так как loader загружает текста виджетов
        // уже с нужной локалью
    }

    public void dispose()
    {
        ResourceHandler.removeStringListener(this::updateText);
    }

    public void onInsertItemClick()
    {
        FileSystemUtils.insert(currentPathField.getText());
        EventBus.publish(new ButtonClickedEvent());
    }

    private void updateDiskCombo()
    {
        if (FileSystemUtils.checkOS(OSType.WINDOWS))
        {
            diskComboBox.setItems(FXCollections.observableArrayList(FileSystemUtils.getLogicalDrives()));
            diskComboBox.setValue(diskComboBox.getItems().getFirst());
        }
    }

    private void updateText()
    {
        createButton.setText(ResourceHandler.getString(StringKeys.BUTTON_ADD_TEXT));
        createButton.setTooltip(new Tooltip(ResourceHandler.getString(StringKeys.BUTTON_ADD_TOOLTIP)));
        backButton.setTooltip(new Tooltip(ResourceHandler.getString(StringKeys.BUTTON_BACK_TOOLTIP)));
        forwardButton.setTooltip(new Tooltip(ResourceHandler.getString(StringKeys.BUTTON_FORWARD_TOOLTIP)));
        insertButton.setTooltip(new Tooltip(ResourceHandler.getString(StringKeys.BUTTON_INSERT_TOOLTIP)));
    }
}
