package widgets;

import events.EventBus;
import events.LocaleChangedEvent;
import events.FileSystemChanged;
import events.PathChangedEvent;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.application.Platform;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.fxml.FXML;
import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Optional;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.UUID;

import app.AppContext;
import models.StringKeys;
import resourceHandler.IconName;
import resourceHandler.IconSize;
import resourceHandler.ResourceHandler;
import utils.ui.ClipboardUtil;
import utils.ui.context.IContextMenuConfig;
import models.ContextMenuItemId;
import models.FileData;
import widgets.interfaces.IWidget;
import widgets.interfaces.ITranslatable;
import javafx.scene.Node;
import utils.Converter;
import utils.filesystem.*;
import events.FileFilterEvent;

/**
 * Класс панели. Отображает содержимое директории
 * */
public final class Panel extends TableView<FileData> implements IWidget, ITranslatable
{
    @FXML
    private TableColumn<FileData, String> fileNameColumn; // колонка с именем файла
    @FXML
    private TableColumn<FileData, String> fileSizeColumn; // размер файла
    @FXML
    private TableColumn<FileData, String> fileEditDateColumn; // дата последнего изменения файла

    private final UUID fileSystemID; // индентификатор файловой системы данной панели
    // TODO стоит ли перенести в fileSystem? Или переделать вообще как-то, чтобы не создавать его отдельно?
    private final StringProperty dirNameProperty; // проперти с именем файла
    private ObservableList<FileData> tableData; // для работы фильтрации придётся сохранять изначальный список файлов

    /**
     * Класс контекста для панели. Служит для передачи данных от панели к контекстному меню
     */
    public final class PanelMenuContext extends IContextMenuConfig
    {
        public PanelMenuContext(FileData data)
        {
            this.data = data;
        }

        private Optional<FileData> getFileData()
        {
            if (data != null && data instanceof FileData)
                return Optional.of((FileData) data);
            return Optional.empty();
        }

        // TODO переработать на работу с несколькими файлами
        @Override
        public void executeAction(String actionID)
        {
            switch (actionID)
            {
                case (ContextMenuItemId.OPEN_ITEM) -> handleDoubleClick((FileData) data);
                case (ContextMenuItemId.COPY_ITEM) ->
                    getFileData().ifPresent(fileData -> ClipboardUtil.copyToClipboard(fileData.getPath()));
                case (ContextMenuItemId.DELETE_ITEM) -> onDeleteItem();
                case (ContextMenuItemId.MOVE_TO_TRASH_ITEM) -> onMoveToTrashItem();
                case (ContextMenuItemId.OPEN_IN_TERMINAL_ITEM) ->
                    getFileData().ifPresent(fileData -> AppContext.getIntegrationService().openInTerminal(fileData.getPath()));
                case (ContextMenuItemId.REFRESH_ITEM) -> refreshTable();
                case (ContextMenuItemId.RENAME_ITEM) -> onRenameItem();
                default -> {/*ничего не делаем*/}
            }
        }

        @Override
        public boolean isActionEnabled(String actionID)
        {
            switch (actionID)
            {
                case (ContextMenuItemId.REFRESH_ITEM) : return true;
                default : return data != null;
            }
        }

        @Override
        public Node getActionGraphic(String actionID)
        {
            if (actionID.equals(ContextMenuItemId.OPEN_ITEM))
            {
                Object rawFileData = getUserData();
                if (rawFileData != null &&  rawFileData instanceof FileData)
                {
                    FileData fileData = (FileData) rawFileData;
                    if (fileData != null)
                    {
                        Image image = FileSystemUtils.isDir(fileData.getPath()) ? ResourceHandler.getIcon(IconSize.SMALL, IconName.OPEN_FOLDER) :
                                ResourceHandler.getIcon(IconSize.SMALL, IconName.OPEN_FILE);
                        return image != null ? new ImageView(image) : null;
                    }
                }
            }

            return null;
        }
    }

    /**
     * Конструктор
     * @param fileSystemId идентификатор файловой системы для данной панели. Идентификатор можно получить
     *                     через {@link FileSystemController}.
     * @param helper помощник для связи пути файловой системы с настройками
     * */
    public Panel(UUID fileSystemId)
    {
        //super();

        fileSystemID = fileSystemId;
        dirNameProperty = new SimpleStringProperty(getFileSystem().getCurrentDirName().toString());

        load(ResourceHandler.getLayout("Panel.fxml"));
        initUI();

        EventBus.subscribe(LocaleChangedEvent.class, this, event -> updateText());
        EventBus.subscribe(PathChangedEvent.class, this, event -> {
            if (event.getFileSystemId().equals(fileSystemId))
                Platform.runLater(() -> refreshTable());
        });
        EventBus.subscribe(FileSystemChanged.class, this, event -> {
            if (fileSystemId.equals(event.getFileSystemId()))
                Platform.runLater(() -> refreshTable());
        });
        EventBus.subscribe(FileFilterEvent.class, this, event -> {
            if (event.getFileSystemId().equals(fileSystemId))
            {
                //TODO добавить в настройки чувствительность к регистру
                String filter = event.getFilter().toLowerCase();
                Platform.runLater(() -> {
                    FilteredList<FileData> data = (FilteredList<FileData>) getItems();
                    if (filter.isEmpty())
                        data.setPredicate(file -> true);
                    else
                        data.setPredicate(file -> file.getNameValue().toLowerCase().contains(filter));
                });
            }
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
                getFileSystem().goUpTree();
                refreshTable();
            }
            else if (FileSystemUtils.isDir(fileInfo.getPath()))
            {
                getFileSystem().goDownTree(fileName);
                refreshTable();
            }
            else
                AppContext.getIntegrationService().openFile(fileInfo.getPath());
        }
    }

