package sdm02;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ProgressBarRender extends JProgressBar implements TableCellRenderer {

    public ProgressBarRender() {
        super(0, 100);
        setStringPainted(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        float progress = 0;
        if (value instanceof String) {
            try { progress = Float.parseFloat(((String) value).replace("%","")); }
            catch(Exception ignored) {}
        }

        setValue((int) progress);
        setString(String.format("%.2f%%", progress));

        Object statusObj = table.getValueAt(row, 3);
        DownloadStatus status = (statusObj instanceof DownloadStatus) ? (DownloadStatus) statusObj : DownloadStatus.PENDING;

        if(status == DownloadStatus.COMPLETED) setForeground(new Color(0,150,0));
        else if(status == DownloadStatus.ERROR || status == DownloadStatus.CANCELLED) setForeground(Color.RED);
        else setForeground(Color.BLUE);

        return this;
    }
}

