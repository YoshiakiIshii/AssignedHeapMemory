package com.example.app.repository;

import com.example.app.model.Product;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {

    private final Path filePath;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");;

    public ProductRepository(@Value("${app.data-file:data/products.csv}") String path) {
        this.filePath = Paths.get(path);
    }

    @PostConstruct
    public void init() throws IOException {
        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
    }

    private synchronized List<Product> readAll() {
        try (java.io.Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            HeaderColumnNameMappingStrategy<Product> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(Product.class);
            CsvToBean<Product> csvToBean = new CsvToBeanBuilder<Product>(reader)
                    .withMappingStrategy(strategy)
                    .withType(Product.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<Product> list = csvToBean.parse();
            return list == null ? new ArrayList<>() : list;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private synchronized void writeAll(List<Product> list) {
        try (java.io.Writer writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            // write header
            String[] header = new String[]{"id", "name", "manufacturer", "price", "createdAt", "updatedAt", "deleted"};
            writer.write(String.join(",", header));
            writer.write(System.lineSeparator());

            ColumnPositionMappingStrategy<Product> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Product.class);

            StatefulBeanToCsv<Product> beanToCsv = new StatefulBeanToCsvBuilder<Product>(writer)
                    .withMappingStrategy(strategy)
                    .withApplyQuotesToAll(false)
                    .build();
            beanToCsv.write(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Product> findAll(boolean includeDeleted) {
        return readAll().stream()
                .filter(p -> includeDeleted || !p.isDeleted())
                .sorted(Comparator.comparing(Product::getId))
                .collect(Collectors.toList());
    }

    public Optional<Product> findById(String id) {
        return readAll().stream().filter(p -> p.getId() != null && p.getId().equals(id)).findFirst();
    }

    public synchronized Product save(Product product) {
        List<Product> all = readAll();
        if (product.getId() == null) {
            String max = all.stream()
                    .map(Product::getId)
                    .filter(x -> x != null)
                    .map(Long::parseLong)
                    .max(Long::compareTo)
                    .map(String::valueOf)
                    .orElse("0");
            product.setId(String.valueOf(Long.parseLong(max) + 1));
            product.setCreatedAt(LocalDateTime.now().format(DATE_FORMATTER));
            product.setUpdatedAt(LocalDateTime.now().format(DATE_FORMATTER));
            all.add(product);
        } else {
            all = all.stream().map(p -> {
                if (p.getId() != null && p.getId().equals(product.getId())) {
                    product.setCreatedAt(p.getCreatedAt() == null ? LocalDateTime.now().format(DATE_FORMATTER) : p.getCreatedAt());
                    product.setUpdatedAt(LocalDateTime.now().format(DATE_FORMATTER));
                    return product;
                }
                return p;
            }).collect(Collectors.toList());
        }
        writeAll(all);
        return product;
    }

    public synchronized void softDelete(String id) {
        List<Product> all = readAll().stream().map(p -> {
            if (p.getId() != null && p.getId().equals(id)) {
                p.setDeleted(true);
                p.setUpdatedAt(LocalDateTime.now().format(DATE_FORMATTER));
            }
            return p;
        }).collect(Collectors.toList());
        writeAll(all);
    }

    public List<Product> search(String q) {
        if (q == null || q.isBlank()) return findAll(false);
        String low = q.toLowerCase();
        return readAll().stream()
                .filter(p -> !p.isDeleted())
                .filter(p -> (p.getName() != null && p.getName().toLowerCase().contains(low))
                        || (p.getManufacturer() != null && p.getManufacturer().toLowerCase().contains(low)))
                .sorted(Comparator.comparing(Product::getId))
                .collect(Collectors.toList());
    }
}
