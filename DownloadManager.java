package sdm02;

import java.util.ArrayList;
import java.util.List;

public class DownloadManager<T extends HttpDownloadTask> {

    private List<T> downloadList = new ArrayList<>(); // ✅ initialized

    public void addDownload(T task) {
        downloadList.add(task);
    }

    public List<T> getDownloadList() {
        return downloadList;
    }

    public T getDownload(int index) {
        if (index >= 0 && index < downloadList.size()) return downloadList.get(index);
        return null;
    }
}
