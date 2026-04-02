package com.mongodbcrud.dao;

import com.mongodbcrud.model.Product;

import java.util.List;

public interface ProductDao {
    void testConnection();

    List<Product> findAll();

    Product findById(int var1);

    void insert(Product var1);
    void update(Product var1);
    void deleteById(int var1);
}
