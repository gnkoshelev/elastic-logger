package ru.gnkoshelev.elastic.logger.core.sender;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;

import java.io.Closeable;

/**
 * @author Gregory Koshelev
 */
public abstract class HttpSender implements Closeable {
    private final String url;
    private final String auth;
    protected final int retryCount;

    HttpSender(String url, String auth, int retryCount) {
        this.url = url;
        this.auth = auth;
        this.retryCount = retryCount;
    }

    public void send(final String index, final byte[] data) {
        HttpPost httpPost = new HttpPost(url + index + "/LogEvent/_bulk");
        if (auth != null) {
            httpPost.setHeader("Authorization", auth);
        }

        HttpEntity entity = new ByteArrayEntity(data, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        execute(httpPost);
    }

    public abstract void execute(final HttpPost httpPost);
    public abstract void close();

    protected boolean shouldRetry(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 502 || statusCode == 503 || statusCode == 504;
    }
}
