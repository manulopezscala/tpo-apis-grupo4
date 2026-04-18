package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.enums.CartStatus;
import com.uade.tpo.ecommerce.exceptions.ActiveCartAlreadyExistsException;
import com.uade.tpo.ecommerce.exceptions.CartNotFoundException;
import com.uade.tpo.ecommerce.exceptions.EmptyCartException;
import com.uade.tpo.ecommerce.exceptions.ForbiddenOperationException;
import com.uade.tpo.ecommerce.exceptions.InvalidCartStatusException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.entity.CartItem;
import com.uade.tpo.ecommerce.repository.CartRepository;
import com.uade.tpo.ecommerce.repository.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Servicio para operaciones de negocio sobre carritos de compra.
 * Permite crear, consultar, finalizar y eliminar carritos.
 */
@Service
public class CartService {

    private final CartRepository repository;
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;

    /**
     * Constructor con inyección de dependencias.
     * @param repository repositorio de carritos
     * @param userRepository repositorio de usuarios
     */
    public CartService(CartRepository repository,
                       UserRepository userRepository,
                       AuthenticatedUserService authenticatedUserService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    /**
     * Obtiene todos los carritos.
     * @return lista de carritos
     */
    public List<Cart> getAll() {
        if (authenticatedUserService.isCurrentUserAdmin()) {
            return repository.findAll();
        }
        Long currentUserId = authenticatedUserService.getCurrentUserId();
        return repository.findAllByUserId(currentUserId);
    }

    /**
     * Busca un carrito por su ID.
     * @param id identificador del carrito
     * @return el carrito encontrado
     * @throws CartNotFoundException si no existe el carrito
     */
    public Cart getById(@NonNull Long id) throws CartNotFoundException {
        if (authenticatedUserService.isCurrentUserAdmin()) {
            return repository.findById(id).orElseThrow(CartNotFoundException::new);
        }
        Long currentUserId = authenticatedUserService.getCurrentUserId();
        return repository.findByIdAndUserId(id, currentUserId).orElseThrow(CartNotFoundException::new);
    }

    /**
     * Crea un nuevo carrito para un usuario existente.
     * @param cart datos del carrito
     * @return el carrito creado
     * @throws UserNotFoundException si el usuario no existe
     * @throws ActiveCartAlreadyExistsException si el usuario ya tiene un carrito activo
     */
    public Cart create(Cart cart) throws UserNotFoundException, ActiveCartAlreadyExistsException {
        User user;
        if (authenticatedUserService.isCurrentUserAdmin()) {
            if (cart.getUser() == null || cart.getUser().getId() == null) {
                throw new IllegalArgumentException("Debe informar un user.id valido para crear el carrito");
            }
            Long targetUserId = cart.getUser().getId();
            user = userRepository.findById(targetUserId).orElseThrow(UserNotFoundException::new);
        } else {
            user = authenticatedUserService.getCurrentUser();
            if (cart.getUser() != null && cart.getUser().getId() != null && !user.getId().equals(cart.getUser().getId())) {
                throw new ForbiddenOperationException("No puede crear carritos para otro usuario");
            }
        }

        if (repository.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE).isPresent()) {
            throw new ActiveCartAlreadyExistsException();
        }

        cart.setUser(user);
        if (cart.getItems() == null) {
            cart.setItems(new ArrayList<>());
        }
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
    public Cart checkout(@NonNull Long id) throws CartNotFoundException, InvalidCartStatusException, EmptyCartException {
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
    public void delete(@NonNull Long id) throws CartNotFoundException {
        if (authenticatedUserService.isCurrentUserAdmin()) {
            if (!repository.existsById(id)) throw new CartNotFoundException();
            repository.deleteById(id);
            return;
        }

        Long currentUserId = authenticatedUserService.getCurrentUserId();
        if (!repository.existsByIdAndUserId(id, currentUserId)) throw new CartNotFoundException();
        repository.deleteById(id);
    }

    /**
     * Devuelve los items de un carrito por su ID.
     * @param id identificador del carrito
     * @return lista de items del carrito
     * @throws CartNotFoundException si el carrito no existe
     */
    public List<CartItem> getCartItems(@NonNull Long id) throws CartNotFoundException {
        Cart cart = getById(id);
        return cart.getItems();
    }
}