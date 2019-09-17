package ru.gnkoshelev.elastic.logger.core.sender;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * @author Gregory Koshelev
 */
public final class SyncHttpSender extends HttpSender {
    private final CloseableHttpClient client = HttpClients.createDefault();

    public SyncHttpSender(final String url, final String auth, int retryCount) {
        super(url, auth, retryCount);
    }

    @Override
    public void execute(final HttpPost httpPost) {
        int leftRetryCount = this.retryCount;
        do {
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
                    System.err.println("SyncHttpSender request fails with HTTP code " + response.getStatusLine().getStatusCode());
                }
                if (!shouldRetry(response)) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } while (leftRetryCount-- > 0);
    }

    @Override
    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
