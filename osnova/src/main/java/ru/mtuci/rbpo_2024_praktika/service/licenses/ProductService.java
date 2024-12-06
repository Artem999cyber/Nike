package ru.mtuci.rbpo_2024_praktika.service.licenses;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mtuci.rbpo_2024_praktika.exceptions.ProductNotFoundException;
import ru.mtuci.rbpo_2024_praktika.model.Product;
import ru.mtuci.rbpo_2024_praktika.repository.ProductRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    public Product getproductById(UUID productId){
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found."));
    }
}
