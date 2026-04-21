package com.uade.tpo.ecommerce.service;

import com.uade.tpo.ecommerce.entity.Order;
import com.uade.tpo.ecommerce.entity.OrderItem;
import com.uade.tpo.ecommerce.entity.User;
import com.uade.tpo.ecommerce.entity.Cart;
import com.uade.tpo.ecommerce.entity.CartItem;
import com.uade.tpo.ecommerce.enums.CartStatus;
import com.uade.tpo.ecommerce.enums.OrderStatus;
import com.uade.tpo.ecommerce.exceptions.CartAlreadyOrderedException;
import com.uade.tpo.ecommerce.exceptions.CartNotFoundException;
import com.uade.tpo.ecommerce.exceptions.ForbiddenOperationException;
import com.uade.tpo.ecommerce.exceptions.InvalidOrderStatusException;
import com.uade.tpo.ecommerce.exceptions.OrderNotFoundException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.repository.CartRepository;
import com.uade.tpo.ecommerce.repository.OrderRepository;
import com.uade.tpo.ecommerce.repository.UserRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Servicio para operaciones de negocio sobre órdenes de compra.
 * Incluye creación, consulta y actualización de estado.
 */
@Service
public class OrderService {

    private final OrderRepository repository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;

    /* Contexto de ownership para resolución de carrito y usuario en creación de ordenes. */
    private record OwnershipContext(Cart cart, User user) {
    }

    /**
     * Constructor con inyección de dependencias.
     * @param repository repositorio de órdenes
     * @param cartRepository repositorio de carritos
     * @param userRepository repositorio de usuarios
     */
    public OrderService(OrderRepository repository,
                        CartRepository cartRepository,
                        UserRepository userRepository,
                        AuthenticatedUserService authenticatedUserService) {
        this.repository = repository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
    }

    /**
     * Obtiene todas las órdenes.
     * @return lista de órdenes
     */
    public List<Order> getAll() {
        if (authenticatedUserService.isCurrentUserAdmin()) return repository.findAll();

        Long currentUserId = authenticatedUserService.getCurrentUserId();
        return repository.findAllByUserId(currentUserId);
    }

    /**
     * Busca una orden por su ID.
     * @param id identificador de la orden
     * @return la orden encontrada
     * @throws OrderNotFoundException si no existe la orden
     */
    public Order getById(@NonNull Long id) throws OrderNotFoundException {
        if (authenticatedUserService.isCurrentUserAdmin()) return repository.findById(id).orElseThrow(OrderNotFoundException::new);

        Long currentUserId = authenticatedUserService.getCurrentUserId();
        return repository.findByIdAndUserId(id, currentUserId).orElseThrow(OrderNotFoundException::new);
    }

    /**
     * Crea una nueva orden a partir de un carrito y usuario existentes.
     * @param order datos de la orden
     * @return la orden creada
     * @throws CartNotFoundException si el carrito no existe
     * @throws CartAlreadyOrderedException si el carrito ya tiene una orden
     * @throws UserNotFoundException si el usuario no existe
     */
    @Transactional
    public Order create(Order order) throws CartNotFoundException, CartAlreadyOrderedException, UserNotFoundException {
        // Validaciones de existencia y estado del carrito
        if (order.getCart() == null || order.getCart().getId() == null) throw new CartNotFoundException();

        // Verificar que el carrito no esté asociado a otra orden
        Long cartId = order.getCart().getId();
        if (repository.existsByCartId(cartId)) throw new CartAlreadyOrderedException();

        OwnershipContext ownershipContext = resolveOwnershipContext(order, cartId);
        Cart cart = ownershipContext.cart();
        User user = ownershipContext.user();

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("No se puede crear una orden con un carrito vacío");
        }

