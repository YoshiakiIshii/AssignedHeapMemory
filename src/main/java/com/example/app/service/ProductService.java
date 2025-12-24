package com.example.app.service;

import com.example.app.model.Product;
import com.example.app.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> listAll() { return repo.findAll(false); }

    public Optional<Product> findById(String id) { return repo.findById(id); }

    public Product create(Product p) { p.setDeleted(false); return repo.save(p); }

    public Product update(Product p) { return repo.save(p); }

    public void delete(String id) { repo.softDelete(id); }

    public List<Product> search(String q) { return repo.search(q); }
}
