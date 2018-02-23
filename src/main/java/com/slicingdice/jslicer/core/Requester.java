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

import com.slicingdice.jslicer.exceptions.api.SlicingDiceException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.json.JSONObject;

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


    public static void close() throws IOException {
        executor.shutdown();
        client.close();
    }

    /**
     * Makes a POST request
     *
     * @param url     A url String to make request
     * @param data    A JSON to send in request
     * @param token   A token to access URL
     * @param timeout A Integer with time max to API response
     */
    public static Future<Response> post(final String url, final String data, final String token,
                                        final int timeout) {
        return client.preparePost(url)
                .setBody(data)
                .setHeader("Authorization", token)
                .setHeader("Content-Type", "application/json")
                .setReadTimeout(timeout * 1000)
                .setRequestTimeout(timeout * 1000)
                .execute();
    }

    /**
     * Makes a POST request
     *
     * @param url     A url String to make request
     * @param data    A JSON to send in request
     * @param token   A token to access URL
     * @param timeout A Integer with time max to API response
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public static void post(final String url, final String data, final String token,
                            final int timeout, final HandlerResponse handler) {
        final ListenableFuture<Response> whenExecute = client.preparePost(url)
                .setBody(data)
                .setHeader("Authorization", token)
                .setHeader("Content-Type", "application/json")
                .setReadTimeout(timeout * 1000)
                .setRequestTimeout(timeout * 1000)
                .execute();

        addListener(handler, whenExecute);
    }

    /**
     * Makes a PUT request
     *
     * @param url     A url String to make request
     * @param data    A JSON to send in request
     * @param token   A token to access URL
     * @param timeout A Integer with time max to API response
     */
    public static Future<Response> put(final String url, final String data, final String token,
                                       final int timeout) {
        return client.preparePut(url)
                .setBody(data)
                .setHeader("Authorization", token)
                .setHeader("Content-Type", "application/json")
                .setReadTimeout(timeout * 1000)
                .setRequestTimeout(timeout * 1000)
                .execute();
    }

    /**
     * Makes a PUT request
     *
     * @param url     A url String to make request
     * @param data    A JSON to send in request
     * @param token   A token to access URL
     * @param timeout A Integer with time max to API response
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public static void put(final String url, final String data, final String token,
                           final int timeout, final HandlerResponse handler) {
        final ListenableFuture<Response> whenExecute = client.preparePut(url)
                .setBody(data)
                .setHeader("Authorization", token)
                .setHeader("Content-Type", "application/json")
                .setReadTimeout(timeout * 1000)
                .setRequestTimeout(timeout * 1000)
                .execute();

        addListener(handler, whenExecute);
    }

    /**
     * Makes a DELETE request
     *
     * @param url     A url String to make request
     * @param token   A token to access URL
     * @param timeout A Integer with time max to API response
     */
    public static Future<Response> delete(final String url, final String token, final int timeout) {
        return client.prepareDelete(url)
                .setHeader("Authorization", token)
                .setHeader("Content-Type", "application/json")
                .setReadTimeout(timeout * 1000)
                .setRequestTimeout(timeout * 1000)
                .execute();
    }

    /**
     * Makes a DELETE request
     *
     * @param url     A url String to make request
     * @param token   A token to access URL
     * @param timeout A Integer with time max to API response
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public static void delete(final String url, final String token, final int timeout,
                              final HandlerResponse handler) {
        final ListenableFuture<Response> whenExecute = client.prepareDelete(url)
                .setHeader("Authorization", token)
                .setHeader("Content-Type", "application/json")
                .setReadTimeout(timeout * 1000)
                .setRequestTimeout(timeout * 1000)
                .execute();

        addListener(handler, whenExecute);
    }

    /**
     * Makes a GET request
     *
     * @param url     A url String to make request
     * @param token   A token to access URL
     * @param timeout A Integer with time max to API response
     */
    public static Future<Response> get(final String url, final String token, final int timeout) {
        return client.prepareGet(url)
                .setHeader("Authorization", token)
                .setHeader("Content-Type", "application/json")
                .setReadTimeout(timeout * 1000)
                .setRequestTimeout(timeout * 1000)
                .execute();
    }

    /**
     * Makes a GET request
     *
     * @param url     A url String to make request
     * @param token   A token to access URL
     * @param timeout A Integer with time max to API response
     * @param handler A handler that will call onError or onSuccess when the request finishes
     */
    public static void get(final String url, final String token, final int timeout,
                           final HandlerResponse handler) {
        final ListenableFuture<Response> whenExecute = client.prepareGet(url)
                .setHeader("Authorization", token)
                .setHeader("Content-Type", "application/json")
                .setReadTimeout(timeout * 1000)
                .setRequestTimeout(timeout * 1000)
                .execute();

        addListener(handler, whenExecute);
    }

    private static void addListener(final HandlerResponse handler,
                                    final ListenableFuture<Response> whenExecute) {
        whenExecute.addListener(() -> {
            try {
                final Response response = whenExecute.get();

                handler.checkRequest(response.getResponseBody(), response.getHeaders(),
                        response.getStatusCode());
            } catch (final Exception e) {
                throw new SlicingDiceException("An error occurred while requesting SlicingDice");
            }
        }, executor);
    }

    public static JSONObject responseToJson(final Response response) {
        return new JSONObject(response.getResponseBody());
    }
}
