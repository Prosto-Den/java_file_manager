package widgets;

import events.EventBus;
import events.InsertButtonClickedEvent;
import events.LocaleChangedEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import java.util.List;

import app.AppContext;
import context.IMenuContext;
import models.StringKeys;
import resourceHandler.IconName;
import resourceHandler.IconSize;
import resourceHandler.ResourceHandler;
import utils.settings.FileSystemSettingsHelper;
import utils.ui.ClipboardUtil;
import models.PanelContextMenuItemId;
import models.FileData;
import widgets.interfaces.IWidget;
import widgets.interfaces.ITranslatable;
import javafx.scene.Node;

import utils.filesystem.*;

/**
 * Класс панели. Отображает содержимое директории
 * */
public class Panel extends VBox implements IWidget, ITranslatable
{
    /**
     * Класс контекста для панели. Служит для передачи данных от панели к контекстному меню
     */
    public class PanelMenuContext implements IMenuContext
    {
        private final FileData data;

        public PanelMenuContext(FileData data)
        {
            this.data = data;
        }

        @Override
        public FileData getFileData() { return data; };

        @Override
        public void executeAction(String actionID)
        {
            switch (actionID)
            {
                case (PanelContextMenuItemId.OPEN_ITEM) -> handleDoubleClick(data);
                case (PanelContextMenuItemId.COPY_ITEM) -> ClipboardUtil.copyToClipboard(data.getAbsolutePath());
                case (PanelContextMenuItemId.DELETE_ITEM) -> 
                {
                    FileSystemUtils.delete(data.getAbsolutePath());
                    refreshTable();
                }
                case (PanelContextMenuItemId.MOVE_TO_TRASH_ITEM) -> 
                {
                    AppContext.getIntegrationService().moveToTrash(data.getAbsolutePath());
                    refreshTable();
                }
                case (PanelContextMenuItemId.OPEN_IN_TERMINAL_ITEM) -> AppContext.getIntegrationService().openInTerminal(data.getAbsolutePath());
                case (PanelContextMenuItemId.REFRESH_ITEM) -> refreshTable();
                default -> {/*ничего не делаем*/}
            }
        }

        @Override
        public boolean isActionEnabled(String actionID)
        {
            switch (actionID)
            {
                case (PanelContextMenuItemId.REFRESH_ITEM) : return true;
                default : return data != null;
            }
        }

        @Override
        public Node getActionGraphic(String actionID)
        {
            if (actionID.equals(PanelContextMenuItemId.OPEN_ITEM))
            {
                FileData fileData = getFileData();
                if (fileData != null)
                {
                    Image image = fileData.isDirectory() ? ResourceHandler.getIcon(IconSize.SMALL, IconName.OPEN_FOLDER) : ResourceHandler.getIcon(IconSize.SMALL, IconName.OPEN_FILE);
                    return image != null ? new ImageView(image) : null;
                }
            }

            return null;
        }
    }

    @FXML
    private TableView<FileData> fileViewer; // виджет отображения файлов
    @FXML
    private TableColumn<FileData, String> fileNameColumn; // колонка с именем файла
    @FXML
    private TableColumn<FileData, String> fileSizeColumn; // размер файла
    @FXML
    private TableColumn<FileData, String> fileEditDateColumn; // дата последнего изменения файла

    private final String fileSystemID;
    private final FileSystemSettingsHelper settingsHelper;

    /**
     * Конструктор
     * @param fileSystemId идентификатор файловой системы для данной панели. Идентификатор можно получить
     *                     через FileSystemController. ВАЖНО!!! внутри конструктора нет проверки, что объект ФС
     *                     по этому ID существует, так что передавать нужно точно валидный ID
     * */
    public Panel(String fileSystemId, FileSystemSettingsHelper helper)
    {
        fileSystemID = fileSystemId;
        settingsHelper = helper;

        load(ResourceHandler.getLayout("Panel.fxml"));
        initUI();

        EventBus.subscribe(InsertButtonClickedEvent.class, event -> refreshTable());
        EventBus.subscribe(LocaleChangedEvent.class, event -> updateText());

        refreshTable();
    }

