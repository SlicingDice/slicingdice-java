# SlicingDice Official Java Client (v1.0)
![](https://circleci.com/gh/SlicingDice/slicingdice-java/tree/master.svg?style=shield)

Official Java client for [SlicingDice](http://www.slicingdice.com/), Data Warehouse and Analytics Database as a Service.

## Documentation

If you are new to SlicingDice, check our [quickstart guide](http://panel.slicingdice.com/docs/#quickstart-guide) and learn to use it in 15 minutes.

Please refer to the [SlicingDice official documentation](http://panel.slicingdice.com/docs/) for more information on [analytics databases](http://panel.slicingdice.com/docs/#analytics-concepts), [data modeling](http://panel.slicingdice.com/docs/#data-modeling), [indexing](http://panel.slicingdice.com/docs/#data-indexing), [querying](http://panel.slicingdice.com/docs/#data-querying), [limitations](http://panel.slicingdice.com/docs/#current-slicingdice-limitations) and [API details](http://panel.slicingdice.com/docs/#api-details).

## Tests and Examples

Whether you want to test the client installation or simply check more examples on how the client works, take a look at [tests and examples directory](src/test/java/com/simbiose/jslicer/).

## Installing

[Click here]() to download our Java client as a `jar` file. After downloading it, you only need to import the `jar` into your project path.

Also, our Java client can be installed via [Maven](https://maven.apache.org/) by adding the following configurations in our pom.xml.

```xml
<dependency>
    <groupId>com.simbioseventures</groupId>
    <artifactId>jslicer</artifactId>
    <version>0.1</version>
    <scope>test</scope>
</dependency>
```

Please note the client has the following dependencies:

* [gson 2.3.2](https://github.com/google/gson)
* [okhttp 3.2.0](http://square.github.io/okhttp/)

[Maven](https://maven.apache.org/) can be used to install the dependencies with the following configurations.

```xml
<dependency>
    <groupid>com.google.code.gson</groupid>
    <artifactid>gson</artifactid>
    <version>2.3.1</version>
</dependency>

<dependency>
    <groupid>com.squareup.okhttp3</groupid>
    <artifactid>okhttp</artifactid>
    <version>3.3.1</version>
</dependency>
```

## Usage

```java
import SlicingDice


public class App
{
    public static void main( String[] args ) throws IOException
    {
        SlicerDicer client = new SlicerDicer("API_KEY")

        // Creating a field
        // Indexing data
        JSONObject indexData = new JSONObject()
            .put("user1@slicingdice.com",
                new JSONObject()
                    .put("age", 22))
            .put("auto-create-fields", true);
        System.out.println(client.index(indexData));

        // Querying data
        JSONObject queryData = new JSONObject()
            .put("users-between-20-and-40",
                new JSONArray()
                    .put(new JSONObject()
                        .put("age",
                            new JSONObject()
                                .put("range",
                                    new JSONArray()
                                        .put(20)
                                        .put(40))));
        System.out.println(client.count_entity(queryData));
    }
}
```

## Reference

`SlicingDice` encapsulates logic for sending requests to the API. Its methods are thin layers around the [API endpoints](http://panel.slicingdice.com/docs/#api-details-api-endpoints), so their parameters and return values are JSON-like `JSONObject` objects with the same syntax as the [API endpoints](http://panel.slicingdice.com/docs/#api-details-api-endpoints)

### Attributes

* `sdAddress (String)` - [Connection endpoint](http://panel.slicingdice.com/docs/#api-details-api-connection-connection-endpoints) to use when generating requests to SlicingDice.

### Constructors

`SlicingDice(String key, int timeout)`
* `key (String)` - [API key](http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys) to authenticate requests with the SlicingDice API.

`SlicingDice(APIKey key, int timeout)`
* `key (APIKey)` - [API key](http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys) to authenticate requests with the SlicingDice API.

`SlicingDice(String key, int timeout)`
* `key (String)` - [API key](http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys) to authenticate requests with the SlicingDice API.
* `timeout (int)` - Amount of time, in seconds, to wait for results for each request.

`SlicingDice(APIKey key, int timeout)`
* `key (APIKey)` - [API key](http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys) to authenticate requests with the SlicingDice API.
* `timeout (int)` - Amount of time, in seconds, to wait for results for each request.

### `JSONObject getProjects()`
Get all created projects, both active and inactive ones. This method corresponds to a [GET request at /project](http://panel.slicingdice.com/docs/#api-details-api-endpoints-get-project).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        JSONObject result = slicingDice.getProjects();
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "active": [
        {
            "name": "Project 1",
            "description": "My first project",
            "data-expiration": 30,
            "created-at": "2016-04-05T10:20:30Z"
        }
    ],
    "inactive": [
        {
            "name": "Project 2",
            "description": "My second project",
            "data-expiration": 90,
            "created-at": "2016-04-05T10:20:30Z"
        }
    ]
}
```

### `JSONObject getFields()`
Get all created fields, both active and inactive ones. This method corresponds to a [GET request at /field](http://panel.slicingdice.com/docs/#api-details-api-endpoints-get-field).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        JSONObject result = slicingDice.getFields();
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "active": [
        {
          "name": "Model",
          "api-name": "car-model",
          "description": "Car models from dealerships",
          "type": "string",
          "category": "general",
          "cardinality": "high",
          "storage": "latest-value"
        }
    ],
    "inactive": [
        {
          "name": "Year",
          "api-name": "car-year",
          "description": "Year of manufacture",
          "type": "integer",
          "category": "general",
          "storage": "latest-value"
        }
    ]
}
```

### `JSONObject createField(JSONObject data)`
Create a new field. This method corresponds to a [POST request at /field](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-field).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        JSONObject field = new JSONObject()
                .put("name", "Year")
                .put("api-name", "year")
                .put("type", "integer")
                .put("description", "Year of manufacturing")
                .put("storage", "latest-value");
        JSONObject result = slicingDice.createField(field);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "api-name": "year"
}
```

### `JSONObject index(JSONObject data)`
Index data to existing entities or create new entities, if necessary. This method corresponds to a [POST request at /index](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-index).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_WRITE_API_KEY");
        JSONObject indexData = new JSONObject()
                .put("user1@slicingdice.com", new JSONObject()
                        .put("car-model", "Ford Ka")
                        .put("year", 2016))
                .put("user2@slicingdice.com", new JSONObject()
                        .put("car-model", "Honda Fit")
                        .put("year", 2016))
                .put("user3@slicingdice.com", new JSONObject()
                        .put("car-model", "Toyota Corolla")
                        .put("year", 2010)
                        .put("test-drives", new JSONArray()
                            .put(new JSONObject()
                                .put("value", "NY")
                                .put("date", "2016-08-17T13:23:47+00:00"))
                            .put(new JSONObject()
                                .put("value", "NY")
                                .put("date", "2016-08-17T13:23:47+00:00"))
                            .put(new JSONObject()
                                .put("value", "NY")
                                .put("date", "2016-04-05T10:20:30Z"))))
                .put("customer5@mycustomer.com", new JSONObject()
                        .put("car-model", "Ford Ka")
                        .put("year", 2005)
                        .put("test-drives", new JSONObject()
                            .put("value", "NY")
                            .put("date", "2016-08-17T13:23:47+00:00")));
        JSONObject result = slicingDice.index(indexData);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "indexed-entities": 4,
    "indexed-fields": 10,
    "took": 0.023
}
```

### `JSONObject existsEntity(ids)`
Verify which entities exist in a project given a list of entity IDs. This method corresponds to a [POST request at /query/exists/entity](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-query-exists-entity).

#### Request example

```java
import org.json.JSONArray;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        JSONArray ids = new JSONArray()
                .put("user1@slicingdice.com")
                .put("user2@slicingdice.com")
                .put("user3@slicingdice.com");
        JSONObject result = slicingDice.existsEntity(ids);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "exists": [
        "user1@slicingdice.com",
        "user2@slicingdice.com"
    ],
    "not-exists": [
        "user3@slicingdice.com"
    ],
    "took": 0.103
}
```

### `JSONObject countEntityTotal()`
Count the number of indexed entities. This method corresponds to a [GET request at /query/count/entity/total](http://panel.slicingdice.com/docs/#api-details-api-endpoints-get-query-count-entity-total).

#### Request example

```java
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        JSONObject result = slicingDice.countEntityTotal();
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "result": {
        "total": 42
    },
    "took": 0.103
}
```

### `JSONObject countEntity(JSONObject data)`
Count the number of entities attending the given query. This method corresponds to a [POST request at /query/count/entity](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-query-count-entity).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        JSONObject countEntityQuery = new JSONObject()
                .put("users-from-ny-or-ca", new JSONArray()
                    .put(new JSONObject()
                        .put("state", new JSONObject()
                            .put("equals", "NY")))
                    .put("or")
                    .put(new JSONObject()
                        .put("state-origin", new JSONObject()
                            .put("equals", "CA"))))
                .put("users-from-ny", new JSONArray()
                    .put(new JSONObject()
                        .put("state", new JSONObject()
                            .put("equals", "NY"))))
                .put("bypass-cache", false);
        JSONObject result = slicingDice.countEntity(countEntityQuery);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "result": {
        "users-from-ny-or-ca": 175,
        "users-from-ny": 296
    },
    "took": 0.103
}
```

### `JSONObject countEvent(JSONObject data)`
Count the number of occurrences for time-series events attending the given query. This method corresponds to a [POST request at /query/count/event](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-query-count-event).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        JSONObject countEventQuery = new JSONObject()
                .put("users-from-ny-in-jan", new JSONArray()
                    .put(new JSONObject()
                        .put("test-field", new JSONObject()
                            .put("equals", "NY")
                            .put("between", new JSONArray()
                                .put("2016-01-01T00:00:00Z")
                                .put("2016-01-31T00:00:00Z"))
                            .put("minfreq", 2))))
                .put("users-from-ny-in-feb", new JSONArray()
                    .put(new JSONObject()
                        .put("test-field", new JSONObject()
                            .put("equals", "NY")
                            .put("between", new JSONArray()
                                .put("2016-02-01T00:00:00Z")
                                .put("2016-02-28T00:00:00Z")))))
                .put("bypass-cache", false);
        JSONObject result = slicingDice.countEvent(countEventQuery);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "result": {
        "users-from-ny-in-jan": 175,
        "users-from-ny-in-feb": 296
    },
    "took": 0.103
}
```

### `JSONObject topValues(JSONObject data)`
Return the top values for entities attending the given query. This method corresponds to a [POST request at /query/top_values](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-query-top-values).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        JSONObject topValuesQuery = new JSONObject()
                .put("user-gender", new JSONObject()
                        .put("gender", 2))
                .put("operating-systems", new JSONObject()
                        .put("os", 2))
                .put("linux-operating-systems", new JSONObject()
                        .put("os", 3)
                        .put("contains", new JSONArray()
                            .put("linux")
                            .put("unix")));
        JSONObject result = slicingDice.topValues(topValuesQuery);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "result": {
        "user-gender": {
            "gender": [
                {
                    "quantity": 6.0,
                    "value": "male"
                }, {
                    "quantity": 4.0,
                    "value": "female"
                }
            ]
        },
        "operating-systems": {
            "os": [
                {
                    "quantity": 55.0,
                    "value": "windows"
                }, {
                    "quantity": 25.0,
                    "value": "macos"
                }, {
                    "quantity": 12.0,
                    "value": "linux"
                }
            ]
        },
        "linux-operating-systems": {
            "os": [
                {
                    "quantity": 12.0,
                    "value": "linux"
                }, {
                    "quantity": 3.0,
                    "value": "debian-linux"
                }, {
                    "quantity": 2.0,
                    "value": "unix"
                }
            ]
        }
    },
    "took": 0.103
}
```

### `JSONObject aggregation(JSONObject data)`
Return the aggregation of all fields in the given query. This method corresponds to a [POST request at /query/aggregation](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-query-aggregation).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        JSONObject aggregationQuery = new JSONObject()
                .put("query", new JSONArray()
                        .put(new JSONObject()
                            .put("gender", 2))
                        .put(new JSONObject()
                            .put("os", 2)
                            .put("equals", new JSONArray()
                                .put("linux")
                                .put("windows")
                                .put("macos")))
                        .put(new JSONObject()
                            .put("browser", 2)));
        JSONObject result = slicingDice.aggregation(aggregationQuery);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "result": {
        "gender": [
            {
                "quantity": 6,
                "value": "male",
                "os": [
                    {
                        "quantity": 5,
                        "value": "windows",
                        "browser": [
                            {
                                "quantity": 3,
                                "value": "safari"
                            }, {
                                "quantity": 2,
                                "value": "internet explorer"
                            }
                        ]
                    }, {
                        "quantity": 1,
                        "value": "linux",
                        "browser": [
                            {
                                "quantity": 1,
                                "value": "chrome"
                            }
                        ]
                    }
                ]
            }, {
                "quantity": 4,
                "value": "female",
                "os": [
                    {
                        "quantity": 3,
                        "value": "macos",
                        "browser": [
                            {
                                "quantity": 3,
                                "value": "chrome"
                            }
                        ]
                    }, {
                        "quantity": 1,
                        "value": "linux",
                        "browser": [
                            {
                                "quantity": 1,
                                "value": "chrome"
                            }
                        ]
                    }
                ]
            }
        ]
    },
    "took": 0.103
}
```

### `JSONObject getSavedQueries()`
Get all saved queries. This method corresponds to a [GET request at /query/saved](http://panel.slicingdice.com/docs/#api-details-api-endpoints-get-query-saved).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        JSONObject result = slicingDice.getSavedQueries();
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "saved-queries": [
        {
            "name": "users-in-ny-or-from-ca",
            "type": "count/entity",
            "query": [
                {
                    "state": {
                        "equals": "NY"
                    }
                },
                "or",
                {
                    "state-origin": {
                        "equals": "CA"
                    }
                }
            ],
            "cache-period": 100
        }, {
            "name": "users-from-ca",
            "type": "count/entity",
            "query": [
                {
                    "state": {
                        "equals": "NY"
                    }
                }
            ],
            "cache-period": 60
        }
    ],
    "took": 0.103
}
```

### `JSONObject createSavedQuery(JSONObject data)`
Create a saved query at SlicingDice. This method corresponds to a [POST request at /query/saved](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-query-saved).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        JSONObject savedQuery = new JSONObject()
                .put("name", "my-saved-query")
                .put("type", "count/entity")
                .put("query", new JSONArray()
                    .put(new JSONObject()
                        .put("state", new JSONObject()
                            .put("equals", "NY")))
                    .put("or")
                    .put(new JSONObject()
                        .put("state", new JSONObject()
                            .put("equals", "CA"))))
                .put("cache-period", 100);
        JSONObject result = slicingDice.createSavedQuery(savedQuery);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "name": "my-saved-query",
    "type": "count/entity",
    "query": [
        {
            "state": {
                "equals": "NY"
            }
        },
        "or",
        {
            "state-origin": {
                "equals": "CA"
            }
        }
    ],
    "cache-period": 100,
    "took": 0.103
}
```

### `JSONObject updateSavedQuery(String queryName, JSONObject data)`
Update an existing saved query at SlicingDice. This method corresponds to a [PUT request at /query/saved/QUERY_NAME](http://panel.slicingdice.com/docs/#api-details-api-endpoints-put-query-saved-query-name).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        JSONObject newSavedQuery = new JSONObject()
                .put("type", "count/entity")
                .put("query", new JSONArray()
                    .put(new JSONObject()
                        .put("state", new JSONObject()
                            .put("equals", "NY")))
                    .put("or")
                    .put(new JSONObject()
                        .put("state", new JSONObject()
                            .put("equals", "CA"))))
                .put("cache-period", 100);
        JSONObject result = slicingDice.updateSavedQuery("my-saved-query", newSavedQuery);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "name": "my-saved-query",
    "type": "count/entity",
    "query": [
        {
            "state": {
                "equals": "NY"
            }
        },
        "or",
        {
            "state-origin": {
                "equals": "CA"
            }
        }
    ],
    "cache-period": 100,
    "took": 0.103
}
```

### `JSONObject getSavedQuery(String queryName)`
Executed a saved query at SlicingDice. This method corresponds to a [GET request at /query/saved/QUERY_NAME](http://panel.slicingdice.com/docs/#api-details-api-endpoints-get-query-saved-query-name).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        JSONObject result = slicingDice.getSavedQuery("my-saved-query");
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "type": "count/entity",
    "query": [
        {
            "state": {
                "equals": "NY"
            }
        },
        "or",
        {
            "state-origin": {
                "equals": "CA"
            }
        }
    ],
    "result": {
        "my-saved-query": 175
    },
    "took": 0.103
}
```

### `JSONObject deleteSavedQuery(String queryName)`
Delete a saved query at SlicingDice. This method corresponds to a [DELETE request at /query/saved/QUERY_NAME](http://panel.slicingdice.com/docs/#api-details-api-endpoints-delete-query-saved-query-name).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        JSONObject result = slicingDice.deleteSavedQuery("my-saved-query");
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "deleted-query": "my-saved-query",
    "type": "count/entity",
    "query": [
        {
            "state": {
                "equals": "NY"
            }
        },
        "or",
        {
            "state-origin": {
                "equals": "CA"
            }
        }
    ],
    "took": 0.103
}
```

### `JSONObject result(JSONObject data)`
Retrieve indexed values for entities attending the given query. This method corresponds to a [POST request at /data_extraction/result](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-data-extraction-result).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        JSONObject resultQuery = new JSONObject()
                .put("query", new JSONArray()
                        .put(new JSONObject()
                            .put("users-from-ny", new JSONObject()
                                .put("equals", "NY")))
                        .put("or")
                        .put(new JSONObject()
                            .put("users-from-ca", new JSONObject()
                                .put("equals", "CA"))))
                .put("fields", new JSONArray()
                    .put("name")
                    .put("year"))
                .put("limit", 2);
        JSONObject result = slicingDice.result(resultQuery);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "data": {
        "user1@slicingdice.com": {
            "name": "John",
            "year": 2016
        },
        "user2@slicingdice.com": {
            "name": "Mary",
            "year": 2005
        }
    },
    "took": 0.103
}
```

### `JSONObject score(JSONObject data)`
Retrieve indexed values as well as their relevance for entities attending the given query. This method corresponds to a [POST request at /data_extraction/score](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-data-extraction-score).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.simbiose.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        JSONObject scoreQuery = new JSONObject()
                .put("query", new JSONArray()
                        .put(new JSONObject()
                            .put("users-from-ny", new JSONObject()
                                .put("equals", "NY")))
                        .put("or")
                        .put(new JSONObject()
                            .put("users-from-ca", new JSONObject()
                                .put("equals", "CA"))))
                .put("fields", new JSONArray()
                    .put("name")
                    .put("year"))
                .put("limit", 2);
        JSONObject result = slicingDice.score(scoreQuery);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "data": {
        "user1@slicingdice.com": {
            "name": "John",
            "year": 2016,
            "score": 2
        },
        "user2@slicingdice.com": {
            "name": "Mary",
            "year": 2005,
            "score": 1
        }
    },
    "took": 0.103
}
```

## License

[MIT](https://opensource.org/licenses/MIT)
