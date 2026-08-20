package widgets;


import app.AppContext;
import events.ClipboardEvent;
import events.EventBus;
import events.InsertButtonClickedEvent;
import events.LocaleChangedEvent;
import events.NewFileInDirEvent;
import events.PathChangedEvent;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import models.CreateButtonMenuId;
import models.StringKeys;
import resourceHandler.ResourceHandler;
import types.OSType;
import utils.filesystem.FileSystem;
import utils.filesystem.FileSystemController;
import utils.filesystem.FileSystemUtils;
import utils.ui.ClipboardUtil;
import widgets.interfaces.ITranslatable;
import widgets.interfaces.IWidget;
import java.util.Optional;


/**
 * Панель с элементами управления для текущей директории
 * */
public final class ControlPanel extends HBox implements IWidget, ITranslatable
{
    @FXML
    private MenuButton createButton; // кнопка создания файла/папки в директории

    @FXML
    private ImageView diskIcon; // иконка диска

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


    private final String fileSystemId;


    /**
     * Конструктор
     * @param fileSystemId идентификатор файловой системы. Так же, как и Panel не проверяет, что файловая
     *                     система с этим ID существует
     * */
    public ControlPanel(String fileSystemId)
    {
        load(ResourceHandler.getLayout("ControlPanel.fxml"));
        insertButton.setOnAction(event -> onInsertItemClick());

        this.fileSystemId = fileSystemId;

        //currentPathField.setText(getFileSystem().getCurrentPath());
        currentPathField.textProperty().bind(getFileSystem().getCurrentPathProperty());
        initUI();

        EventBus.subscribe(LocaleChangedEvent.class, event -> updateText());
        EventBus.subscribe(PathChangedEvent.class, event -> {
            //currentPathField.setText(getFileSystem().getCurrentPath());
            backButton.setDisable(getFileSystem().isBackStackEmpty());
            forwardButton.setDisable(getFileSystem().isForwardStackEmpty());
        });

        backButton.setOnAction(event ->getFileSystem().goBack());
        forwardButton.setOnAction(event -> getFileSystem().goForward());

        getCreateMenuItem(CreateButtonMenuId.CREATE_FOLDER_ITEM).ifPresent(item -> item.setOnAction(event -> onCreateFolderItemClick()));
        getCreateMenuItem(CreateButtonMenuId.CREATE_TEXT_FILE_ITEM).ifPresent(item -> item.setOnAction(event -> onCreateTextFileItemClick()));
    }

    /**
     * Действия при нажатии кнопки "Вставить"
     * */
    private void onInsertItemClick()
    {
        ClipboardUtil.insert(currentPathField.getText());
        EventBus.publish(new InsertButtonClickedEvent());
    }

    private void onCreateFolderItemClick()
    {
        if (getFileSystem().createFolderInCurrentDirectory())
            EventBus.publish(new NewFileInDirEvent());
    }

    private void onCreateTextFileItemClick()
    {
        if (getFileSystem().createTextFileInCurrentDirectory())
            EventBus.publish(new NewFileInDirEvent());
    }

    // IWidget
    @Override
    public void initUI()
    {
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

    private FileSystem getFileSystem()
    {
        return FileSystemController.get(fileSystemId);
    }

    private Optional<MenuItem> getCreateMenuItem(String itemId)
    {
        return createButton.getItems().stream()
            .filter(item -> item.getId() != null && item.getId().equals(itemId))
            .findFirst();
    }
}
