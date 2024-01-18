package mate.academy.bookstore.repository.shoppingcart;

import mate.academy.bookstore.model.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
}