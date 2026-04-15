package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Category;
import com.uade.tpo.ecommerce.exceptions.CategoryDuplicateException;
import com.uade.tpo.ecommerce.exceptions.CategoryNotFoundException;
import com.uade.tpo.ecommerce.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;



/**
 * Servicio para operaciones sobre categorías de productos.
 * Permite crear, consultar y eliminar categorías.
 */
@Service
public class CategoryService {

    private final CategoryRepository repository;

    /**
     * Constructor con inyección de dependencias.
     * @param repository repositorio de categorías
     */
    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    /**
     * Obtiene todas las categorías.
     * @return lista de categorías
     */
    public List<Category> getAll() {
        return repository.findAll();
    }

    /**
     * Busca una categoría por su ID.
     * @param id identificador de la categoría
     * @return la categoría encontrada
     * @throws CategoryNotFoundException si no existe la categoría
     */
    public Category getById(Long id) throws CategoryNotFoundException {
        return repository.findById(id).orElseThrow(CategoryNotFoundException::new);
    }

    /**
     * Crea una nueva categoría, validando unicidad.
     * @param category datos de la categoría
     * @return la categoría creada
     * @throws CategoryDuplicateException si ya existe una categoría con ese nombre
     */
    public Category create(Category category) throws CategoryDuplicateException {
        if (repository.existsByNameIgnoreCase(category.getName()))
            throw new CategoryDuplicateException();
        return repository.save(category);
    }

    /**
     * Elimina una categoría por su ID.
     * @param id identificador de la categoría
     * @throws CategoryNotFoundException si la categoría no existe
     */
    public void delete(Long id) throws CategoryNotFoundException {
        if (!repository.existsById(id)) throw new CategoryNotFoundException();
        repository.deleteById(id);
    }
}