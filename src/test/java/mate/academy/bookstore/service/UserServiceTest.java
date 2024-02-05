package mate.academy.bookstore.service;

import static mate.academy.bookstore.config.DatabaseHelper.JOHN_REGISTRATION_REQUEST_DTO;
import static mate.academy.bookstore.config.DatabaseHelper.JOHN_RESPONSE_DTO;
import static mate.academy.bookstore.config.DatabaseHelper.USER_JOHN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserResponseDto;
import mate.academy.bookstore.exception.RegistrationException;
import mate.academy.bookstore.mapper.UserMapper;
import mate.academy.bookstore.model.Role;
import mate.academy.bookstore.model.ShoppingCart;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.role.RoleRepository;
import mate.academy.bookstore.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.bookstore.repository.user.UserRepository;
import mate.academy.bookstore.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Register new user")
    void register_ValidUserRegistrationRequest_Success() throws RegistrationException {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(userMapper.toModel(any(UserRegistrationRequestDto.class)))
                .thenReturn(USER_JOHN);
        when(roleRepository.findByName(Role.RoleName.ROLE_USER))
                .thenReturn(USER_JOHN.getRoles().stream().findFirst());
        when(userRepository.save(any(User.class)))
                .thenReturn(USER_JOHN);
        when(passwordEncoder.encode(anyString()))
                .thenReturn(USER_JOHN.getPassword());
        doReturn(null).when(shoppingCartRepository).save(any(ShoppingCart.class));
        UserResponseDto expected = JOHN_RESPONSE_DTO;
        when(userMapper.toDto(any(User.class)))
                .thenReturn(expected);
        UserResponseDto actual = userService.register(JOHN_REGISTRATION_REQUEST_DTO);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Register new user when user already exists")
    void register_UserAlreadyExists_ShouldThrowRegistrationException() {
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(USER_JOHN));
        RegistrationException registrationException = assertThrows(RegistrationException.class,
                () -> userService.register(JOHN_REGISTRATION_REQUEST_DTO));
        assertEquals(RegistrationException.class, registrationException.getClass());
        assertEquals("Email already registered", registrationException.getMessage());
    }

    @Test
    @DisplayName("Register new shopping cart when user is 'null'")
    void registerNewShoppingCart_UserIsNull_ShouldThrowIllegalArgumentException() {
        //Given
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenThrow(new IllegalArgumentException("Can't save shopping cart, user is null"));
        //When
        IllegalArgumentException illegalArgumentException = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerNewShoppingCart(any(User.class)));
        //Then
        assertEquals("Can't save shopping cart, user is null",
                illegalArgumentException.getMessage());
        assertEquals(IllegalArgumentException.class, illegalArgumentException.getClass());
    }
}
