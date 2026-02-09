package widgets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import java.util.List;

import resourceHandler.IconName;
import resourceHandler.ResourceHandler;
import types.IconTypes;
import utils.FileSystem;
import models.FileData;


public class Panel extends VBox implements IWidget
{
    @FXML
    private TableView<FileData> fileViewer;

    @FXML
    private TableColumn<FileData, String> fileNameColumn;
    @FXML
    private TableColumn<FileData, String> fileSizeColumn;
    @FXML
    private TableColumn<FileData, String> fileEditDateColumn;

    private FileSystem fileSystem;

    public Panel()
    {
        load("/layouts/Panel.fxml");
        initUI();
    }

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

    private void setupTableColumns()
    {
        // настраиваем колонку с именем файла
        fileNameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
        fileNameColumn.setCellFactory(column -> new TableCell<FileData, String>()
        {
            private final ImageView imageView = new ImageView();

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
                        icon = ResourceHandler.getIcon(IconTypes.FILE_VIEWER, "24x24", IconName.BACK);
                    else
                    {
                        String fullPath = fileSystem.buildPath(file.getNameValue());
                        if (FileSystem.isDir(fullPath))
                            icon = ResourceHandler.getIcon(IconTypes.FILE_VIEWER, "24x24", IconName.FOLDER);
                        else
                            icon = ResourceHandler.getIcon(IconTypes.FILE_VIEWER, "24x24", IconName.FILE);
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

            return row;
        });
    }

    private void handleDoubleClick(FileData fileInfo)
    {
        String fileName = fileInfo.getNameValue();

        if (fileName.equals(".."))
            fileSystem.goBack();
        else if (fileInfo.isDirectory())
            fileSystem.goForward(fileName);

        refreshTable();
    }

    private void refreshTable()
    {
        ObservableList<FileData> fileData = FXCollections.observableArrayList();

        if (!fileSystem.isCurrentPathRoot())
            fileData.add(new FileData("..", "", "", true));

        List<String> files = fileSystem.listCurrentPath(false);
        for (String file : files)
        {
            String fileSize = FileSystem.getFileSize(file);
            String fileEditDate = FileSystem.lastModifiedDate(file);

            FileData fileInfo = new FileData(FileSystem.getFilenameFromPath(file), fileSize, fileEditDate,
                    FileSystem.isDir(file));
            fileData.add(fileInfo);
        }

        fileViewer.getItems().clear();
        fileViewer.setItems(fileData);
        fileViewer.refresh();
    }
}