    /**
     * Настроить колонки таблицы
     * */
    private void setupTableColumns()
    {
        // настраиваем колонку с именем файла
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
        fileNameColumn.setCellFactory(column -> new TableCell<FileData, String>()
        {
            private final ImageView imageView = new ImageView();

            /**
             * Метод обновления содержимого ячейки таблицы
             * */
            @Override
            protected void updateItem(String item, boolean isEmpty)
            {
                super.updateItem(item, isEmpty);

                if (isEmpty || item == null)
                {
                    setText(null);
                    setGraphic(null);
                }
                else
                {
                    FileData file = getTableView().getItems().get(getIndex());
                    String fileName = file.getNameValue();
                    Image icon;

                    if (fileName.equals(AppContext.getLanguageManager().getString(StringKeys.FILEVIEWER_ROW_BACK)))
                        icon = ResourceHandler.getIcon(IconSize.BIG, IconName.BACK);
                    else
                    {
                        String fullPath = getFileSystem().buildPath(file.getNameValue());
                        if (FileSystemUtils.isDir(fullPath))
                            icon = ResourceHandler.getIcon(IconSize.BIG, IconName.FOLDER);
                        else
                            icon = ResourceHandler.getIcon(IconSize.BIG, IconName.FILE);
                    }

                    if (icon != null)
                    {
                        imageView.setImage(icon);
                        setGraphic(imageView);
                    }

                    setText(item);
                }
            }
        });

        // настраиваем колонку с размером файла
        fileSizeColumn.setCellValueFactory(cellData -> cellData.getValue().size());
        // настраиваем колонку с датой последнего изменения
        fileEditDateColumn.setCellValueFactory(cellData -> cellData.getValue().date());

        // задаём настройки для ряда
        fileViewer.setRowFactory( tv ->
        {
            TableRow<FileData> row = new TableRow<>(); 

            // Настраиваем поведение при вызове контекстного меню панели
            row.setOnContextMenuRequested(event -> {
                FileData data = row.getItem();
                
                if (data != null && data.getNameValue().equals(AppContext.getLanguageManager().getString(StringKeys.FILEVIEWER_ROW_BACK)))
                {
                    event.consume();
                    return;
                }
                ContextMenu contextMenu = AppContext.getContextMenuManager().createOrGetpanelContextMenu();
                AppContext.getContextMenuManager().configureContextMenu(contextMenu, new PanelMenuContext(data));

                contextMenu.show(row, event.getScreenX(), event.getScreenY());
                event.consume();
            });

            // Настраиваем поведение при двойном щелчке ЛКМ
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY)
                {
                    if (event.getClickCount() == 2 && !row.isEmpty())
                    {
                        FileData fileInfo = row.getItem();
                        handleDoubleClick(fileInfo);
                    }
                }
            });

            return row;
        });
    }

    /**
     * Обработка двойного нажатия на ряд таблицы
     * @param fileInfo данные файла
     * */
    private void handleDoubleClick(FileData fileInfo)
    {
        if (getFileSystem() != null)
        {
            String fileName = fileInfo.getNameValue();

            if (fileName.equals(".."))
            {
                getFileSystem().goBack();
                updateSettings();
                refreshTable();
            }
            else if (fileInfo.isDirectory())
            {
                getFileSystem().goForward(fileName);
                updateSettings();
                refreshTable();
            }
            else
                AppContext.getIntegrationService().openFile(fileInfo.getAbsolutePath());
        }
    }

    /**
     * Обновить содержимое таблицы
     * */
    private void refreshTable()
    {
        if (getFileSystem() != null)
        {
            ObservableList<FileData> fileData = FXCollections.observableArrayList();

            if (!getFileSystem().isCurrentPathRoot())
                fileData.add(new FileData("..", "", "", true));

            List<String> files = getFileSystem().listCurrentPath(false);
            for (String file : files)
            {
                String fileSize = FileSystemUtils.getFileSize(file);
                String fileEditDate = FileSystemUtils.lastModifiedDate(file);

                FileData fileInfo = new FileData(file, fileSize, fileEditDate,
                    FileSystemUtils.isDir(file));
                fileData.add(fileInfo);
            }

            fileViewer.getItems().clear();
            fileViewer.setItems(fileData);
            fileViewer.refresh();
        }
    }

    // IWidget
    @Override
    public void initUI()
    {
        // Меняем поведение fileViewer при увеличении размера окна. По умолчанию, будет создаваться четвёртая колонка.
        // Тут же ставим, чтобы последняя колонка подстраивалась под новый размер окна
        fileViewer.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        setupTableColumns();
        refreshTable();
    }

    // ITranslatable
    @Override
    public void updateText()
    {
        fileNameColumn.setText(AppContext.getLanguageManager().getString(StringKeys.PANEL_COLUMN_FILENAME));
        fileSizeColumn.setText(AppContext.getLanguageManager().getString(StringKeys.PANEL_COLUMN_FILE_SIZE));
        fileEditDateColumn.setText(AppContext.getLanguageManager().getString(StringKeys.PANEL_COLUMN_EDIT_DATE));
    }

    // Приватные методы

    /**
     * Получить экземпляр файловой системы дял данной панели. Метод нужен для более простого доступа
     * к экземпляру
     * @return объект файловой системы для данной панели
     * */
    private FileSystem getFileSystem() { return FileSystemController.get(fileSystemID); }

    /**
     * Записать директорию в настройки
     * */
    private void updateSettings()
    {
        if (getFileSystem() != null)
        {
            String currentPath = getFileSystem().getCurrentPath();
            settingsHelper.setPath(fileSystemID, currentPath);
        }
    }
}
