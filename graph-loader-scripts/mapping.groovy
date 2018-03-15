config create_schema: false, load_new: true, load_vertex_threads: 3, preparation: false

inputfiledir = '/Users/jimhatcher/src/adv-graph-personalization/graph-loader-scripts/'


transactionsInput = File.csv(inputfiledir + 'transactions.csv').delimiter(',').header('id', 'chain', 'dept', 'category', 'company', 'brand', 'date', 'productsize', 'productmeasure', 'purchasequantity', 'purchaseamount')

transactionsInput = transactionsInput.transform {
    it['customer_id'] = it['id'];
    it['chain_id'] = it['chain'];
    it['department_id'] = it['dept'];
    it['category_id'] = it['category'];
    it['company_id'] = it['company'];
    it['brand_id'] = it['brand'];
    it['product_size'] = it['productsize'];
    it['product_measure'] = it['productmeasure'];
    it
}

load(transactionsInput).asVertices {
    label "customer"
    key "customer_id"
    ignore "chain"
    ignore "dept"
    ignore "category"
    ignore "company"
    ignore "brand"
    ignore "date"
    ignore "productsize"
    ignore "productmeasure"
    ignore "purchasequantity"
    ignore "purchaseamount"
    ignore "id"
}

load(transactionsInput).asVertices {
    label "product"
    key "customer_id"
    ignore "chain"
    ignore "dept"
    ignore "category"
    ignore "company"
    ignore "brand"
    ignore "date"
    ignore "productsize"
    ignore "productmeasure"
    ignore "purchasequantity"
    ignore "purchaseamount"
    ignore "id"
    ignore "customer_id"
}


//load(transactionsInput).asVertices {
//    label "product"
//    key "sessionId"
//}

//load(transactionsInput).asEdges {
//    label "isRelatedTo"
//    outV "parentCustomerId", {
//        label "customer"
//        key "customerId"
//    }
//    inV "childCustomerId", {
//        label "customer"
//        key "customerId"
//    }
//}

//run with a command like this
// /Users/jimhatcher/datastax/dse-graph-loader-6.0.0/graphloader /Users/jimhatcher/src/adv-graph-personalization/graph-loader-scripts/mapping.groovy -graph adv_graph_personalization -address ec2-52-53-130-28.us-west-1.compute.amazonaws.com -dryrun true

/home/rightscale/dse-graph-loader-6.0.0/graphloader /home/rightscale/graph-loader-scripts/mapping.groovy -graph adv_graph_personalization -address ec2-52-53-130-28.us-west-1.compute.amazonaws.com -dryrun false