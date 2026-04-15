package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Product;
import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.exceptions.CategoryNotFoundException;
import com.uade.tpo.ecommerce.exceptions.ProductDuplicateException;
import com.uade.tpo.ecommerce.exceptions.ProductNotFoundException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.repository.CategoryRepository;
import com.uade.tpo.ecommerce.repository.ProductRepository;
import com.uade.tpo.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Servicio para operaciones de negocio sobre productos.
 * Permite crear, consultar, actualizar y eliminar productos.
 */
@Service
public class ProductService {

    private final ProductRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Constructor con inyección de dependencias.
     * @param repository repositorio de productos
     * @param userRepository repositorio de usuarios
     * @param categoryRepository repositorio de categorías
     */
    public ProductService(ProductRepository repository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Obtiene todos los productos.
     * @return lista de productos
     */
    public List<Product> getAll() {
        return repository.findAll();
    }

    /**
     * Busca un producto por su ID.
     * @param id identificador del producto
     * @return el producto encontrado
     * @throws ProductNotFoundException si no existe el producto
     */
    public Product getById(Long id) throws ProductNotFoundException {
        return repository.findById(id).orElseThrow(ProductNotFoundException::new);
    }

        /**
         * Crea un nuevo producto, validando unicidad y relaciones.
         * @param product datos del producto
         * @return el producto creado
         * @throws ProductDuplicateException si ya existe un producto con ese nombre
         * @throws UserNotFoundException si el vendedor no existe
         * @throws CategoryNotFoundException si la categoría no existe
         */
        public Product create(Product product)
            throws ProductDuplicateException, UserNotFoundException, CategoryNotFoundException {
        if (repository.existsByNameIgnoreCase(product.getName()))
            throw new ProductDuplicateException();

        if (product.getSeller() == null || product.getSeller().getId() == null) {
            throw new IllegalArgumentException("Debe informar un seller.id válido para crear el producto");
        }
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            throw new IllegalArgumentException("Debe informar un category.id válido para crear el producto");
        }

        User seller = userRepository.findById(product.getSeller().getId()).orElseThrow(UserNotFoundException::new);
        Category category = categoryRepository.findById(product.getCategory().getId()).orElseThrow(CategoryNotFoundException::new);
        product.setSeller(seller);
        product.setCategory(category);
        return repository.save(product);
    }

        /**
         * Actualiza un producto existente.
         * @param id identificador del producto
         * @param product datos actualizados
         * @return el producto actualizado
         * @throws ProductNotFoundException si el producto no existe
         * @throws UserNotFoundException si el vendedor no existe
         * @throws CategoryNotFoundException si la categoría no existe
         */
        public Product update(Long id, Product product)
            throws ProductNotFoundException, UserNotFoundException, CategoryNotFoundException {
        if (!repository.existsById(id)) throw new ProductNotFoundException();

        if (product.getSeller() == null || product.getSeller().getId() == null) {
            throw new IllegalArgumentException("Debe informar un seller.id válido para actualizar el producto");
        }
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            throw new IllegalArgumentException("Debe informar un category.id válido para actualizar el producto");
        }

        User seller = userRepository.findById(product.getSeller().getId()).orElseThrow(UserNotFoundException::new);
        Category category = categoryRepository.findById(product.getCategory().getId()).orElseThrow(CategoryNotFoundException::new);
        product.setSeller(seller);
        product.setCategory(category);
        product.setId(id);
        return repository.save(product);
    }

    /**
     * Elimina un producto por su ID.
     * @param id identificador del producto
     * @throws ProductNotFoundException si el producto no existe
     */
    public void delete(Long id) throws ProductNotFoundException {
        if (!repository.existsById(id)) throw new ProductNotFoundException();
        repository.deleteById(id);
    }
}