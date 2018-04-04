package com.hatcherdev.AdvGraphPersonalization.DataLoader;

import com.hatcherdev.AdvGraphPersonalization.DataLoader.objects.Transaction;

import com.datastax.driver.dse.*;
import com.datastax.driver.dse.graph.*;
import com.datastax.dse.graph.api.DseGraph;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.*;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataLoader {

    private Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private SimpleDateFormat sdfDateOnly = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdfDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    private DseSession _dseSession;
    private GraphTraversalSource g;

    private String _filePath;
    private String _clusterNode;

    public DataLoader(String filePath, String clusterNode) {

        _filePath = filePath;
        _clusterNode = clusterNode;

        connectCluster();
    }

    public static void main(String[] args){

        Logger logger = LoggerFactory.getLogger(DataLoader.class);

        if (args.length != 2){
            logger.error("You must specify the following arguments: the file path to process, a node in the cluster");
        }

        String filePath = args[0];
        String clusterNode = args[1];

        DataLoader dataLoader = new DataLoader(filePath, clusterNode);
        dataLoader.run();

        System.exit(0);

    }

    private void connectCluster(){
        String graphName = "adv_graph_personalization";

        DseCluster dseCluster = DseCluster.builder()
                .addContactPoint(_clusterNode)
                .withGraphOptions(new GraphOptions()
                        .setGraphName(graphName)
                        .setGraphSubProtocol(GraphProtocol.GRAPHSON_2_0))
                .build();

        _dseSession = dseCluster.connect();

        g = DseGraph.traversal(_dseSession);

    }

    public void run(){

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(_filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Integer lineCounter = 0;

        logger.info("Starting to read from file...");

        try {
            for (String line = br != null ? br.readLine() : null; line != null; line = br.readLine()) {

                //skip the header row
                if (line.equals("id,chain,dept,category,company,brand,date,productsize,productmeasure,purchasequantity,purchaseamount")){
                    continue;
                }

                String[] fieldList = line.split(",");

                final Integer requiredFieldCount = 11;

                if (fieldList.length != requiredFieldCount){
                    logger.warn("Input line does not have " + requiredFieldCount.toString() + " fields: " + line);
                    continue;
                }

                Integer fieldIterator = 0;

                Long customerID;
                Long chainID;
                Long departmentID;
                Long categoryID;
                Long companyID;
                Long brandID;
                Long timestamp;
                Double productSize;
                String productMeasure;
                Integer quantity;
                Double amount;
                try {
                    customerID = Long.parseLong(fieldList[fieldIterator++]);
                    chainID = Long.parseLong(fieldList[fieldIterator++]);
                    departmentID = Long.parseLong(fieldList[fieldIterator++]);
                    categoryID = Long.parseLong(fieldList[fieldIterator++]);
                    companyID = Long.parseLong(fieldList[fieldIterator++]);
                    brandID = Long.parseLong(fieldList[fieldIterator++]);
                    timestamp = getTimestamp(fieldList[fieldIterator++]);
                    productSize = Double.parseDouble(fieldList[fieldIterator++]);
                    productMeasure = fieldList[fieldIterator++];
                    quantity = Integer.parseInt(fieldList[fieldIterator++]);
                    amount = Double.parseDouble(fieldList[fieldIterator++]);
                } catch(Exception ex) {
                    logger.error("Error on line " + lineCounter.toString() + " for field # " + Integer.toString(fieldIterator + 1) + ": " + line);
                    continue;
                }

                Transaction transaction = new Transaction(chainID, companyID, brandID, productSize, productMeasure,
                        categoryID, departmentID, customerID, timestamp, quantity, amount);

                insertVerticesAndEdges(transaction);

                lineCounter++;

                if (lineCounter % 1000 == 0){
                    String dateString = sdfDateAndTime.format(new Date(System.currentTimeMillis()));
                    logger.info("Processed " + lineCounter.toString() + " records at " + dateString);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private Long getTimestamp(String dateString){
        Date date = null;
        try {
            //format: 2012-03-02
            date = sdfDateOnly.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    private void insertVerticesAndEdges(Transaction transaction)
    {

        GraphTraversal<Vertex, Edge> traversal =
                g
                    .addV("customer")
                        .property("customer_id", transaction.getCustomerID())
                        .as("customer")
                    .addV("product")
                        .property("chain_id", transaction.getChainID())
                        .property("company_id", transaction.getCompanyID())
                        .property("brand_id", transaction.getBrandID())
                        .property("product_size", transaction.getProductSize())
                        .property("product_measure", transaction.getProductMeasure())
                        .property("category_id", transaction.getCategoryID())
                        .property("department_id", transaction.getDepartmentID())
                        .as("product")
                    .addE("purchases")
                        .property("date", transaction.getDate())
                        .property("quantity", transaction.getQuantity())
                        .property("amount", transaction.getAmount())
                        .from("customer")
                        .to("product")
                ;

        GraphStatement statement = DseGraph.statementFromTraversal(traversal);
        _dseSession.executeGraph(statement);

    }


}
