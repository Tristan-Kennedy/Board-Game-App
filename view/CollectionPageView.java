//Design Pattern: Composite
package view;

import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CollectionPageView {

    private GameCollection currentlyDisplayedCollection;
    private DefaultTableModel tableModel = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private JPanel collectionPagePanel;
    private JTable collectionsTable;
    private JTextField addCollectionName;
    private JButton addCollectionButton;
    private GameListView gameListView;
    private JPanel collectionGameListViewPanel;
    private JButton saveButton;
    private JButton deleteSelectedButton;
    private JButton removeSelectedGameButton;
    private JLabel addCollectionLabel;
    private JPanel collectionsHeader;
    private JPanel collectionButtons;

    public CollectionPageView() {
        currentlyDisplayedCollection = null;

        tableModel.addColumn("Collection Name");
        collectionsTable.setModel(tableModel);
        collectionsTable.setFont(collectionsTable.getFont().deriveFont(26f));
        collectionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        collectionsTable.setSelectionBackground(ImageCellRenderer.GREY_SELECTED);
        collectionsTable.setSelectionForeground(Color.BLACK);

        addCollectionLabel.setFont(addCollectionLabel.getFont().deriveFont(Font.BOLD, 16));
        addCollectionLabel.setText("Add Collection: ");

        // Text box
        addCollectionName.setFont(addCollectionName.getFont().deriveFont(16f));
        // Enables hitting the Enter key to submit
        addCollectionName.addActionListener(e -> {
            String name = addCollectionName.getText();
            addCollectionName.setText("");

            if (name.length() > 0) {
                GameCollection collection = new GameCollection(name);
                if (UserDataManager.currentUser.addCollection(collection))
                    tableModel.addRow(new Object[]{name});
            }
        });

        // Add Collection Button
        int height = addCollectionName.getPreferredSize().height;
        addCollectionButton.setPreferredSize(new Dimension(height, height));
        // Draw a + sign in the button
        addCollectionButton.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                int off = height/2;
                // Vertical Lines
                g2.drawLine(off, off-6, off, off+5);
                g2.drawLine(off-1, off-6, off-1, off+5);
                // Horizontal Lines
                g2.drawLine(off-6, off, off+5, off);
                g2.drawLine(off-6, off-1, off+5, off-1);
            }
            @Override public int getIconWidth() { return 11; }
            @Override public int getIconHeight() { return 11; }
        });
        addCollectionButton.setHorizontalAlignment(JLabel.CENTER);
        addCollectionButton.addActionListener(e -> {
            String name = addCollectionName.getText();
            addCollectionName.setText("");

            if (name.length() > 0) {
                GameCollection collection = new GameCollection(name);
                if (UserDataManager.currentUser.addCollection(collection))
                    tableModel.addRow(new Object[]{name});
            }
        });

        deleteSelectedButton.addActionListener(e -> {
            if (collectionsTable.getSelectedRow() != -1) {
                String name = (String) tableModel.getValueAt(collectionsTable.convertRowIndexToModel(collectionsTable.getSelectedRow()), 0);
                UserDataManager.currentUser.deleteCollection(UserDataManager.currentUser.getGameCollectionByName(name));
                tableModel.removeRow(collectionsTable.getSelectedRow());
            }
        });

        saveButton.addActionListener(e -> UserDataManager.saveGameCollections());

        collectionsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String name = (String) tableModel.getValueAt(collectionsTable.convertRowIndexToModel(collectionsTable.getSelectedRow()), 0);
                    currentlyDisplayedCollection = UserDataManager.currentUser.getGameCollectionByName(name);
                    gameListView.setTable(currentlyDisplayedCollection);
                }
            }
        });

        // Sets the "Remove Selected Game" button to the width of the table
        removeSelectedGameButton.setMaximumSize(new Dimension(collectionGameListViewPanel.getPreferredSize().width, removeSelectedGameButton.getPreferredSize().height));
        removeSelectedGameButton.addActionListener(e -> {
            JTable gameTable = gameListView.getCurrentTable();
            int gameID = (int) gameTable.getModel().getValueAt(gameTable.convertRowIndexToModel(gameTable.getSelectedRow()), 4);
            currentlyDisplayedCollection.remove(GameDatabaseLoader.mainList.getGame(gameID));
            gameListView.setTable(currentlyDisplayedCollection);
        });

        collectionsTable.setModel(tableModel);
        collectionsTable.setRowHeight(60);
    }

    public void setCurrentUser(User user) {

        tableModel.setRowCount(0);

        for (GameCollection c : user.getCollectionList())
            tableModel.addRow(new Object[]{c.getName()});

        gameListView.setTable(new GameList());
    }

    public JPanel getPanel() {
        return collectionPagePanel;
    }

    private void createUIComponents() {
        gameListView = new GameListView(new GameList());
        collectionGameListViewPanel = gameListView.getPanel();
    }

    public void addSwitchTabListener(SwitchTabListener tsl) {
        gameListView.addSwitchTabListener(tsl);
    }

    public void updateTableData(Game g) {
        gameListView.updateTableData(g);
    }

    public void refreshTable() {
        if (currentlyDisplayedCollection != null)
            gameListView.setTable(currentlyDisplayedCollection);
    }
}
