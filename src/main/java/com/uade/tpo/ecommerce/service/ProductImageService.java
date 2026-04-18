package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.ProductImage;
import com.uade.tpo.ecommerce.exceptions.ProductImageNotFoundException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import com.uade.tpo.ecommerce.repository.ProductImageRepository;
import org.springframework.stereotype.Service;


/**
 * Servicio para operaciones sobre imágenes de productos.
 * Permite crear imágenes asociadas a productos existentes.
 */
@Service
public class ProductImageService {

    private final ProductImageRepository repository;
    private final ProductRepository productRepository;

    /**
     * Constructor con inyección de dependencias.
     * @param repository repositorio de imágenes de producto
     * @param productRepository repositorio de productos
     */
    public ProductImageService(ProductImageRepository repository, ProductRepository productRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
    }

    /**
     * Crea una nueva imagen para un producto existente.
     * @param image datos de la imagen
     * @return la imagen creada
     * @throws ProductNotFoundException si el producto no existe
     */
    public ProductImage create(ProductImage image) throws ProductNotFoundException {
        if (image.getProduct() == null || image.getProduct().getId() == null) {
            throw new IllegalArgumentException("Debe informar un product.id válido para crear la imagen");
        }
        image.setProduct(productRepository.findById(image.getProduct().getId()).orElseThrow(ProductNotFoundException::new));
        return repository.save(image);
    }

    /**
     * Elimina una imagen de producto por su ID.
     * @param id identificador de la imagen
     * @throws ProductImageNotFoundException si la imagen no existe
     */
    public void delete(Long id) throws ProductImageNotFoundException {
        if (!repository.existsById(id)) throw new ProductImageNotFoundException();
        repository.deleteById(id);
    }
}