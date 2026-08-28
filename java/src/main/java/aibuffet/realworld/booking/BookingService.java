package aibuffet.realworld.booking;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * In-memory booking store from the LangChain4j customer support agent example.
 *
 * @see <a href="https://github.com/langchain4j/langchain4j-examples/blob/main/customer-support-agent-example/src/main/java/dev/langchain4j/example/booking/BookingService.java">BookingService.java</a>
 */
public class BookingService {

    private static final Customer CUSTOMER = new Customer("John", "Doe");
    private static final String BOOKING_NUMBER = "MS-777";
    private static final Booking BOOKING = new Booking(
            BOOKING_NUMBER,
            LocalDate.of(2025, 12, 13),
            LocalDate.of(2025, 12, 31),
            CUSTOMER
    );

    private final Map<String, Booking> bookings = new HashMap<>() {{
        put(BOOKING_NUMBER, BOOKING);
    }};

    public Booking getBookingDetails(String bookingNumber, String customerName, String customerSurname) {
        ensureExists(bookingNumber, customerName, customerSurname);
        return bookings.get(bookingNumber);
    }

    public void cancelBooking(String bookingNumber, String customerName, String customerSurname) {
        ensureExists(bookingNumber, customerName, customerSurname);
        bookings.remove(bookingNumber);
    }

    private void ensureExists(String bookingNumber, String customerName, String customerSurname) {
        Booking booking = bookings.get(bookingNumber);
        if (booking == null) {
            throw new BookingNotFoundException(bookingNumber);
        }

        Customer customer = booking.customer();
        if (!customer.name().equals(customerName)) {
            throw new BookingNotFoundException(bookingNumber);
        }
        if (!customer.surname().equals(customerSurname)) {
            throw new BookingNotFoundException(bookingNumber);
        }
    }
}
