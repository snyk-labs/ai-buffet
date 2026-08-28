package aibuffet.realworld;

import aibuffet.realworld.booking.Booking;
import aibuffet.realworld.booking.BookingService;
import dev.langchain4j.agent.tool.Tool;

/**
 * Booking tools exposed to the customer support agent.
 *
 * @see <a href="https://github.com/langchain4j/langchain4j-examples/blob/main/customer-support-agent-example/src/main/java/dev/langchain4j/example/BookingTools.java">BookingTools.java</a>
 */
public class BookingTools {

    private final BookingService bookingService;

    public BookingTools(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Tool
    public Booking getBookingDetails(String bookingNumber, String customerName, String customerSurname) {
        return bookingService.getBookingDetails(bookingNumber, customerName, customerSurname);
    }

    @Tool
    public void cancelBooking(String bookingNumber, String customerName, String customerSurname) {
        bookingService.cancelBooking(bookingNumber, customerName, customerSurname);
    }
}
