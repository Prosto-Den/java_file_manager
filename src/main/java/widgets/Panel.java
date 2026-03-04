package widgets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;

import java.io.IOException;
import java.util.List;

import javafx.util.Pair;
import models.StringKeys;
import resourceHandler.IconName;
import resourceHandler.IconSize;
import resourceHandler.ResourceHandler;
import utils.ContextMenuManager;
import utils.FileSystem;
import models.FileData;
import utils.FileSystemUtils;


/**
 * Класс панели. Отображает содержимое директории
 * */
public class Panel extends VBox implements IWidget
{
    @FXML
    private TableView<FileData> fileViewer; // виджет отображения файлов
    @FXML
    private TableColumn<FileData, String> fileNameColumn; // колонка с именем файла
    @FXML
    private TableColumn<FileData, String> fileSizeColumn; // размер файла
    @FXML
    private TableColumn<FileData, String> fileEditDateColumn; // дата последнего изменения файла

    private FileSystem fileSystem; // экземпляр файловой системы

    /**
     * Конструктор
     * */
    public Panel() throws IOException
    {
        load(ResourceHandler.getLayout("Panel.fxml"));
        initUI();

        ResourceHandler.addStringListener(this::updateText);
    }

    /**
     * Выставить файловую систему для панели. Нужна для отображения текущей директории, перехода по ней и т.п.
     * Так панелей несколько и каждая должна работать независимо от другой, у каждой панели должен быть свой
     * экземпляр файловой системы
     * @param fileSystem файловая система
     * */
    public void setFileSystem(FileSystem fileSystem)
    {
        this.fileSystem = fileSystem;
        refreshTable();
    }

    @Override
    public void initUI()
    {
        // Меняем поведение fileViewer при увеличении размера окна. По умолчанию, будет создаваться четвёртая колонка.
        // Тут же ставим, чтобы последняя колонка подстраивалась под новый размер окна
        fileViewer.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        setupTableColumns();
        // тут не получится вызвать refreshTable, так как файловая система ещё не создана
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

                    if (fileName.equals(".."))
                        icon = ResourceHandler.getIcon(IconSize.BIG, IconName.BACK);
                    else
                    {
                        String fullPath = fileSystem.buildPath(file.getNameValue());
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
        fileSizeColumn.setCellValueFactory(cellData -> cellData.getValue().getSize());
        // настраиваем колонку с датой последнего изменения
        fileEditDateColumn.setCellValueFactory(cellData -> cellData.getValue().getDate());

        // задаём поведение при двойном нажатии ЛКМ по ряду таблицы
        fileViewer.setRowFactory( tv ->
        {
            TableRow<FileData> row = new TableRow<>();
            row.setOnMouseClicked(event ->
            {
                if (event.getClickCount() == 2 && !row.isEmpty())
                {
                    FileData fileInfo = row.getItem();
                    handleDoubleClick(fileInfo);
                }
            });

            // задаём поведение при вызове контекстного меню
            row.setOnContextMenuRequested(event -> {
                FileData fileInfo = row.getItem();

                // в зависимости от того, было вызвано меню по ряду файла или ряду папки, будет отображаться разная
                // иконка
                if (fileInfo != null)
                {
                    MenuItem openItem = ContextMenuManager.getOpenMenuItem();
                    
                    if (openItem != null)
                    {
                        openItem.setOnAction(actionEvent -> onContextItemOpenClick(fileInfo.getNameValue()));
                        //Pair<FileSystem, String> data = new Pair<>(fileSystem, fileInfo.getNameValue());
                        
                        
                        // Помещаем абсолютный путь к файлу в item, так будет проще работать с открытием
                        //openItem.setUserData(data);

                        Image icon;
                        if (fileInfo.isDirectory())
                            icon = ResourceHandler.getIcon(IconSize.SMALL, IconName.OPEN_FOLDER);
                        else
                            icon = ResourceHandler.getIcon(IconSize.SMALL, IconName.OPEN_FILE);

                        if (icon != null)
                            openItem.setGraphic(new ImageView(icon));
                    }

                }
            });


            row.setContextMenu(ContextMenuManager.getPanelContextMenu());
            row.setStyle("-fx-font-size: 14px;");

            return row;
        });

        updateText();
    }

    /**
     * Обработка двойного нажатия на ряд таблицы
     * */
    private void handleDoubleClick(FileData fileInfo)
    {
        String fileName = fileInfo.getNameValue();

        if (fileName.equals(".."))
        {
            fileSystem.goBack();
            refreshTable();
        }
        else if (fileInfo.isDirectory())
        {
            fileSystem.goForward(fileName);
            refreshTable();
        }
        else
            fileSystem.openFile(fileName);

        refreshTable();
    }

    /**
     * Обновить содержимое таблицы
     * */
    private void refreshTable()
    {
        ObservableList<FileData> fileData = FXCollections.observableArrayList();

        if (!fileSystem.isCurrentPathRoot())
            fileData.add(new FileData("..", "", "", true));

        List<String> files = fileSystem.listCurrentPath(false);
        for (String file : files)
        {
            String fileSize = FileSystemUtils.getFileSize(file);
            String fileEditDate = FileSystemUtils.lastModifiedDate(file);

            FileData fileInfo = new FileData(FileSystemUtils.getFilenameFromPath(file), fileSize, fileEditDate,
                    FileSystemUtils.isDir(file));
            fileData.add(fileInfo);
        }

        fileViewer.getItems().clear();
        fileViewer.setItems(fileData);
        fileViewer.refresh();
    }

    /**
     * Обновить текста заголовков таблицы. Нужно для первой инициализации, а также для перезаполнения
     * при смене языка
     * */
    private void updateText()
    {
        fileNameColumn.setText(ResourceHandler.getString(StringKeys.PANEL_COLUMN_FILENAME));
        fileSizeColumn.setText(ResourceHandler.getString(StringKeys.PANEL_COLUMN_FILE_SIZE));
        fileEditDateColumn.setText(ResourceHandler.getString(StringKeys.PANEL_COLUMN_EDIT_DATE));
    }

    /**
     * Действия на нажатии кнопки "Открыть" в контекстном меню
     * @param fileName имя файла в текущей директории
     * */
    private void onContextItemOpenClick(String fileName)
    {
        String path = fileSystem.buildPath(fileName);

        if (FileSystemUtils.isDir(path))
        {
            fileSystem.goForward(fileName);
            refreshTable();
        }
        else
            fileSystem.openFile(fileName);
    }
}
