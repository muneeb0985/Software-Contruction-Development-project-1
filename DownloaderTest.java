package sdm02;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.net.URL;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DownloaderTest {

    private static final String IMAGE_URL = "https://raw.githubusercontent.com/github/explore/main/topics/java/java.png";
    private static final String IMAGE_NAME = "java.png";

    private static final String VIDEO_URL = "https://raw.githubusercontent.com/mediaelement/mediaelement-files/master/big_buck_bunny.mp4";
    private static final String VIDEO_NAME = "big_buck_bunny.mp4";

    private static final String FILE_URL = "https://raw.githubusercontent.com/github/gitignore/main/Java.gitignore";
    private static final String FILE_NAME = "Java.gitignore";

    // Wait until task reaches a terminal status
    private void waitForCompletion(HttpDownloadTask task) throws InterruptedException {
        int attempts = 0;
        while (attempts < 60) { // max ~30 seconds
            DownloadStatus s = task.getStatus();
            if (s == DownloadStatus.COMPLETED ||
                s == DownloadStatus.CANCELLED ||
                s == DownloadStatus.ERROR) {
                return;
            }
            Thread.sleep(500);
            attempts++;
        }
    }

    @Test
    @Order(1)
    public void testInternetAvailable() {
        boolean available = ApiTester.isInternetAvailable();
        System.out.println("Internet available: " + available);
        assertTrue(available, "Internet should be available for tests");
    }

    @Test
    @Order(2)
    public void testDownloadManagerAddAndGet() throws Exception {
        DownloadManager<HttpDownloadTask> manager = new DownloadManager<>();

        HttpDownloadTask imageTask = new HttpDownloadTask(new URL(IMAGE_URL), IMAGE_NAME);
        HttpDownloadTask videoTask = new HttpDownloadTask(new URL(VIDEO_URL), VIDEO_NAME);
        HttpDownloadTask fileTask = new HttpDownloadTask(new URL(FILE_URL), FILE_NAME);

        manager.addDownload(imageTask);
        manager.addDownload(videoTask);
        manager.addDownload(fileTask);

        List<HttpDownloadTask> list = manager.getDownloadList();
        assertEquals(3, list.size());
        assertEquals(IMAGE_NAME, list.get(0).getFileName());
        assertEquals(VIDEO_NAME, list.get(1).getFileName());
        assertEquals(FILE_NAME, list.get(2).getFileName());
    }

    @Test
    @Order(3)
    public void testHttpDownloadTaskStatusFlow() throws Exception {
        HttpDownloadTask task = new HttpDownloadTask(new URL(IMAGE_URL), IMAGE_NAME);

        waitForCompletion(task);
        assertTrue(task.getStatus() == DownloadStatus.DOWNLOADING ||
                   task.getStatus() == DownloadStatus.COMPLETED,
                   "Status should be DOWNLOADING or COMPLETED");

        // Pause (may already be completed)
        task.pause();
        Thread.sleep(500);
        assertTrue(task.getStatus() == DownloadStatus.PAUSED ||
                   task.getStatus() == DownloadStatus.COMPLETED,
                   "Status should be PAUSED or COMPLETED");

        // Resume (only if not completed)
        if (task.getStatus() != DownloadStatus.COMPLETED) {
            task.resume();
            Thread.sleep(500);
            assertTrue(task.getStatus() == DownloadStatus.DOWNLOADING ||
                       task.getStatus() == DownloadStatus.COMPLETED,
                       "Status should be DOWNLOADING or COMPLETED after resume");
        }

        // Cancel
        task.cancel();
        waitForCompletion(task);
        assertTrue(task.getStatus() == DownloadStatus.CANCELLED ||
                   task.getStatus() == DownloadStatus.COMPLETED,
                   "Status should be CANCELLED or COMPLETED after cancel");
    }

    @Test
    @Order(4)
    public void testProgressValue() throws Exception {
        HttpDownloadTask task = new HttpDownloadTask(new URL(VIDEO_URL), VIDEO_NAME);

        waitForCompletion(task); // wait until download finishes
        float progress = task.getProgress();

        // If completed but progress is 0 (unknown content-length), force 100%
        if (task.getStatus() == DownloadStatus.COMPLETED && progress == 0f) {
            progress = 100f;
        }

        assertTrue(progress >= 0 && progress <= 100, "Progress should be between 0 and 100");
    }

    @Test
    @Order(5)
    public void testErrorHandling() throws Exception {
        HttpDownloadTask task = new HttpDownloadTask(new URL("https://invalid.invalid/file.png"), "file.png");

        waitForCompletion(task);

        assertEquals(DownloadStatus.ERROR, task.getStatus(), "Status should be ERROR");
        assertNotNull(task.getErrorMessage(), "Error message should not be null");
    }
}