    // IWidget
    @Override
    public void initUI()
    {
        setupFileViewer();
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

    public StringProperty getCurrentDirProperty()
    {
        return dirNameProperty;
    }

    // Приватные методы

    /**
     * Обновить содержимое таблицы
     * */
    private void refreshTable()
    {
        if (getFileSystem() != null)
        {
            dirNameProperty.setValue(getFileSystem().getCurrentDirName().toString());

            if (tableData == null)
                tableData = FXCollections.observableArrayList();
            tableData.clear();

            if (!getFileSystem().isCurrentPathRoot())
                tableData.add(new FileData(getFileSystem().getCurrentPath().getParent(),
                                new ReadOnlyStringWrapper(".."),
                                new ReadOnlyStringWrapper(),
                                new ReadOnlyStringWrapper()));

            List<Path> files = getFileSystem().listCurrentPath(false);
            for (Path path : files)
            {
                long fileSize = FileSystemUtils.getFileAttributes(path).size();
                FileTime fileEditDate = FileSystemUtils.getFileAttributes(path).lastModifiedTime();
                FileData data = new FileData(path, Converter.convertFileSize(fileSize), Converter.convertDateTime(fileEditDate));
                tableData.add(data);
            }

            FilteredList<FileData> filteredList = new FilteredList<>(tableData, p -> true);
            setItems(filteredList);
            refresh();
        }
    }

    /**
     * Получить экземпляр файловой системы дял данной панели. Метод нужен для более простого доступа
     * к экземпляру
     * @return объект файловой системы для данной панели
     * */
    private FileSystem getFileSystem()
    {
        return FileSystemController.get(fileSystemID);
    }

    /**
     * Действия при переименовании файла
     */
    private void onRenameItem()
    {
        int selectedIndex = getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0)
        {
            FileData selectedFile = getItems().get(selectedIndex);
            String fileName = selectedFile.getNameValue();
            if (fileName.equals(AppContext.getLanguageManager().getString(StringKeys.FILEVIEWER_ROW_BACK)))
                return;
            
            setEditable(true);
            edit(selectedIndex, fileNameColumn);
            setEditable(false);
        }
    }

    // TODO добавить выбор, что делать при удалении (перемещать в корзину/реально удалять)
    /**
     * Действия при удалении файлов
     */
    private void onDeleteItem()
    {
        ObservableList<FileData> files = getSelectionModel().getSelectedItems();
        for (FileData data : files)
            if (!data.getNameValue().equals(AppContext.getLanguageManager().getString(StringKeys.FILEVIEWER_ROW_BACK)))
                FileSystemUtils.delete(data.getPath());
    }

    /**
     * Действия при перемещении в корзину
     */
    private void onMoveToTrashItem()
    {
        ObservableList<FileData> files = getSelectionModel().getSelectedItems();
        for (FileData data : files)
            if (!data.getNameValue().equals(AppContext.getLanguageManager().getString(StringKeys.FILEVIEWER_ROW_BACK)))
                AppContext.getIntegrationService().moveToTrash(data.getPath());
    }

    /**
     * Настроить колонки таблицы
     * */
    private void setupFileViewerColumns()
    {
        // настраиваем колонку с именем файла
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
        fileNameColumn.setCellFactory(column -> new TableCell<FileData, String>()
        {
            private final ImageView imageView = new ImageView();
            private TextField textField;
            
            // отключаем редактирование по двойному нажатию на мышку
            {
                addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (!isEditing())
                    {
                        if (event.getClickCount() > 1)
                            event.consume();
                        // обработка двойного щёлка на ЛКМ происходит ниже, тут можно ничего не писать
                    }
                });
            }

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
                    if (isEditing())
                    {
                        if (textField != null)
                            textField.setText(item);
                        setText(null);
                        setGraphic(textField);
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
                            Path fullPath = file.getPath();
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
            }

            /**
             * Действия при запуске радектирования ячейки
             */
            @Override
            public void startEdit()
            {
                if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable())
                    return;

                FileData file = getTableView().getItems().get(getIndex());
                String fileName = file.getNameValue();
                if (fileName.equals(AppContext.getLanguageManager().getString(StringKeys.FILEVIEWER_ROW_BACK)))
                    return;

                super.startEdit();
                createTextField();

                setText(null);
                setGraphic(textField);
                textField.selectAll();
                textField.requestFocus();
            }
            
            /**
             * Отмена редактирования
             */
            @Override
            public void cancelEdit()
            {
                super.cancelEdit();
                setText(getItem());
                setGraphic(imageView);
            }
            
