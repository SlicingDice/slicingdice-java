# SlicingDice Official Java Client (v2.0.2)
### Build Status: [![CircleCI](https://circleci.com/gh/SlicingDice/slicingdice-java.svg?style=svg)](https://circleci.com/gh/SlicingDice/slicingdice-java)

Official Java client for [SlicingDice](http://www.slicingdice.com/), Data Warehouse and Analytics Database as a Service.  

[SlicingDice](http://www.slicingdice.com/) is a serverless, API-based, easy-to-use and really cost-effective alternative to Amazon Redshift and Google BigQuery.

## Documentation

If you are new to SlicingDice, check our [quickstart guide](https://docs.slicingdice.com/docs/quickstart-guide) and learn to use it in 15 minutes.

Please refer to the [SlicingDice official documentation](https://docs.slicingdice.com/) for more information on [how to create a database](https://docs.slicingdice.com/docs/how-to-create-a-database), [how to insert data](https://docs.slicingdice.com/docs/how-to-insert-data), [how to make queries](https://docs.slicingdice.com/docs/how-to-make-queries), [how to create columns](https://docs.slicingdice.com/docs/how-to-create-columns), [SlicingDice restrictions](https://docs.slicingdice.com/docs/current-restrictions) and [API details](https://docs.slicingdice.com/docs/api-details).

## Tests and Examples

Whether you want to test the client installation or simply check more examples on how the client works, take a look at [tests and examples directory](src/test/java/com/slicingdice/jslicer/).

## Requirements

In order to import SlicingDice's client on your application, you'll need
to setup the following dependencies:

* [json 20180130](https://github.com/stleary/JSON-java)
* [async-http-client 2.4.2](https://github.com/AsyncHttpClient/async-http-client)


In case you're using [Maven](https://maven.apache.org/) to manage packages,
 add this to your pom.xml:

```xml
<dependency>
    <groupId>org.asynchttpclient</groupId>
    <artifactId>async-http-client</artifactId>
    <version>2.4.2</version>
</dependency>
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20180130</version>
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
    <version>2.0.2</version>
</dependency>
```

## Usage

The following code snippet is an example of how to add and query data
using the SlicingDice JAVA client. We entry data informing
`user1@slicingdice.com` has age 22 and then query the database for
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
        SlicingDice client = new SlicingDice("API_KEY");

        // Creating a column
        // Inserting data
        JSONObject insertData = new JSONObject()
                .put("user1@slicingdice.com",
                        new JSONObject()
                                .put("age", 22))
                .put("auto-create", new JSONArray()
                        .put("dimension")
                        .put("column"));
        System.out.println(client.insert(insertData).get().getResponseBody());

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
        System.out.println(client.countEntity(queryData).get().getResponseBody());
    }
}
```

## Reference

`SlicingDice` encapsulates logic for sending requests to the API. Its methods are thin layers around the [API endpoints](https://docs.slicingdice.com/reference), so their parameters and return values are JSON-like `JSONObject` objects with the same syntax as the [API endpoints](https://docs.slicingdice.com/reference).

### Constructors

`SlicingDice(String masterKey)`
* `masterKey (String)` - [API key](https://docs.slicingdice.com/docs/api-keys) to authenticate requests with the SlicingDice API  Master Key.

`SlicingDice(String masterKey, String customKey, String writeKey, String readKey)`
* `masterKey (String)` - [API key](https://docs.slicingdice.com/docs/api-keys) to authenticate requests with the SlicingDice API Master Key.
* `customKey (String)` - [API key](https://docs.slicingdice.com/docs/api-keys) to authenticate requests with the SlicingDice API Custom Key.
* `writeKey (String)` - [API key](https://docs.slicingdice.com/docs/api-keys) to authenticate requests with the SlicingDice API Write Key.
* `readKey (String)` - [API key](https://docs.slicingdice.com/docs/api-keys) to authenticate requests with the SlicingDice API Read Key.

`SlicingDice(String masterKey, String customKey, String writeKey, String readKey, int timeout)`
* `masterKey (String)` - [API key](https://docs.slicingdice.com/docs/api-keys) to authenticate requests with the SlicingDice API Master Key.
* `customKey (String)` - [API key](https://docs.slicingdice.com/docs/api-keys) to authenticate requests with the SlicingDice API Custom Key.
* `writeKey (String)` - [API key](https://docs.slicingdice.com/docs/api-keys) to authenticate requests with the SlicingDice API Write Key.
* `readKey (String)` - [API key](https://docs.slicingdice.com/docs/api-keys) to authenticate requests with the SlicingDice API Read Key.
* `timeout (int)` - Amount of time, in seconds, to wait for results for each request, if not defined timeout will have 60 seconds.

### `Future<Response> getDatabase()`
Get information about current database. This method corresponds to a `GET` request at `/database`.

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        final Future<Response> result = slicingDice.getDatabase();
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
    }
}

```

#### Output example

```json
{
    "name": "Database 1",
    "description": "My first database",
    "dimensions": [
    	"default",
        "users"
    ],
    "updated-at": "2017-05-19T14:27:47.417415",
    "created-at": "2017-05-12T02:23:34.231418"
}
```

### `Future<Response> getColumns()`
Get all created columns, both active and inactive ones. This method corresponds to a [GET request at /column](https://docs.slicingdice.com/docs/how-to-list-edit-or-delete-columns).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        final Future<Response> result = slicingDice.getColumns();
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> createColumn(JSONObject data)`
Create a new column. This method corresponds to a [POST request at /column](https://docs.slicingdice.com/docs/how-to-create-columns#section-creating-columns-using-column-endpoint).

#### Request example

```java
import java.io.IOException;
import org.json.JSONObject;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        final JSONObject column = new JSONObject()
                .put("name", "Year")
                .put("api-name", "year")
                .put("type", "integer")
                .put("description", "Year of manufacturing")
                .put("storage", "latest-value");
        final Future<Response> result = slicingDice.createColumn(column);
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> insert(JSONObject data)`
Insert data to existing entities or create new entities, if necessary. This method corresponds to a [POST request at /insert](https://docs.slicingdice.com/docs/how-to-insert-data).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_WRITE_API_KEY");
        final SONObject insertData = new JSONObject()
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
                .put("auto-create", new JSONArray()
                        .put("dimension")
                        .put("column"));
        final Future<Response> result = slicingDice.insert(insertData);
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> existsEntity(ids[, dimension])`
Verify which entities exist in a dimension (uses `default` dimension if not provided) given a list of entity IDs. This method corresponds to a [POST request at /query/exists/entity](https://docs.slicingdice.com/docs/exists).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        final JSONArray ids = new JSONArray()
                .put("user1@slicingdice.com")
                .put("user2@slicingdice.com")
                .put("user3@slicingdice.com");
        Future<Response> result = slicingDice.existsEntity(ids);
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> countEntityTotal()`
Count the number of inserted entities in the whole database. This method corresponds to a [POST request at /query/count/entity/total](https://docs.slicingdice.com/docs/total).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        final Future<Response> result = slicingDice.countEntityTotal();
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> countEntityTotal(Collection<String> dimensions)`
Count the total number of inserted entities in the given dimensions. This method corresponds to a [POST request at /query/count/entity/total](https://docs.slicingdice.com/docs/total#section-counting-specific-tables).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;
import java.util.ArrayList;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");        
        final Set<String> dimensions = Collections.singleton("default");
        final Future<Response> result = slicingDice.countEntityTotal(dimensions);
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> countEntity(JSONArray data)`
Count the number of entities matching the given query. This method corresponds to a [POST request at /query/count/entity](https://docs.slicingdice.com/docs/count-entities).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        final JSONArray countEntityQuery = new JSONArray()
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
        final Future<Response> result = slicingDice.countEntity(countEntityQuery);
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> countEntity(JSONObject data)`
Count the number of entities matching the given query. This method corresponds to a [POST request at /query/count/entity](https://docs.slicingdice.com/docs/count-entities).

#### Request example

```java
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        final JSONObject countEntityQuery = new JSONObject()
                        .put("query-name", "corolla-or-fit")
                        .put("query", new JSONArray()
                                .put(new JSONObject()
                                        .put("car-model", new JSONObject()
                                                .put("equals", "toyota corolla")))
                                .put("or")
                                .put(new JSONObject()
                                        .put("car-model", new JSONObject()
                                                .put("equals", "honda fit"))))
                        .put("bypass-cache", false);
        final Future<Response> result = slicingDice.countEntity(countEntityQuery);
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
    }
}
```

#### Output example

```json
{
   "result":{
      "corolla-or-fit":2
   },
   "took":0.018,
   "status":"success"
}
```

### `Future<Response> countEvent(JSONArray data)`
Count the number of occurrences for time-series events matching the given query. This method corresponds to a [POST request at /query/count/event](https://docs.slicingdice.com/docs/count-events).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        final JSONArray countEventQuery = new JSONArray()
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

        final Future<Response> result = slicingDice.countEvent(countEventQuery);
        final System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> countEvent(JSONObject data)`
Count the number of occurrences for time-series events matching the given query. This method corresponds to a [POST request at /query/count/event](https://docs.slicingdice.com/docs/count-events).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        final JSONObject countEventQuery = new JSONObject()
                        .put("query-name", "test-drives-in-ny")
                        .put("query", new JSONArray()
                                .put(new JSONObject()
                                        .put("test-drives", new JSONObject()
                                                .put("equals", "NY")
                                                .put("between", new JSONArray()
                                                        .put("2016-08-16T00:00:00Z")
                                                        .put("2016-08-18T00:00:00Z")))))
                        .put("bypass-cache", false);
        
        final Future<Response> result = slicingDice.countEvent(countEventQuery);
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
    }
}
```

#### Output example

```json
{
   "result":{
      "test-drives-in-ny":3
   },
   "took":0.063,
   "status":"success"
}
```

### `Future<Response> topValues(JSONObject data)`
Return the top values for entities matching the given query. This method corresponds to a [POST request at /query/top_values](https://docs.slicingdice.com/docs/top-values).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        final JSONObject topValuesQuery = new JSONObject()
                .put("car-year", new JSONObject()
                        .put("year", 2))
                .put("car models", new JSONObject()
                        .put("car-model", 3));
        final Future<Response> result = slicingDice.topValues(topValuesQuery);
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> aggregation(JSONObject data)`
Return the aggregation of all columns in the given query. This method corresponds to a [POST request at /query/aggregation](https://docs.slicingdice.com/docs/aggregations).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        final JSONObject aggregationQuery = new JSONObject()
                .put("query", new JSONArray()
                        .put(new JSONObject()
                                .put("year", 2))
                        .put(new JSONObject()
                                .put("car-model", 2)
                                .put("equals", new JSONArray()
                                        .put("honda fit")
                                        .put("toyota corolla"))));
        final Future<Response> result = slicingDice.aggregation(aggregationQuery);
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> getSavedQueries()`
Get all saved queries. This method corresponds to a [GET request at /query/saved](https://docs.slicingdice.com/docs/saved-queries).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        final Future<Response> result = slicingDice.getSavedQueries();
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> createSavedQuery(JSONObject data)`
Create a saved query at SlicingDice. This method corresponds to a [POST request at /query/saved](https://docs.slicingdice.com/docs/saved-queries).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        final JSONObject savedQuery = new JSONObject()
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
        final Future<Response> result = slicingDice.createSavedQuery(savedQuery);
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> updateSavedQuery(String queryName, JSONObject data)`
Update an existing saved query at SlicingDice. This method corresponds to a [PUT request at /query/saved/QUERY_NAME](https://docs.slicingdice.com/docs/saved-queries).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
       final  SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        final JSONObject newSavedQuery = new JSONObject()
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
        final Future<Response> result = slicingDice.updateSavedQuery("my-saved-query", newSavedQuery);
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> getSavedQuery(String queryName)`
Executed a saved query at SlicingDice. This method corresponds to a [GET request at /query/saved/QUERY_NAME](https://docs.slicingdice.com/docs/saved-queries).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        final Future<Response> result = slicingDice.getSavedQuery("my-saved-query");
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> deleteSavedQuery(String queryName)`
Delete a saved query at SlicingDice. This method corresponds to a [DELETE request at /query/saved/QUERY_NAME](https://docs.slicingdice.com/docs/saved-queries).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;

public class Example {

    public static void main(String[] args) InterruptedException, ExecutionException, throws IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_API_KEY");
        final Future<Response> result = slicingDice.deleteSavedQuery("my-saved-query");
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> result(JSONObject data)`
Retrieve inserted values for entities matching the given query. This method corresponds to a [POST request at /data_extraction/result](https://docs.slicingdice.com/docs/result-extraction).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        final JSONObject resultQuery = new JSONObject()
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
        final Future<Response> result = slicingDice.result(resultQuery);
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
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

### `Future<Response> score(JSONObject data)`
Retrieve inserted values as well as their relevance for entities matching the given query. This method corresponds to a [POST request at /data_extraction/score](https://docs.slicingdice.com/docs/score-extraction).

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        final JSONObject scoreQuery = new JSONObject()
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
        final Future<Response> result = slicingDice.score(scoreQuery);
        System.out.println(result.get().getResponseBody());
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

## `Future<Response> sql(String query)`
Retrieve inserted values using a SQL syntax. This method corresponds to a POST request at /query/sql.

#### Request example

```java
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.asynchttpclient.Response;

public class Example {

    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        final SlicingDice slicingDice = new SlicingDice("MASTER_OR_READ_API_KEY");
        final Future<Response> result = slicingDice.sql("SELECT COUNT(*) FROM default WHERE age BETWEEN 0 AND 49");
        System.out.println(result.get().getResponseBody());

        slicingDice.close();
    }
}
```

#### Output example

```json
{
   "took":0.063,
   "result":[
       {"COUNT": 3}
   ],
   "count":1,
   "status":"success"
}
```

## `close()`
Use this method when you're done with SlicingDice client, this method will properly close http threads.

## Using callback instead of Future
The programmer can define callbacks instead of using returned Futures, to create a callback you'll need to extend the class HandlerResponse and implement the methods `onError` and `onSuccess`. See the example below.

```java
public static class MyHandler extends HandlerResponse {
    @Override
    public void onSuccess(final JSONObject data) throws Exception {
        System.out.println(data.toString());
    }

    @Override
    public void onError(final JSONObject data) throws Exception {
        System.out.println(data.toString());
    }
}
```

And you can use the handler this way:
```java
slicingDice.sql("SELECT COUNT(*) FROM default WHERE age BETWEEN 0 AND 49", new MyHandler());
```

## License

[MIT](https://opensource.org/licenses/MIT)
