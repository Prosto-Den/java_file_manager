package controllers;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Optional;

import app.AppContext;
import events.EventBus;
import events.FileSystemChangedEvent;

import java.util.List;

import models.ContextMenuItemId;
import models.StringKeys;
import models.TrashItem;
import utils.i18n.LanguageManager;
import utils.ui.context.IContextMenuConfig;

public class TrashController implements Initializable
{
    @FXML 
    private TableView<TrashItem> trashTable;
    @FXML
    private TableColumn<TrashItem, String> nameColumn;
    @FXML
    private TableColumn<TrashItem, String> pathColumn;
    @FXML
    private TableColumn<TrashItem, String> dateColumn;

    private class TrashMenuContext extends IContextMenuConfig
    {
        public TrashMenuContext()
        {
        }

        @Override
        public void executeAction(String actionID)
        {
            switch (actionID)
            {
                case (ContextMenuItemId.RESTORE_ITEM) -> onRestoreItem();
                case (ContextMenuItemId.DELETE_PERMANENTLY_ITEM) -> onDeletePermanentlyItem();
                default -> {/*ничего не делаем*/}
            }
        }
        
        @Override
        public boolean isActionEnabled(String actionID)
        {
            return data != null;
        }

        @Override
        public Node getActionGraphic(String actionID)
        {
            return null;
        }

        private Optional<TrashItem> getTrashItem()
        {
            if (data != null && data instanceof TrashItem)
                return Optional.of((TrashItem) data);
            return Optional.empty();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().originalNameProperty());
        pathColumn.setCellValueFactory(cellData -> cellData.getValue().originalPathProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().deletionDateProperty());

        trashTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        createContextMenu();

        refreshTable();
    }

    // Приватные методы
    
    private void createContextMenu()
    {
        ContextMenu contextMenu = AppContext.getContextMenuManager().createTrashContextMenu();
        AppContext.getContextMenuManager().configureContextMenu(contextMenu, new TrashMenuContext());
    }

    private void onRestoreItem()
    {
        ObservableList<TrashItem> selectedItems = trashTable.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty())
            return;

        boolean anyError = false;

        for (TrashItem item : selectedItems)
            anyError |= AppContext.getTrashManager().restoreItem(item);

        if (anyError)
        {
            LanguageManager langManager = AppContext.getLanguageManager();
            showErrorAlert(langManager.getString(StringKeys.ALERT_RESTORE_ERROR_TITLE), langManager.getString(StringKeys.ALERT_RESTORE_ERROR_TEXT));
        }

        refreshTable();

        // TODO прилумать, как передать ID файловой системы
        //EventBus.publish(new FileSystemChangedEvent());
    }

    private void onDeletePermanentlyItem()
    {
        ObservableList<TrashItem> selectedItems = trashTable.getSelectionModel().getSelectedItems();
        if (selectedItems.isEmpty())
            return;

        // TODO сделать менеджер для предупреждений
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        LanguageManager langManager = AppContext.getLanguageManager();
        confirm.setTitle(langManager.getString(StringKeys.ALERT_DELETE_TITLE));
        confirm.setHeaderText(langManager.getString(StringKeys.ALERT_DELETE_HEADER));
        confirm.setContentText(langManager.getString(StringKeys.ALERT_DELETE_TEXT));

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK)
        {
            for (TrashItem item : selectedItems)
                AppContext.getTrashManager().deletePermanently(item);
            refreshTable();
        }

    }

    private void refreshTable()
    {
        List<TrashItem> items = AppContext.getTrashManager().getTrashItems();
        trashTable.setItems(FXCollections.observableArrayList(items));
    }

    // TODO вынести в отдельный менеджер предупреждающих сообщений
    private void showErrorAlert(String title, String text)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
