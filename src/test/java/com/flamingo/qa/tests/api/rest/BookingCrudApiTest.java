package com.flamingo.qa.tests.api.rest;

import com.flamingo.qa.api.common.ApiResponse;
import com.flamingo.qa.api.rest.client.AuthClient;
import com.flamingo.qa.api.rest.client.BookingClient;
import com.flamingo.qa.api.rest.data.BookingFactory;
import com.flamingo.qa.api.rest.model.Booking;
import com.flamingo.qa.api.rest.model.CreateBookingResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@Tag("rest")
class BookingCrudApiTest {
    private final BookingClient bookingClient = new BookingClient();
    private final AuthClient authClient = new AuthClient();
    private final Set<Integer> bookingsToCleanup = new HashSet<>();

    private String token;

    @BeforeEach
    void authenticate() {
        token = authClient.getToken();
    }

    @AfterEach
    void cleanupCreatedBookings() {
        for (Integer bookingId : bookingsToCleanup) {
            try {
                bookingClient.delete(bookingId, token);
            } catch (RuntimeException ignored) {
                // Cleanup is best-effort: it must not hide the original test result.
            }
        }
    }

    @Test
    void createdBookingShouldBeRetrievableById() {
        Booking expectedBooking = BookingFactory.validBooking();

        ApiResponse<CreateBookingResponse> createResponse = bookingClient.create(expectedBooking);

        assertThat(createResponse.statusCode()).isEqualTo(200);
        assertThat(createResponse.body()).isNotNull();
        assertThat(createResponse.body().bookingid()).isPositive();
        assertThat(createResponse.body().booking()).usingRecursiveComparison().isEqualTo(expectedBooking);

        int bookingId = createResponse.body().bookingid();
        bookingsToCleanup.add(bookingId);

        ApiResponse<Booking> getResponse = bookingClient.get(bookingId);

        assertThat(getResponse.statusCode()).isEqualTo(200);
        assertThat(getResponse.body()).usingRecursiveComparison().isEqualTo(expectedBooking);
    }

    @Test
    void updateShouldReplaceBookingData() {
        Booking originalBooking = BookingFactory.validBooking();
        int bookingId = createBooking(originalBooking);
        Booking updatedBooking = BookingFactory.updatedFrom(originalBooking);

        ApiResponse<Booking> updateResponse = bookingClient.update(bookingId, updatedBooking, token);

        assertThat(updateResponse.statusCode()).isEqualTo(200);
        assertThat(updateResponse.body()).usingRecursiveComparison().isEqualTo(updatedBooking);

        ApiResponse<Booking> getResponse = bookingClient.get(bookingId);
        assertThat(getResponse.statusCode()).isEqualTo(200);
        assertThat(getResponse.body()).usingRecursiveComparison().isEqualTo(updatedBooking);
    }

    @Test
    void deleteShouldMakeBookingUnavailable() {
        int bookingId = createBooking(BookingFactory.validBooking());

        ApiResponse<Void> deleteResponse = bookingClient.delete(bookingId, token);

        assertThat(deleteResponse.statusCode()).isEqualTo(201);
        bookingsToCleanup.remove(bookingId);

        ApiResponse<Booking> getResponse = bookingClient.get(bookingId);
        assertThat(getResponse.statusCode()).isEqualTo(404);
        assertThat(getResponse.body()).isNull();
    }

    private int createBooking(Booking booking) {
        ApiResponse<CreateBookingResponse> response = bookingClient.create(booking);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotNull();

        int bookingId = response.body().bookingid();
        bookingsToCleanup.add(bookingId);
        return bookingId;
    }
}
