package mate.academy.bookstore.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.bookstore.dto.cartitem.CartItemDto;
import mate.academy.bookstore.dto.shoppingcart.ShoppingCartDto;
import mate.academy.bookstore.model.CartItem;
import mate.academy.bookstore.model.ShoppingCart;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        implementationPackage = "<PACKAGE_NAME>.impl"
)
public interface ShoppingCartMapper {

    default ShoppingCartDto toDto(ShoppingCart shoppingCart,
                                  CartItemMapper cartItemMapper) {
        Long id = shoppingCart.getId();
        Long userId = shoppingCart.getUser().getId();
        Set<CartItem> itemsInSource = shoppingCart.getCartItems();
        Set<CartItemDto> itemsInResult = itemsInSource.stream()
                .map(cartItemMapper::toDto)
                .collect(Collectors.toSet());
        return new ShoppingCartDto(id, userId, itemsInResult);
    }

    @Mapping(target = "user", ignore = true)
    ShoppingCart toModel(ShoppingCartDto shoppingCartDto);
}
