schema.config().option('graph.schema_mode').set('Development')
schema.config().option('graph.allow_scan').set('true')
schema.config().option('graph.traversal_sources.g.restrict_lambda').set(false)

schema.drop()

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

schema.vertexLabel("customer").partitionKey("customer_id")
// .properties(
//   "??",
//   "??"
// )
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
