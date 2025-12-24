package com.example.app.controller;

import com.example.app.model.Product;
import com.example.app.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        if (q != null && !q.isBlank()) {
            model.addAttribute("products", service.search(q));
            model.addAttribute("q", q);
        } else {
            model.addAttribute("products", service.listAll());
        }
        return "list";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("product", new Product());
        return "form";
    }

    @PostMapping
    public String create(@ModelAttribute Product product) {
        service.create(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model) {
        Optional<Product> p = service.findById(id);
        if (p.isPresent()) {
            model.addAttribute("product", p.get());
            return "edit";
        }
        return "redirect:/products";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable String id, @ModelAttribute Product product) {
        product.setId(id);
        service.update(product);
        return "redirect:/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id) {
        service.delete(id);
        return "redirect:/products";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable String id, Model model) {
        Optional<Product> p = service.findById(id);
        if (p.isPresent()) {
            model.addAttribute("product", p.get());
            return "details";
        }
        return "redirect:/products";
    }
}
