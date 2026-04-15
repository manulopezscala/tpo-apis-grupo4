package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.enums.CartStatus;
import com.uade.tpo.ecommerce.exceptions.CartNotFoundException;
import com.uade.tpo.ecommerce.exceptions.EmptyCartException;
import com.uade.tpo.ecommerce.exceptions.InvalidCartStatusException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.repository.CartRepository;
import com.uade.tpo.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Servicio para operaciones de negocio sobre carritos de compra.
 * Permite crear, consultar, finalizar y eliminar carritos.
 */
@Service
public class CartService {

    private final CartRepository repository;
    private final UserRepository userRepository;

    /**
     * Constructor con inyección de dependencias.
     * @param repository repositorio de carritos
     * @param userRepository repositorio de usuarios
     */
    public CartService(CartRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene todos los carritos.
     * @return lista de carritos
     */
    public List<Cart> getAll() {
        return repository.findAll();
    }

    /**
     * Busca un carrito por su ID.
     * @param id identificador del carrito
     * @return el carrito encontrado
     * @throws CartNotFoundException si no existe el carrito
     */
    public Cart getById(Long id) throws CartNotFoundException {
        return repository.findById(id).orElseThrow(CartNotFoundException::new);
    }

    /**
     * Crea un nuevo carrito para un usuario existente.
     * @param cart datos del carrito
     * @return el carrito creado
     * @throws UserNotFoundException si el usuario no existe
     */
    public Cart create(Cart cart) throws UserNotFoundException {
        if (cart.getUser() == null || cart.getUser().getId() == null) {
            throw new IllegalArgumentException("Debe informar un user.id válido para crear el carrito");
        }

        User user = userRepository.findById(cart.getUser().getId()).orElseThrow(UserNotFoundException::new);
        cart.setUser(user);
        cart.setStatus(CartStatus.ACTIVE);
        return repository.save(cart);
    }

    /**
     * Finaliza un carrito (checkout), validando estado y contenido.
     * @param id identificador del carrito
     * @return el carrito actualizado
     * @throws CartNotFoundException si el carrito no existe
     * @throws InvalidCartStatusException si el carrito no está activo
     * @throws EmptyCartException si el carrito está vacío
     */
    public Cart checkout(Long id) throws CartNotFoundException, InvalidCartStatusException, EmptyCartException {
        Cart cart = getById(id);
        if (cart.getStatus() != CartStatus.ACTIVE) throw new InvalidCartStatusException();
        if (cart.getItems() == null || cart.getItems().isEmpty()) throw new EmptyCartException();
        cart.setStatus(CartStatus.COMPLETED);
        return repository.save(cart);
    }

    /**
     * Elimina un carrito por su ID.
     * @param id identificador del carrito
     * @throws CartNotFoundException si el carrito no existe
     */
    public void delete(Long id) throws CartNotFoundException {
        if (!repository.existsById(id)) throw new CartNotFoundException();
        repository.deleteById(id);
    }
}