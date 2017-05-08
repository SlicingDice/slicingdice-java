# SlicingDice Official Java Client (v1.0)
### Build Status: [![CircleCI](https://circleci.com/gh/SlicingDice/slicingdice-java.svg?style=svg)](https://circleci.com/gh/SlicingDice/slicingdice-java)

Official Java client for [SlicingDice](http://www.slicingdice.com/), Data Warehouse and Analytics Database as a Service.  

[SlicingDice](http://www.slicingdice.com/) is a serverless, API-based, easy-to-use and really cost-effective alternative to Amazon Redshift and Google BigQuery.

## Documentation

If you are new to SlicingDice, check our [quickstart guide](http://panel.slicingdice.com/docs/#quickstart-guide) and learn to use it in 15 minutes.

Please refer to the [SlicingDice official documentation](http://panel.slicingdice.com/docs/) for more information on [analytics databases](http://panel.slicingdice.com/docs/#analytics-concepts), [data modeling](http://panel.slicingdice.com/docs/#data-modeling), [data insertion](http://panel.slicingdice.com/docs/#data-insertion), [querying](http://panel.slicingdice.com/docs/#data-querying), [limitations](http://panel.slicingdice.com/docs/#current-slicingdice-limitations) and [API details](http://panel.slicingdice.com/docs/#api-details).

## Tests and Examples

Whether you want to test the client installation or simply check more examples on how the client works, take a look at [tests and examples directory](src/test/java/com/slicingdice/jslicer/).

## Requirements

In order to import SlicingDice's client on your application, you'll need
to setup the following dependencies:

* [gson 2.3.2](https://github.com/google/gson)
* [okhttp 3.2.0](http://square.github.io/okhttp/)


In case you're using [Maven](https://maven.apache.org/) to manage packages,
 add this to your pom.xml:

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

## Installing

[Click here](https://github.com/SlicingDice/slicingdice-java/releases) to
download our Java client as a `jar` file. After downloading it,
you only need to import the `jar` into your project path.

Additionally, our Java client can be installed via [Maven](https://maven.apache.org/)
by adding the following configurations in your pom.xml. Mind that these snippets
go on different sections of your pom, as the first one sets the repository
from which to download the package and the second one sets the package itself.

```xml
<repository>
    <id>slicingdice-clients</id>
    <url>https://packagecloud.io/slicingdice/clients/maven2</url>
    <releases>
        <enabled>true</enabled>
    </releases>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```

```xml
<dependency>
    <groupId>com.slicingdice</groupId>
    <artifactId>jslicer</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage

The following code snippet is an example of how to add and query data
using the SlicingDice JAVA client. We entry data informing
'user1@slicingdice.com' has age 22 and then query the database for
the number of users with age between 20 and 40 years old.
If this is the first record ever entered into the system,
 the answer should be 1.

```java
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.slicingdice.jslicer.SlicingDice;

public class Example {
    public static void main( String[] args ) throws IOException
    {
        SlicingDice client = new SlicingDice("API_KEY", false);

        // Creating a column
        // Inserting data
        JSONObject insertData = new JSONObject()
                .put("user1@slicingdice.com",
                        new JSONObject()
                                .put("age", 22))
                .put("auto-create-columns", true);
        System.out.println(client.insert(insertData));

        // Querying data
        JSONObject queryData = new JSONObject()
                .put("query-name", "users-between-20-and-40")
                .put("query",
                        new JSONArray()
                                .put(new JSONObject()
                                        .put("age",
                                                new JSONObject()
                                                        .put("range",
                                                                new JSONArray()
                                                                        .put(20)
                                                                        .put(40)))));
        System.out.println(client.countEntity(queryData));
    }
}
```

## Reference

`SlicingDice` encapsulates logic for sending requests to the API. Its methods are thin layers around the [API endpoints](http://panel.slicingdice.com/docs/#api-details-api-endpoints), so their parameters and return values are JSON-like `JSONObject` objects with the same syntax as the [API endpoints](http://panel.slicingdice.com/docs/#api-details-api-endpoints)

### Attributes

* `sdAddress (String)` - [Connection endpoint](http://panel.slicingdice.com/docs/#api-details-api-connection-connection-endpoints) to use when generating requests to SlicingDice.

### Constructors

`SlicingDice(String masterKey, boolean usesTestEndPoint)`
* `masterKey (String)` - [API key](http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys) to authenticate requests with the SlicingDice API  Master Key.
* `usesTestEndPoint (boolean)` - If false the client will send requests to production end-point, otherwise to tests end-point.

`SlicingDice(String masterKey, String customKey, String writeKey, String readKey, boolean usesTestEndPoint)`
* `masterKey (String)` - [API key](http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys) to authenticate requests with the SlicingDice API Master Key.
* `customKey (String)` - [API key](http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys) to authenticate requests with the SlicingDice API Custom Key.
* `writeKey (String)` - [API key](http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys) to authenticate requests with the SlicingDice API Write Key.
* `readKey (String)` - [API key](http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys) to authenticate requests with the SlicingDice API Read Key.
* `usesTestEndPoint (boolean)` - If false the client will send requests to production end-point, otherwise to tests end-point.

`SlicingDice(String masterKey, String customKey, String writeKey, String readKey, int timeout, boolean usesTestEndPoint)`
* `masterKey (String)` - [API key](http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys) to authenticate requests with the SlicingDice API Master Key.
* `customKey (String)` - [API key](http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys) to authenticate requests with the SlicingDice API Custom Key.
* `writeKey (String)` - [API key](http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys) to authenticate requests with the SlicingDice API Write Key.
* `readKey (String)` - [API key](http://panel.slicingdice.com/docs/#api-details-api-connection-api-keys) to authenticate requests with the SlicingDice API Read Key.
* `timeout (int)` - Amount of time, in seconds, to wait for results for each request.
* `usesTestEndPoint (boolean)` - If false the client will send requests to production end-point, otherwise to tests end-point.

### `JSONObject getDatabase()`
Get information about current database. This method corresponds to a [GET request at /database](http://panel.slicingdice.com/docs/#api-details-api-endpoints-get-database).

#### Request example

```java
import java.io.IOException;
import org.json.JSONObject;
import com.slicingdice.jslicer.SlicingDice;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY", false);
        JSONObject result = slicingDice.getDatabase();
        System.out.println(result.toString());
    }
}

```

#### Output example

```json
{
    "active": [
        {
            "name": "Database 1",
            "description": "My first database",
            "data-expiration": 30,
            "created-at": "2016-04-05T10:20:30Z"
        }
    ],
    "inactive": [
        {
            "name": "Database 2",
            "description": "My second database",
            "data-expiration": 90,
            "created-at": "2016-04-05T10:20:30Z"
        }
    ]
}
```

### `JSONObject getColumns()`
Get all created columns, both active and inactive ones. This method corresponds to a [GET request at /column](http://panel.slicingdice.com/docs/#api-details-api-endpoints-get-column).

#### Request example

```java
import java.io.IOException;
import org.json.JSONObject;
import com.slicingdice.jslicer.SlicingDice;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY", false);
        JSONObject result = slicingDice.getColumns();
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

### `JSONObject createColumn(JSONObject data)`
Create a new column. This method corresponds to a [POST request at /column](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-column).

#### Request example

```java
import java.io.IOException;
import org.json.JSONObject;
import com.slicingdice.jslicer.SlicingDice;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY", false);
        JSONObject column = new JSONObject()
                .put("name", "Year")
                .put("api-name", "year")
                .put("type", "integer")
                .put("description", "Year of manufacturing")
                .put("storage", "latest-value");
        JSONObject result = slicingDice.createColumn(column);
        System.out.println(result.toString());
    }
}

```

#### Output example

```json
{
    "status": "success",
    "api-name": "year",
    "took":0.082
}
```

### `JSONObject insert(JSONObject data)`
Insert data to existing entities or create new entities, if necessary. This method corresponds to a [POST request at /insert](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-insert).

#### Request example

```java
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.slicingdice.jslicer.SlicingDice;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_WRITE_API_KEY", false);
        JSONObject insertData = new JSONObject()
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
                                .put("date", "2016-08-17T13:23:47+00:00")))
                .put("auto-create-columns", true);
        JSONObject result = slicingDice.insert(insertData);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
    "status": "success",
    "inserted-entities": 4,
    "inserted-columns": 10,
    "took": 0.023
}
```

### `JSONObject existsEntity(ids)`
Verify which entities exist in a database given a list of entity IDs. This method corresponds to a [POST request at /query/exists/entity](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-query-exists-entity).

#### Request example

```java
import com.slicingdice.jslicer.SlicingDice;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY", false);
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
Count the number of inserted entities. This method corresponds to a [GET request at /query/count/entity/total](http://panel.slicingdice.com/docs/#api-details-api-endpoints-get-query-count-entity-total).

#### Request example

```java
import com.slicingdice.jslicer.SlicingDice;
import java.io.IOException;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY", false);
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
Count the number of entities matching the given query. This method corresponds to a [POST request at /query/count/entity](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-query-count-entity).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import com.slicingdice.jslicer;

import java.io.IOException;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY", false);
        JSONArray countEntityQuery = new JSONArray()
                        .put(new JSONObject()
                                .put("query-name", "corolla-or-fit")
                                .put("query", new JSONArray()
                                        .put(new JSONObject()
                                                .put("car-model", new JSONObject()
                                                        .put("equals", "toyota corolla")))
                                        .put("or")
                                        .put(new JSONObject()
                                                .put("car-model", new JSONObject()
                                                        .put("equals", "honda fit"))))
                                .put("bypass-cache", false))
                        .put(new JSONObject()
                                .put("query-name", "ford-ka")
                                .put("query", new JSONArray()
                                        .put(new JSONObject()
                                                .put("car-model", new JSONObject()
                                                        .put("equals", "ford ka"))))
                                .put("bypass-cache", false));
                JSONObject result = slicingDice.countEntity(countEntityQuery);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
   "result":{
      "ford-ka":2,
      "corolla-or-fit":2
   },
   "took":0.083,
   "status":"success"
}
```

### `JSONObject countEvent(JSONObject data)`
Count the number of occurrences for time-series events matching the given query. This method corresponds to a [POST request at /query/count/event](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-query-count-event).

#### Request example

```java
import com.slicingdice.jslicer.SlicingDice;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY", false);
        JSONArray countEventQuery = new JSONArray()
                        .put(new JSONObject()
                                .put("query-name", "test-drives-in-ny")
                                .put("query", new JSONArray()
                                        .put(new JSONObject()
                                                .put("test-drives", new JSONObject()
                                                        .put("equals", "NY")
                                                        .put("between", new JSONArray()
                                                                .put("2016-08-16T00:00:00Z")
                                                                .put("2016-08-18T00:00:00Z")))))
                                .put("bypass-cache", false))
                        .put(new JSONObject()
                                .put("query-name", "test-drives-in-ca")
                                .put("query", new JSONArray()
                                        .put(new JSONObject()
                                                .put("test-drives", new JSONObject()
                                                        .put("equals", "CA")
                                                        .put("between", new JSONArray()
                                                                .put("2016-04-04T00:00:00Z")
                                                                .put("2016-04-06T00:00:00Z")))))
                                .put("bypass-cache", false));

                JSONObject result = slicingDice.countEvent(countEventQuery);
                System.out.println(result.toString());
            }
}
```

#### Output example

```json
{
   "result":{
      "test-drives-in-ny":3,
      "test-drives-in-ca":0
   },
   "took":0.063,
   "status":"success"
}
```

### `JSONObject topValues(JSONObject data)`
Return the top values for entities matching the given query. This method corresponds to a [POST request at /query/top_values](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-query-top-values).

#### Request example

```java
import com.slicingdice.jslicer.SlicingDice;
import java.io.IOException;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY", false);
        JSONObject topValuesQuery = new JSONObject()
                .put("car-year", new JSONObject()
                        .put("year", 2))
                .put("car models", new JSONObject()
                        .put("car-model", 3));
        JSONObject result = slicingDice.topValues(topValuesQuery);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
   "result":{
      "car models":{
         "car-model":[
            {
               "quantity":2,
               "value":"ford ka"
            },
            {
               "quantity":1,
               "value":"honda fit"
            },
            {
               "quantity":1,
               "value":"toyota corolla"
            }
         ]
      },
      "car-year":{
         "year":[
            {
               "quantity":2,
               "value":"2016"
            },
            {
               "quantity":1,
               "value":"2010"
            }
         ]
      }
   },
   "took":0.034,
   "status":"success"
}
```

### `JSONObject aggregation(JSONObject data)`
Return the aggregation of all columns in the given query. This method corresponds to a [POST request at /query/aggregation](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-query-aggregation).

#### Request example

```java
import com.slicingdice.jslicer.SlicingDice;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY", false);
        JSONObject aggregationQuery = new JSONObject()
                .put("query", new JSONArray()
                        .put(new JSONObject()
                                .put("year", 2))
                        .put(new JSONObject()
                                .put("car-model", 2)
                                .put("equals", new JSONArray()
                                        .put("honda fit")
                                        .put("toyota corolla"))));
        JSONObject result = slicingDice.aggregation(aggregationQuery);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
   "result":{
      "year":[
         {
            "quantity":2,
            "value":"2016",
            "car-model":[
               {
                  "quantity":1,
                  "value":"honda fit"
               }
            ]
         },
         {
            "quantity":1,
            "value":"2005"
         }
      ]
   },
   "took":0.079,
   "status":"success"
}
```

### `JSONObject getSavedQueries()`
Get all saved queries. This method corresponds to a [GET request at /query/saved](http://panel.slicingdice.com/docs/#api-details-api-endpoints-get-query-saved).

#### Request example

```java
import com.slicingdice.jslicer.SlicingDice;
import java.io.IOException;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY", false);
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
import com.slicingdice.jslicer.SlicingDice;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY", false);
        JSONObject savedQuery = new JSONObject()
                .put("name", "my-saved-query")
                .put("type", "count/entity")
                .put("query", new JSONArray()
                        .put(new JSONObject()
                                .put("car-model", new JSONObject()
                                        .put("equals", "honda fit")))
                        .put("or")
                        .put(new JSONObject()
                                .put("car-model", new JSONObject()
                                        .put("equals", "toyota corolla"))))
                .put("cache-period", 100);
        JSONObject result = slicingDice.createSavedQuery(savedQuery);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
   "took":0.053,
   "query":[
      {
         "car-model":{
            "equals":"honda fit"
         }
      },
      "or",
      {
         "car-model":{
            "equals":"toyota corolla"
         }
      }
   ],
   "name":"my-saved-query",
   "type":"count/entity",
   "cache-period":100,
   "status":"success"
}
```

### `JSONObject updateSavedQuery(String queryName, JSONObject data)`
Update an existing saved query at SlicingDice. This method corresponds to a [PUT request at /query/saved/QUERY_NAME](http://panel.slicingdice.com/docs/#api-details-api-endpoints-put-query-saved-query-name).

#### Request example

```java
import com.slicingdice.jslicer.SlicingDice;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY", false);
        JSONObject newSavedQuery = new JSONObject()
                .put("type", "count/entity")
                .put("query", new JSONArray()
                        .put(new JSONObject()
                                .put("car-model", new JSONObject()
                                        .put("equals", "honda fit")))
                        .put("or")
                        .put(new JSONObject()
                                .put("car-model", new JSONObject()
                                        .put("equals", "toyota corolla"))))
                .put("cache-period", 100);
        JSONObject result = slicingDice.updateSavedQuery("my-saved-query", newSavedQuery);
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
   "took":0.037,
   "query":[
      {
         "car-model":{
            "equals":"honda fit"
         }
      },
      "or",
      {
         "car-model":{
            "equals":"toyota corolla"
         }
      }
   ],
   "type":"count/entity",
   "cache-period":100,
   "status":"success"
}
```

### `JSONObject getSavedQuery(String queryName)`
Executed a saved query at SlicingDice. This method corresponds to a [GET request at /query/saved/QUERY_NAME](http://panel.slicingdice.com/docs/#api-details-api-endpoints-get-query-saved-query-name).

#### Request example

```java
import com.slicingdice.jslicer.SlicingDice;
import java.io.IOException;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY", false);
        JSONObject result = slicingDice.getSavedQuery("my-saved-query");
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
   "result":{
      "query":2
   },
   "took":0.035,
   "query":[
      {
         "car-model":{
            "equals":"honda fit"
         }
      },
      "or",
      {
         "car-model":{
            "equals":"toyota corolla"
         }
      }
   ],
   "type":"count/entity",
   "status":"success"
}
```

### `JSONObject deleteSavedQuery(String queryName)`
Delete a saved query at SlicingDice. This method corresponds to a [DELETE request at /query/saved/QUERY_NAME](http://panel.slicingdice.com/docs/#api-details-api-endpoints-delete-query-saved-query-name).

#### Request example

```java
import com.slicingdice.jslicer.SlicingDice;
import java.io.IOException;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY", false);
        JSONObject result = slicingDice.deleteSavedQuery("my-saved-query");
        System.out.println(result.toString());
    }
}
```

#### Output example

```json
{
   "took":0.029,
   "query":[
      {
         "car-model":{
            "equals":"honda fit"
         }
      },
      "or",
      {
         "car-model":{
            "equals":"toyota corolla"
         }
      }
   ],
   "type":"count/entity",
   "cache-period":100,
   "status":"success",
   "deleted-query":"my-saved-query"
}
```

### `JSONObject result(JSONObject data)`
Retrieve inserted values for entities matching the given query. This method corresponds to a [POST request at /data_extraction/result](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-data-extraction-result).

#### Request example

```java
import com.slicingdice.jslicer.SlicingDice;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY", false);
        JSONObject resultQuery = new JSONObject()
                .put("query", new JSONArray()
                        .put(new JSONObject()
                                .put("car-model", new JSONObject()
                                        .put("equals", "ford ka")))
                        .put("or")
                        .put(new JSONObject()
                                .put("car-model", new JSONObject()
                                        .put("equals", "honda fit"))))
                .put("columns", new JSONArray()
                        .put("car-model")
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
   "took":0.113,
   "next-page":null,
   "data":{
      "customer5@mycustomer.com":{
         "year":"2005",
         "car-model":"ford ka"
      },
      "user1@slicingdice.com":{
         "year":"2016",
         "car-model":"ford ka"
      }
   },
   "page":1,
   "status":"success"
}
```

### `JSONObject score(JSONObject data)`
Retrieve inserted values as well as their relevance for entities matching the given query. This method corresponds to a [POST request at /data_extraction/score](http://panel.slicingdice.com/docs/#api-details-api-endpoints-post-data-extraction-score).

#### Request example

```java
import com.slicingdice.jslicer.SlicingDice;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws IOException {
        SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY", false);
        JSONObject scoreQuery = new JSONObject()
                .put("query", new JSONArray()
                        .put(new JSONObject()
                                .put("car-model", new JSONObject()
                                        .put("equals", "toyota corolla")))
                        .put("or")
                        .put(new JSONObject()
                                .put("car-model", new JSONObject()
                                        .put("equals", "honda fit"))))
                .put("columns", new JSONArray()
                        .put("car-model")
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
   "took":0.063,
   "next-page":null,
   "data":{
      "user3@slicingdice.com":{
         "score":1,
         "year":"2010",
         "car-model":"toyota corolla"
      },
      "user2@slicingdice.com":{
         "score":1,
         "year":"2016",
         "car-model":"honda fit"
      }
   },
   "page":1,
   "status":"success"
}
```

## License

[MIT](https://opensource.org/licenses/MIT)
