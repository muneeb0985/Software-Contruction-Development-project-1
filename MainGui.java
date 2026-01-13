package sdm02;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainGui extends JFrame {

    private JTextField urlField;
    private JButton addBtn, pauseBtn, resumeBtn, cancelBtn;
    private JTable downloadTable;
    private DefaultTableModel tableModel;
    private DownloadManager<HttpDownloadTask> manager;
    private Map<Integer, HttpDownloadTask> downloadTasks = new HashMap<>();

    public MainGui() {
        setTitle("SMART DOWNLOADER");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        manager = new DownloadManager<>();

        // ---------------- Top Panel ----------------
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(25, 25, 35));

        // Top title icon
        ImageIcon topIcon = new ImageIcon(getClass().getResource("download2.png"));
        topIcon = new ImageIcon(topIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        JLabel title = new JLabel("SMART DOWNLOADER", topIcon, JLabel.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 36));
        title.setForeground(new Color(0, 255, 200));
        title.setIconTextGap(20);

        // ---------------- URL Field ----------------
        urlField = new JTextField();
        urlField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        urlField.setBackground(new Color(50, 50, 60));
        urlField.setForeground(Color.WHITE);
        urlField.setCaretColor(Color.CYAN);
        urlField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 180, 140), 2, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // ---------------- Buttons with Icons ----------------
        addBtn = new JButton("Add Download");
        pauseBtn = new JButton("Pause");
        resumeBtn = new JButton("Resume");
        cancelBtn = new JButton("Cancel");

        // Load icons
        ImageIcon playIcon = new ImageIcon(getClass().getResource("play.png"));
        playIcon = new ImageIcon(playIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

        ImageIcon downloadIconBtn = new ImageIcon(getClass().getResource("download-circular-button.png"));
        downloadIconBtn = new ImageIcon(downloadIconBtn.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

        ImageIcon pauseIcon = new ImageIcon(getClass().getResource("video-pause-button.png"));
        pauseIcon = new ImageIcon(pauseIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

        ImageIcon cancelIcon = new ImageIcon(getClass().getResource("cancel.png"));
        cancelIcon = new ImageIcon(cancelIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

        // Assign icons
        addBtn.setIcon(downloadIconBtn);
        pauseBtn.setIcon(pauseIcon);
        resumeBtn.setIcon(playIcon);
        cancelBtn.setIcon(cancelIcon);

        // Button styling
        JButton[] buttons = {addBtn, pauseBtn, resumeBtn, cancelBtn};
        for (JButton b : buttons) {
            b.setFocusPainted(false);
            b.setBackground(new Color(0, 200, 180));
            b.setForeground(Color.BLACK);
            b.setFont(new Font("Montserrat", Font.BOLD, 14));
            b.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setHorizontalTextPosition(SwingConstants.RIGHT);
            b.setIconTextGap(10);
            b.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    b.setBackground(new Color(0, 255, 220));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    b.setBackground(new Color(0, 200, 180));
                }
            });
        }

        // ---------------- URL Panel (Adjusted) ----------------
        JPanel urlPanel = new JPanel(new BorderLayout(5, 5));
        urlPanel.setOpaque(false);

        // Only adjust URL field width to match nicely with button
        urlField.setPreferredSize(new Dimension(600, addBtn.getPreferredSize().height));

        urlPanel.add(urlField, BorderLayout.CENTER);
        urlPanel.add(addBtn, BorderLayout.EAST);

        // Optional wrapper for spacing
        JPanel urlWrapper = new JPanel(new BorderLayout());
        urlWrapper.setOpaque(false);
        urlWrapper.add(urlPanel, BorderLayout.CENTER);

        // ---------------- Action Buttons Panel ----------------
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionPanel.setOpaque(false);
        actionPanel.add(pauseBtn);
        actionPanel.add(resumeBtn);
        actionPanel.add(cancelBtn);

        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(urlWrapper, BorderLayout.CENTER);
        topPanel.add(actionPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // ---------------- Table Styling ----------------
        tableModel = new DefaultTableModel(
                new Object[]{"#", "File Name", "Progress", "Status"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        downloadTable = new JTable(tableModel);
        downloadTable.setRowHeight(32);
        downloadTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        downloadTable.getColumn("Progress").setCellRenderer(new ProgressBarRender());
        downloadTable.setBackground(new Color(30, 30, 40));
        downloadTable.setForeground(Color.WHITE);
        downloadTable.setSelectionBackground(new Color(0, 255, 180, 120));
        downloadTable.setSelectionForeground(Color.BLACK);
        downloadTable.setGridColor(new Color(60, 60, 70));

        downloadTable.getTableHeader().setFont(new Font("Montserrat", Font.BOLD, 16));
        downloadTable.getTableHeader().setBackground(new Color(0, 180, 140));
        downloadTable.getTableHeader().setForeground(Color.BLACK);
        ((DefaultTableCellRenderer) downloadTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        downloadTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(0, 255, 180, 120));
                    c.setForeground(Color.BLACK);
                } else if (row % 2 == 0) {
                    c.setBackground(new Color(40, 40, 50));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(new Color(35, 35, 45));
                    c.setForeground(Color.WHITE);
                }
                setHorizontalAlignment(column == 0 ? JLabel.CENTER : JLabel.LEFT);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(downloadTable);
        scrollPane.getViewport().setBackground(new Color(30, 30, 40));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 140), 2, true));
        add(scrollPane, BorderLayout.CENTER);

        // ---------------- Actions ----------------
        addBtn.addActionListener(e -> addDownload());
        pauseBtn.addActionListener(e -> control("pause"));
        resumeBtn.addActionListener(e -> control("resume"));
        cancelBtn.addActionListener(e -> control("cancel"));
    }

    private void addDownload() {
        try {
            URL url = new URL(urlField.getText().trim());
            String fileName = url.getPath();
            fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
            if (fileName.isEmpty()) fileName = "downloadedfile.png";

            HttpDownloadTask task = new HttpDownloadTask(url, fileName);
            manager.addDownload(task);

            int row = tableModel.getRowCount();
            tableModel.addRow(new Object[]{row + 1, fileName, "0%", task.getStatus()});
            downloadTasks.put(row, task);

            new Timer(500, ev -> {
                if (task.getStatus() == DownloadStatus.ERROR) {
                    tableModel.setValueAt(task.getErrorMessage(), row, 3);
                } else {
                    tableModel.setValueAt(
                            String.format("%.2f%%", task.getProgress()), row, 2);
                    tableModel.setValueAt(task.getStatus(), row, 3);
                }
            }).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid URL", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void control(String action) {
        int viewRow = downloadTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a download first");
            return;
        }

        int modelRow = downloadTable.convertRowIndexToModel(viewRow);
        HttpDownloadTask task = downloadTasks.get(modelRow);
        if (task == null) return;

        switch (action) {
            case "pause" -> task.pause();
            case "resume" -> task.resume();
            case "cancel" -> task.cancel();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGui().setVisible(true));
    }
}


