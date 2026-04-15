package com.mongodbcrud.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
    private static final String Connection_String = "mongodb://localhost:27017";
    private static final String Database_Name = "peersdb";
    private static MongoClient mongoClient;

    public static MongoDatabase getDatabase(){
        if(mongoClient == null){
            mongoClient = MongoClients.create("mongodb://localhost:27017");
        }

        return mongoClient.getDatabase("peersdb");
    }

    public static void closeConnection(){
        if(mongoClient != null){
            mongoClient.close();
        }
    }

}
