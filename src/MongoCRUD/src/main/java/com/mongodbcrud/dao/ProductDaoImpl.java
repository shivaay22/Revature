package com.mongodbcrud.dao;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodbcrud.config.MongoConnection;
import com.mongodbcrud.model.Product;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class ProductDaoImpl implements ProductDao{
    private final MongoCollection<Document> collection;
    
    public ProductDaoImpl() {

        MongoDatabase database = MongoConnection.getDatabase();
        this.collection = database.getCollection("products");

    }

    public void testConnection(){
        try{
            long count = this.collection.countDocuments();
            System.out.println("Connected Successfully to MongoDb");
            System.out.println("Total Document in products Collection: " + count);
        } catch (Exception e){
            System.out.println("Connection Failed");
            e.printStackTrace();
        }
    }

    public List<Product> findAll(){
        List<Product> products = new ArrayList<>();
        MongoCursor var2 = this.collection.find().iterator();

        while(var2.hasNext()){
            Document doc = (Document)var2.next();
            Product product = new Product();
            product.setId(doc.getInteger("_id",0));
            product.setProductName(doc.getString("productName"));
            product.setPrice(doc.get("price") != null ? ((Number)doc.get("price")).doubleValue() : (double)0.0F);
            product.setReleaseDate(doc.getDate("releaseDate"));
            products.add(product);
        }

        return products;
    }

    public Product findById(int id) {
        Document doc = (Document)this.collection.find(Filters.eq("_id", id)).first();
        if (doc == null) {
            return null;
        } else {
            Product product = new Product();
            product.setId(doc.getInteger("_id", 0));
            product.setProductName(doc.getString("productname"));
            product.setPrice(doc.get("price") != null ? ((Number)doc.get("price")).doubleValue() : (double)0.0F);
            product.setReleaseDate(doc.getDate("releaseDate"));
            return product;
        }
    }

    public void insert(Product product) {
        Document doc = (new Document("_id", product.getId())).append("productname", product.getProductName()).append("price", product.getPrice()).append("releaseDate", product.getReleaseDate());
        this.collection.insertOne(doc);
    }

    public void update(Product product) {
        this.collection.updateOne(Filters.eq("_id", product.getId()), Updates.combine(new Bson[]{Updates.set("productname", product.getProductName()), Updates.set("price", product.getPrice()), Updates.set("releaseDate", product.getReleaseDate())}));
    }

    public void deleteById(int id) {
        this.collection.deleteOne(Filters.eq("_id", id));
    }
    
}
