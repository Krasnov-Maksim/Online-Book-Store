package mate.academy.bookstore.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IsbnValidator implements ConstraintValidator<Isbn, String> {
    private static final Pattern NOT_DIGITS = Pattern.compile("[\\D]");
    private static final int Isbn10Length = 10;
    private static final int Isbn13Length = 13;

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null) {
            return true;
        }
        String digitsOnly = NOT_DIGITS.matcher(isbn).replaceAll("");
        if (!(digitsOnly.length() == Isbn10Length || digitsOnly.length() == Isbn13Length)) {
            return false;
        }
        return isValidChecksum(digitsOnly);
    }

    private boolean isValidChecksum(String isbn) {
        final int length = isbn.length();
        return switch (length) {
            case Isbn10Length -> checkChecksumIsbn10(isbn);
            case Isbn13Length -> checkChecksumIsbn13(isbn);
            default -> false;
        };
    }

    /**
     * Check the digits for ISBN 10 using algorithm from
     * <a href="https://en.wikipedia.org/wiki/International_Standard_Book_Number#ISBN-10_check_digits">Wikipedia</a>.
     */
    private boolean checkChecksumIsbn10(String isbn) {
        int sum = 0;
        for (int i = 0; i < isbn.length() - 1; i++) {
            sum += (isbn.charAt(i) - '0') * (Isbn10Length - i);
        }
        sum += isbn.charAt(9) == 'X' ? Isbn10Length : isbn.charAt(9) - '0';
        return (sum % 11) == 0;
    }

    /**
     * Check the digits for ISBN 13 using algorithm from
     * <a href="https://en.wikipedia.org/wiki/International_Standard_Book_Number#ISBN-13_check_digit_calculation">Wikipedia</a>.
     */
    private boolean checkChecksumIsbn13(String isbn) {
        int sum = 0;
        for (int i = 0; i < isbn.length(); i++) {
            sum += (isbn.charAt(i) - '0') * (i % 2 == 0 ? 1 : 3);
        }
        return (sum % Isbn10Length) == 0;
    }
}
