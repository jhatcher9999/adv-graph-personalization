# Graph Data Loader

### Using DataStax DSE Driver

This project demonstrates reading data from a text file and writing that data into a DSE Graph database using the Fluent API in the DataStax Java DSE Driver.

It uses data from the following Kaggle dataset:
https://www.kaggle.com/c/acquire-valued-shoppers-challenge

It uses the following very simple Graph schema:
```groovy

//Property Keys - product
schema.propertyKey("chain_id").Int().create()
schema.propertyKey("company_id").Int().create()
schema.propertyKey("brand_id").Int().create()
schema.propertyKey("product_size").Double().create()
schema.propertyKey("product_measure").Text().create()
schema.propertyKey("category_id").Int().create()
schema.propertyKey("department_id").Int().create()

//Property Keys - customer
schema.propertyKey("customer_id").Int().create()

//Property Keys - purchases
schema.propertyKey("date").Timestamp().create()
schema.propertyKey("quantity").Int().create()
schema.propertyKey("amount").Double().create()

schema
    .vertexLabel("product")
    .partitionKey("chain_id", "company_id", "brand_id")
    .clusteringKey("product_size", "product_measure")
    .properties(
        "category_id",
        "department_id"
    )
    .create()

schema.vertexLabel("customer")
    .partitionKey("customer_id")
    .create()

schema.edgeLabel("purchases")
    .multiple()
    .properties(
      "date",
      "quantity",
      "amount"
      )
    .connection("customer", "product")
    .create()
```

It reads data from the text file, loads that data into a POJO, then uses a Fluent API Gremlin traversal to load the data into the Graph.

To execute the project:

- Build the project by running `mvn clean compile`
- Create a jar by running `mvn package`.  This will create a jar in the target directory.
- Execute the jar by running `java -jar /path/to/jar/adv-graph-personalization-data-loader-1.0-SNAPSHOT-jar-with-dependencies.jar /path/to/transactions.csv 127.0.0.1` where `127.0.0.1` represents the address of a node in the DSE cluster

In my experience on a 3-node cluster with very modest hardware running DSE 5.1.6, it will load around 200 records per second.

Some ideas for future enhancements:

- Make the program multi-threaded to improve throughput
- Make the Gremlin traversal smart enough to know whether the vertex or edge already exists and not re-load the entity if it exists.