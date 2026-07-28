package widgets;


import app.AppContext;
import events.ClipboardEvent;
import events.EventBus;
import events.InsertButtonClickedEvent;
import events.LocaleChangedEvent;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import models.StringKeys;
import resourceHandler.IconName;
import resourceHandler.IconSize;
import resourceHandler.ResourceHandler;
import types.OSType;
import utils.ClipboardUtil;
import utils.FileSystemController;
import utils.FileSystemUtils;
import widgets.interfaces.ITranslatable;
import widgets.interfaces.IWidget;


/**
 * Панель с элементами управления для текущей директории
 * */
public class ControlPanel extends HBox implements IWidget, ITranslatable
{
    @FXML
    private Button createButton; // кнопка добавления файла в директорию

    @FXML
    private ImageView diskIcon;

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


    /**
     * Конструктор
     * @param fileSystemId идентификатор файловой системы. Так же, как и Panel не проверяет, что файловая
     *                     система с этим ID существует
     * */
    public ControlPanel(String fileSystemId)
    {
        load(ResourceHandler.getLayout("ControlPanel.fxml"));
        insertButton.setOnAction(event -> onInsertItemClick());

        if (FileSystemController.get(fileSystemId) != null)
            currentPathField.textProperty().bind(FileSystemController.get(fileSystemId).getCurrentPathProperty());
        initUI();

        EventBus.subscribe(LocaleChangedEvent.class, event -> updateText());
    }

    /**
     * Действия при нажатии кнопки "Вставить"
     * */
    public void onInsertItemClick()
    {
        ClipboardUtil.insert(currentPathField.getText());
        EventBus.publish(new InsertButtonClickedEvent());
    }

    // IWidget
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

        showDiskControlsVisibility(OSType.is(OSType.WINDOWS));

        insertButton.setDisable(true);
        EventBus.subscribe(ClipboardEvent.class, event -> {
            insertButton.setDisable(!event.isHasFiles());
        });

        updateDiskCombo();
        // вызывать updateText при инициализации не требуется, так как loader загружает текста виджетов
        // уже с нужной локалью
    }

    // ITranslatable
    @Override
    public void updateText()
    {
        createButton.setText(AppContext.getLanguageManager().getString(StringKeys.BUTTON_ADD_TEXT));
        createButton.setTooltip(new Tooltip(AppContext.getLanguageManager().getString(StringKeys.BUTTON_ADD_TOOLTIP)));
        backButton.setTooltip(new Tooltip(AppContext.getLanguageManager().getString(StringKeys.BUTTON_BACK_TOOLTIP)));
        forwardButton.setTooltip(new Tooltip(AppContext.getLanguageManager().getString(StringKeys.BUTTON_FORWARD_TOOLTIP)));
        insertButton.setTooltip(new Tooltip(AppContext.getLanguageManager().getString(StringKeys.BUTTON_INSERT_TOOLTIP)));
    }

    // Приватные методы
    /**
     * Обновить наполнение выпадающего меню с логическими дисками
     * */
    private void updateDiskCombo()
    {
        if (OSType.is(OSType.WINDOWS))
        {
            diskComboBox.setItems(FXCollections.observableArrayList(FileSystemUtils.getLogicalDrives()));
            diskComboBox.setValue(diskComboBox.getItems().getFirst());
        }
    }

    private void showDiskControlsVisibility(boolean show)
    {
        diskComboBox.setVisible(show);
        diskComboBox.setManaged(show);
        diskIcon.setVisible(show);
        diskIcon.setManaged(show);
    }
}
