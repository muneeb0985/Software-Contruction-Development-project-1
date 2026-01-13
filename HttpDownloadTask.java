package sdm02;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownloadTask implements Runnable {

    private final URL url;
    private final String fileName;
    private volatile boolean paused = false;
    private volatile boolean cancelled = false;
    private volatile float progress = 0f;
    private volatile DownloadStatus status = DownloadStatus.PENDING;
    private volatile String errorMessage = "";
    private Thread thread;

    public HttpDownloadTask(URL url, String fileName) {
        this.url = url;
        this.fileName = fileName;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        status = DownloadStatus.DOWNLOADING;
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            int response = connection.getResponseCode();
            if (response / 100 != 2) {
                status = DownloadStatus.ERROR;
                errorMessage = "HTTP Error: " + response;
                return;
            }

            long totalBytes = connection.getContentLengthLong();
            if (totalBytes <= 0) totalBytes = -1; // unknown size

            File outputFile = new File(System.getProperty("user.home") + "/Downloads/" + fileName);

            try (InputStream in = connection.getInputStream();
                 FileOutputStream fos = new FileOutputStream(outputFile)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                long downloaded = 0;

                while ((bytesRead = in.read(buffer)) != -1) {
                    synchronized (this) {
                        while (paused) {
                            status = DownloadStatus.PAUSED;
                            wait();
                            status = DownloadStatus.DOWNLOADING;
                        }
                    }

                    if (cancelled) {
                        status = DownloadStatus.CANCELLED;
                        break;
                    }

                    fos.write(buffer, 0, bytesRead);
                    downloaded += bytesRead;

                    if (totalBytes > 0) {
                        progress = (float) downloaded / totalBytes * 100;
                    }
                }

                // Ensure progress = 100 if completed
                if (status != DownloadStatus.CANCELLED && status != DownloadStatus.ERROR) {
                    progress = 100f;
                    status = DownloadStatus.COMPLETED;
                } else if (status == DownloadStatus.CANCELLED) {
                    outputFile.delete();
                }

            }

        } catch (Exception e) {
            status = DownloadStatus.ERROR;
            errorMessage = e.getMessage();
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
        notifyAll();
    }

    public synchronized void cancel() {
        cancelled = true;
        notifyAll();
    }

    public float getProgress() { return progress; }
    public DownloadStatus getStatus() { return status; }
    public String getFileName() { return fileName; }
    public URL getUrl() { return url; }
    public String getErrorMessage() { return errorMessage; }
}