        // Crear snapshot de los items del carrito para la orden
        List<OrderItem> snapshotItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setProduct(cartItem.getProduct());
            snapshotItems.add(orderItem);
        }

        // Asociar la orden al carrito y usuario, y guardar la orden
        order.setCart(cart);
        order.setUser(user);
        order.setDate(new Date());
        order.setStatus(OrderStatus.CREATED);

        // Guardar la orden y los items asociados
        Order savedOrder = repository.save(order);
        for (OrderItem snapshotItem : snapshotItems) {
            snapshotItem.setOrder(savedOrder);
        }
        savedOrder.setItems(snapshotItems);
        savedOrder = repository.save(savedOrder);

        // Limpiar el carrito
        cart.getItems().clear();
        // Dejar el carrito en estado activo para que pueda ser reutilizado por el usuario
        cart.setStatus(CartStatus.ACTIVE);

        cartRepository.save(cart);

        return savedOrder;
    }

    /**
     * Actualiza el estado de una orden, validando la transición.
     * @param id identificador de la orden
     * @param newStatus nuevo estado a asignar
     * @return la orden actualizada
     * @throws OrderNotFoundException si la orden no existe
     * @throws InvalidOrderStatusException si la transición no es válida
     */
    public Order updateStatus(@NonNull Long id, OrderStatus newStatus) throws OrderNotFoundException, InvalidOrderStatusException {
        Order order = getById(id);
        OrderStatus currentStatus = order.getStatus();
        if (!isValidTransition(currentStatus, newStatus)) {
            if (isFinalStatus(currentStatus)) {
                throw new InvalidOrderStatusException(
                    "La orden ya se encuentra en un estado final (" + currentStatus + ") y no puede actualizarse."
                );
            }
            String nextAllowed = nextAllowedStatusMessage(currentStatus);
            throw new InvalidOrderStatusException("La transición de estado de la orden no es válida. El siguiente estado permitido es: " + nextAllowed);
        }
        order.setStatus(newStatus);
        return repository.save(order);
    }

    /**
     * Verifica si la transición de estado es válida según las reglas de negocio.
     * @param current estado actual
     * @param next estado siguiente
     * @return true si la transición es válida, false si no
     */
    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        return switch (current) {
            case CREATED -> next == OrderStatus.PENDING || next == OrderStatus.CANCELLED;
            case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.SHIPPED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            default -> false;
        };
    }

    /**
     * Devuelve un mensaje con los siguientes estados permitidos para una orden.
     * @param current estado actual
     * @return texto con los estados válidos siguientes
     */
    private String nextAllowedStatusMessage(OrderStatus current) {
        return switch (current) {
            case CREATED -> "PENDING o CANCELLED";
            case PENDING -> "CONFIRMED o CANCELLED";
            case CONFIRMED -> "SHIPPED";
            case SHIPPED -> "DELIVERED";
            default -> "-";
        };
    }

    /**
     * Determina si un estado representa un estado final de la orden.
     * @param status estado a evaluar
     * @return true si el estado es DELIVERED o CANCELLED
     */
    private boolean isFinalStatus(OrderStatus status) {
        return status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED;
    }

    /**
     * Devuelve los items de una orden por su ID.
     * @param id identificador de la orden
     * @return lista de items de la orden
     * @throws OrderNotFoundException si la orden no existe
     */
    public List<OrderItem> getOrderItems(@NonNull Long id) throws OrderNotFoundException {
        Order order = getById(id);
        return order.getItems();
    }

    /**
     * Elimina una orden por su ID.
     * @param id identificador de la orden
     * @throws OrderNotFoundException si la orden no existe
     */
    public void delete(@NonNull Long id) throws OrderNotFoundException {
        if (authenticatedUserService.isCurrentUserAdmin()) {
            if (!repository.existsById(id)) throw new OrderNotFoundException();
            repository.deleteById(id);
            return;
        }

        Long currentUserId = authenticatedUserService.getCurrentUserId();
        if (!repository.existsByIdAndUserId(id, currentUserId)) throw new OrderNotFoundException();
        repository.deleteById(id);
    }

    /**
     * Resuelve el carrito y usuario que deben asociarse a la orden segun el rol del autenticado.
     * @param order orden solicitada
     * @param cartId identificador del carrito a asociar
     * @return contexto de ownership con carrito y usuario efectivos
     * @throws CartNotFoundException si el carrito no existe o no pertenece al usuario autenticado
     * @throws UserNotFoundException si el usuario objetivo no existe
     */
    private OwnershipContext resolveOwnershipContext(Order order, Long cartId)
        throws CartNotFoundException, UserNotFoundException {
        if (authenticatedUserService.isCurrentUserAdmin()) {
            Cart cart = cartRepository.findById(cartId).orElseThrow(CartNotFoundException::new);
            User user = resolveUserForAdmin(order, cart);
            return new OwnershipContext(cart, user);
        }

        User currentUser = authenticatedUserService.getCurrentUser();
        Cart cart = cartRepository.findByIdAndUserId(cartId, currentUser.getId())
            .orElseThrow(CartNotFoundException::new);
        if (order.getUser() != null && order.getUser().getId() != null && !currentUser.getId().equals(order.getUser().getId())) {
            throw new ForbiddenOperationException("No puede crear ordenes para otro usuario");
        }
        return new OwnershipContext(cart, currentUser);
    }

    /**
     * Resuelve el usuario final de la orden cuando la operacion es ejecutada por administrador.
     * @param order orden recibida
     * @param cart carrito asociado a la orden
     * @return usuario destino de la orden
     * @throws UserNotFoundException si se informa un usuario inexistente
     */
    private User resolveUserForAdmin(Order order, Cart cart) throws UserNotFoundException {
        if (order.getUser() == null || order.getUser().getId() == null) return cart.getUser();

        Long targetUserId = order.getUser().getId();
        return userRepository.findById(targetUserId).orElseThrow(UserNotFoundException::new);
    }
}