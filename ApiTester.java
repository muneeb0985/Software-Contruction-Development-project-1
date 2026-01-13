package sdm02;

import java.net.HttpURLConnection;
import java.net.URL;

public class ApiTester {
    public static boolean isInternetAvailable() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://www.google.com").openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000);
            connection.connect();
            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
