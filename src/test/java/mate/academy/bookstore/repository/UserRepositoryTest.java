package mate.academy.bookstore.repository;

import static mate.academy.bookstore.config.DatabaseHelper.USER_JOHN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    private static final String INVALID_EMAIL = "invlaid@test.com";
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Find user by valid email")
    @Sql(scripts = "classpath:sql/repository/user/before/add-user-to-users-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:sql/repository/user/after/remove-from-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByEmail_ValidEmail_Success() {
        User expected = USER_JOHN;
        User actual = userRepository.findByEmail(USER_JOHN.getEmail()).get();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find user by INVALID email -> get empty optional")
    @Sql(scripts = "classpath:sql/repository/user/before/add-user-to-users-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:sql/repository/user/after/remove-from-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByEmail_InvalidEmail_EmptyOptional() {
        Optional<User> actual = userRepository.findByEmail(INVALID_EMAIL);
        assertTrue(actual.isEmpty());
    }
}
