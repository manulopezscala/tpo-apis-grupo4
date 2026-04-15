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
import com.uade.tpo.ecommerce.exceptions.InvalidOrderStatusException;
import com.uade.tpo.ecommerce.exceptions.OrderNotFoundException;
import com.uade.tpo.ecommerce.exceptions.UserNotFoundException;
import com.uade.tpo.ecommerce.repository.CartRepository;
import com.uade.tpo.ecommerce.repository.OrderRepository;
import com.uade.tpo.ecommerce.repository.UserRepository;
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

    /**
     * Constructor con inyección de dependencias.
     * @param repository repositorio de órdenes
     * @param cartRepository repositorio de carritos
     * @param userRepository repositorio de usuarios
     */
    public OrderService(OrderRepository repository, CartRepository cartRepository, UserRepository userRepository) {
        this.repository = repository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    /**
     * Obtiene todas las órdenes.
     * @return lista de órdenes
     */
    public List<Order> getAll() {
        return repository.findAll();
    }

    /**
     * Busca una orden por su ID.
     * @param id identificador de la orden
     * @return la orden encontrada
     * @throws OrderNotFoundException si no existe la orden
     */
    public Order getById(Long id) throws OrderNotFoundException {
        return repository.findById(id).orElseThrow(OrderNotFoundException::new);
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
        if (order.getCart() == null || order.getCart().getId() == null) {
            throw new CartNotFoundException();
        }

        // Verificar que el carrito no esté asociado a otra orden
        Long cartId = order.getCart().getId();
        if (repository.existsByCartId(cartId)) {
            throw new CartAlreadyOrderedException();
        }

        // Validar que el carrito exista y tenga items
        Cart cart = cartRepository.findById(cartId).orElseThrow(CartNotFoundException::new);
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
        if (order.getUser() != null && order.getUser().getId() != null) {
            User user = userRepository.findById(order.getUser().getId()).orElseThrow(UserNotFoundException::new);
            order.setUser(user);
        }
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
    public Order updateStatus(Long id, OrderStatus newStatus) throws OrderNotFoundException, InvalidOrderStatusException {
        Order order = getById(id);
        if (!isValidTransition(order.getStatus(), newStatus)) {
            String nextAllowed = nextAllowedStatusMessage(order.getStatus());
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
}