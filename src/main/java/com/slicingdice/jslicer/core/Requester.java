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
import java.util.concurrent.TimeUnit;

import okhttp3.*;
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
    static OkHttpClient client;

    /**
     * @param url     A url String to make request
     * @param data    A JSON to send in request
     * @param token   A token to access URL
     * @param timeout A Integer with time max to API response
     * @return A request response
     * @throws IOException
     */
    public static Response post(String url, String data, String token, int timeout) throws IOException {
        OkHttpClient clientConfigured = getConfiguredClient()
                .newBuilder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
        RequestBody body = RequestBody.create(null, data);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .post(body)
                .build();
        Response response = clientConfigured.newCall(request).execute();
        return response;
    }

    public static Response put(String url, String data, String token, int timeout) throws IOException {
        OkHttpClient clientConfigured = getConfiguredClient()
                .newBuilder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();

        RequestBody body = RequestBody.create(null, data);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .put(body)
                .build();
        Response response = clientConfigured.newCall(request).execute();
        return response;
    }

    public static Response delete(String url, String token, int timeout) throws IOException {
        OkHttpClient clientConfigured = getConfiguredClient()
                .newBuilder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .delete()
                .build();
        Response response = clientConfigured.newCall(request).execute();
        return response;
    }

    public static Response get(String url, String token, int timeout) throws IOException {
        OkHttpClient clientConfigured = getConfiguredClient()
                .newBuilder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .build();
        Response response = clientConfigured.newCall(request).execute();
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
            ctx.init(null, new TrustManager[] {
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
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
