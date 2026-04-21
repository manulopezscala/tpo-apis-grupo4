package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Discount;
import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.exceptions.DiscountDuplicateException;
import com.uade.tpo.ecommerce.exceptions.DiscountNotFoundException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.repository.DiscountRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;


/**
 * Servicio para operaciones sobre descuentos de productos.
 * Permite crear descuentos asociados a productos existentes.
 */
@Service
public class DiscountService {

    private final DiscountRepository repository;
    private final ProductRepository productRepository;

    /**
     * Constructor con inyección de dependencias.
     * @param repository repositorio de descuentos
     * @param productRepository repositorio de productos
     */
    public DiscountService(DiscountRepository repository, ProductRepository productRepository) {
        this.repository = repository;
        this.productRepository = productRepository;
    }

    /**
     * Crea un nuevo descuento para un producto existente.
     * @param discount datos del descuento
     * @return el descuento creado
     * @throws DiscountDuplicateException si ya existe un descuento para el producto
     * @throws ProductNotFoundException si el producto no existe
     */
    public Discount create(Discount discount) throws DiscountDuplicateException, ProductNotFoundException {
        if (discount.getProduct() == null || discount.getProduct().getId() == null) {
            throw new IllegalArgumentException("Debe informar un product.id válido para crear el descuento");
        }

        Long productId = discount.getProduct().getId();
        if (repository.existsByProductId(productId)) throw new DiscountDuplicateException();

        Product product = productRepository.findById(productId).orElseThrow(ProductNotFoundException::new);
        discount.setProduct(product);
        return repository.save(discount);
    }

    /**
     * Elimina un descuento por su ID.
     * @param id identificador del descuento
     * @throws DiscountNotFoundException si el descuento no existe
     */
    public void delete(Long id) throws DiscountNotFoundException {
        if (!repository.existsById(id)) throw new DiscountNotFoundException();
        repository.deleteById(id);
    }
}