            /**
             * Создать поле ввода в момент редактирования
             */
            private void createTextField()
            {
                textField = new TextField(getItem());
                textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);

                textField.setOnKeyPressed(t -> {
                    if (t.getCode() == KeyCode.ENTER)
                        commitEdit(textField.getText());
                    else if (t.getCode() == KeyCode.ESCAPE)
                        cancelEdit();
                });

                textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue)
                        commitEdit(textField.getText());
                });
            }
        });
        fileNameColumn.setEditable(true);

        // настраиваем поведение при завершении редактирования
        fileNameColumn.setOnEditCommit(event -> {
            String oldName = event.getOldValue();
            String newName = event.getNewValue();

            if (newName == null || newName.isBlank() || newName.equals(oldName))
                return;

            if (getFileSystem().renameFile(oldName, newName))
                // TODO хотелось бы не перерисовывать всю таблицу, а изменить имя только у этой ячейки
                refreshTable();
        });


        // настраиваем колонку с размером файла
        fileSizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
        // настраиваем колонку с датой последнего изменения
        fileEditDateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
    }

    /**
     * Настроить виджет таблицы
     */
    private void setupFileViewer()
    {
        setEditable(false);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setupFileViewerColumns();

        // задаём настройки для ряда
        setRowFactory( tv ->
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
                ContextMenu contextMenu = AppContext.getContextMenuManager().createOrGetPanelContextMenu();
                AppContext.getContextMenuManager().configureContextMenu(contextMenu, new PanelMenuContext(data));

                contextMenu.show(row, event.getScreenX(), event.getScreenY());
                event.consume();
            });

            // Настраиваем поведение при двойном щелчке ЛКМ
            row.setOnMouseClicked(event -> {
                switch (event.getButton())
                {
                    // нажатие на ЛКМ
                    case MouseButton.PRIMARY -> {
                        // открытие файлов/папок при двойном клике
                        if (event.getClickCount() == 2 && !row.isEmpty())
                        {
                            FileData data = row.getItem();
                            handleDoubleClick(data);
                        }
                        // снятие выделения при клике на пустую строку
                        else if (event.getClickCount() == 1 && row.isEmpty())
                            getSelectionModel().clearSelection();
                    }
                    // нажатие на кнопку "назад"
                    case MouseButton.BACK -> getFileSystem().goBack();
                    // нажатие на кнопку "вперёд"
                    case MouseButton.FORWARD -> getFileSystem().goForward();
                    default -> {/* ничего не делаем */}
                }
            });

            return row;
        });

        // задаём поведение при нажатии на кнопки клавиатуры
        setOnKeyPressed(event -> {
            switch (event.getCode())
            {
                // переименование файлов при нажатии F2
                case KeyCode.F2 -> {
                    onRenameItem();
                    event.consume();
                }
                // снятие выделения 
                case KeyCode.ESCAPE -> {
                    getSelectionModel().clearSelection();
                    event.consume();
                }
                case KeyCode.DELETE -> {
                    if (event.isShiftDown())
                        onDeleteItem();
                    else
                        onMoveToTrashItem();

                    event.consume();
                }
                default -> {/* ничего не делаем */}
            }
        });

        // задаём поведение при начале перетаскивания
        setOnDragDetected(event -> {
            ObservableList<FileData> files = getSelectionModel().getSelectedItems();

            if (files == null || files.isEmpty())
                return;
            
            List<File> filesToDrag = new ArrayList<>();
            for (FileData data : files)
            {
                if (data.getNameValue().equals(AppContext.getLanguageManager().getString(StringKeys.FILEVIEWER_ROW_BACK)))
                    continue;
                filesToDrag.add(data.getPath().toFile());
            }

            Dragboard dragBoard = startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putFiles(filesToDrag);
            content.put(AppContext.getPanelDataFormat(), fileSystemID);
            dragBoard.setContent(content);

            event.consume();
        });

        // поведение при активном перетаскивании
        setOnDragOver(event -> {
            Dragboard dragBoard = event.getDragboard();

            if (dragBoard.hasFiles() && dragBoard.hasContent(AppContext.getPanelDataFormat()))
            {
                UUID sourceFileSystemId = (UUID) dragBoard.getContent(AppContext.getPanelDataFormat());
                // проверяем, что сейчас курсор находится над другой панелью. Тогда разрешаем завершение перетаскивания
                if (sourceFileSystemId != null && !sourceFileSystemId.equals(fileSystemID))
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }

            event.consume();
        });

        // поведение при завершении перетаскивания
        setOnDragDropped(event -> {
            Dragboard dragBoard = event.getDragboard();
            FileSystem targetFileSystem = getFileSystem();
            List<File> filesToTransfer = dragBoard.getFiles();
            
            TransferMode acceptedMode = event.getAcceptedTransferMode();
            
            if (acceptedMode == TransferMode.MOVE)
            {
                targetFileSystem.moveInto(filesToTransfer);
            }
            else if (acceptedMode == TransferMode.COPY)
                targetFileSystem.copyInto(filesToTransfer);
            
            event.setDropCompleted(true);
            event.consume();
        });
    }
}
