/*
 * Copyright (C) 2016 Simbiose Ventures.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.slicingdice.jslicer.core;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import sun.rmi.runtime.Log;

import javax.net.ssl.*;

/**
 * A simple helper to make post requests with okhttp3
 *
 * @author Simbiose Ventures
 * @version 0.2
 * @since 2016-08-10
 */
public class Requester {
    private static final AsyncHttpClient client = Dsl.asyncHttpClient();

    private static final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    /**
     * Makes a POST request
     *
     * @param url     A url String to make request
     * @param data    A JSON to send in request
     * @param token   A token to access URL
     * @param timeout A Integer with time max to API response
     * @return A request response
     * @throws IOException
     */
    public static void post(final String url, final String data, final String token,
                            final int timeout) throws IOException {
        final ListenableFuture<Response> whenExecute = client.preparePost(url)
                .setBody(data)
                .setHeader("Authorization", token)
                .setHeader("Content-Type", "application/json")
                .setReadTimeout(timeout * 1000)
                .setRequestTimeout(timeout * 1000)
                .execute();

        whenExecute.addListener(() -> {
            try {
                final Response response = whenExecute.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }, executor);
    }

    /**
     * Makes a PUT request
     *
     * @param url     A url String to make request
     * @param data    A JSON to send in request
     * @param token   A token to access URL
     * @param timeout A Integer with time max to API response
     * @return A request response
     * @throws IOException
     */
    public static Response put(final String url, final String data, final String token,
                               final int timeout) throws IOException {
        final OkHttpClient clientConfigured = getConfiguredClient()
                .newBuilder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();

        final RequestBody body = RequestBody.create(null, data);
        final Request request = new Request.Builder()
                .url(url)
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .put(body)
                .build();
        final Response response = clientConfigured.newCall(request).execute();
        return response;
    }

    /**
     * Makes a DELETE request
     *
     * @param url     A url String to make request
     * @param token   A token to access URL
     * @param timeout A Integer with time max to API response
     * @return A request response
     * @throws IOException
     */
    public static Response delete(final String url, final String token, final int timeout)
            throws IOException {
        final OkHttpClient clientConfigured = getConfiguredClient()
                .newBuilder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .delete()
                .build();
        final Response response = clientConfigured.newCall(request).execute();
        return response;
    }

    /**
     * Makes a GET request
     *
     * @param url     A url String to make request
     * @param token   A token to access URL
     * @param timeout A Integer with time max to API response
     * @return A request response
     * @throws IOException
     */
    public static Response get(final String url, final String token, final int timeout)
            throws IOException {
        final OkHttpClient clientConfigured = getConfiguredClient()
                .newBuilder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .build();
        final Response response = clientConfigured.newCall(request).execute();
        return response;
    }

    private static OkHttpClient getConfiguredClient() {
        Security.setProperty("jdk.certpath.disabledAlgorithms", "MD2, RSA keySize < 1024");
        Security.setProperty("jdk.tls.disabledAlgorithms", "SSLv3, RC4, DH keySize < 768");
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            ctx.init(null, new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        client = new OkHttpClient().newBuilder()
                .sslSocketFactory(ctx.getSocketFactory())
                .hostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
        return client;
    }
}
