package com.github.jupyter0.notenoughinfo.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

public class APIUtil {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private static SSLContext ctx;
    private static final Gson gson = new Gson();

    public static class Request {
        private final List<NameValuePair> queryArguments = new ArrayList<>();
        private String baseUrl = null;
        private boolean shouldGunzip = false;
        private String method = "GET";

        public Request method(String method) {
            this.method = method;
            return this;
        }

        public Request url(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Request queryArgument(String key, String value) {
            queryArguments.add(new BasicNameValuePair(key, value));
            return this;
        }

        public Request queryArguments(Collection<NameValuePair> queryArguments) {
            this.queryArguments.addAll(queryArguments);
            return this;
        }

        public Request gunzip() {
            shouldGunzip = true;
            return this;
        }

        private CompletableFuture<URL> buildUrl() {
            CompletableFuture<URL> fut = new CompletableFuture<>();
            try {
                fut.complete(new URIBuilder(baseUrl)
                        .addParameters(queryArguments)
                        .build()
                        .toURL());
            } catch (URISyntaxException |
                    MalformedURLException |
                    NullPointerException e) { // Using CompletableFuture as an exception monad, isn't that exiting?
                fut.completeExceptionally(e);
            }
            return fut;
        }

        public CompletableFuture<String> requestString() {
            return buildUrl().thenApplyAsync(url -> {
                try {
                    InputStream inputStream = null;
                    URLConnection conn = null;
                    try {
                        conn = url.openConnection();
                        if (conn instanceof HttpsURLConnection && ctx != null) {
                            HttpsURLConnection sslConn = (HttpsURLConnection) conn;
                            sslConn.setSSLSocketFactory(ctx.getSocketFactory());
                        }
                        if (conn instanceof HttpURLConnection) {
                            ((HttpURLConnection) conn).setRequestMethod(method);
                        }
                        conn.setConnectTimeout(10000);
                        conn.setReadTimeout(10000);

                        inputStream = conn.getInputStream();

                        if (shouldGunzip) {
                            inputStream = new GZIPInputStream(inputStream);
                        }

                        // While the assumption of UTF8 isn't always true; it *should* always be true.
                        // Not in the sense that this will hold in most cases (although that as well),
                        // but in the sense that any violation of this better have a good reason.
                        return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                    } finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } finally {
                            if (conn instanceof HttpURLConnection) {
                                ((HttpURLConnection) conn).disconnect();
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e); // We can rethrow, since supplyAsync catches exceptions.
                }
            }, executorService);
        }

        public CompletableFuture<JsonObject> requestJson() {
            return requestJson(JsonObject.class);
        }

        public <T> CompletableFuture<T> requestJson(Class<? extends T> clazz) {
            return requestString().thenApply(str -> gson.fromJson(str, clazz));
        }
    }

    public Request request() {
        return new Request();
    }

    public static Request newAnonymousHypixelApiRequest(String apiPath) {
        return new Request()
                .url("https://api.hypixel.net/" + apiPath);
    }
}
