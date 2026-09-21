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
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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

        assertThat(token)
                .as("Authentication token")
                .isNotBlank();
    }

    @AfterEach
    void cleanupCreatedBookings() {
        bookingsToCleanup.forEach(bookingId -> {
            try {
                bookingClient.delete(bookingId, token);
            } catch (RuntimeException ignored) {
                // Best-effort cleanup: the booking may already have been deleted.
            }
        });

        bookingsToCleanup.clear();
    }

    @Test
    void createdBookingShouldBeRetrievableById() {
        Booking expectedBooking = BookingFactory.validBooking();

        ApiResponse<CreateBookingResponse> createResponse =
                bookingClient.create(expectedBooking);

        assertThat(createResponse.statusCode())
                .as("Create booking HTTP status code")
                .isEqualTo(200);

        assertThat(createResponse.body())
                .as("Create booking response body")
                .isNotNull();

        CreateBookingResponse createdBooking = createResponse.body();
        int bookingId = createdBooking.bookingid();

        assertThat(bookingId)
                .as("Created booking ID")
                .isPositive();

        bookingsToCleanup.add(bookingId);

        ApiResponse<Booking> getResponse =
                bookingClient.get(bookingId);

        assertSoftly(softly -> {
            softly.assertThat(createdBooking.booking())
                    .as("Booking returned by create request")
                    .usingRecursiveComparison()
                    .isEqualTo(expectedBooking);

            softly.assertThat(getResponse.statusCode())
                    .as("Get booking HTTP status code")
                    .isEqualTo(200);

            softly.assertThat(getResponse.body())
                    .as("Retrieved booking")
                    .isNotNull()
                    .usingRecursiveComparison()
                    .isEqualTo(expectedBooking);
        });
    }

    @Test
    void updateShouldReplaceBookingData() {
        Booking originalBooking = BookingFactory.validBooking();

        ApiResponse<CreateBookingResponse> createResponse =
                bookingClient.create(originalBooking);

        assertThat(createResponse.statusCode())
                .as("Precondition: create booking HTTP status code")
                .isEqualTo(200);

        assertThat(createResponse.body())
                .as("Precondition: create booking response body")
                .isNotNull();

        int bookingId = createResponse.body().bookingid();

        assertThat(bookingId)
                .as("Precondition: created booking ID")
                .isPositive();

        bookingsToCleanup.add(bookingId);

        Booking updatedBooking =
                BookingFactory.updatedFrom(originalBooking);

        ApiResponse<Booking> updateResponse =
                bookingClient.update(bookingId, updatedBooking, token);

        assertSoftly(softly -> {
            softly.assertThat(updateResponse.statusCode())
                    .as("Update booking HTTP status code")
                    .isEqualTo(200);

            softly.assertThat(updateResponse.body())
                    .as("Updated booking returned by the API")
                    .isNotNull()
                    .usingRecursiveComparison()
                    .isEqualTo(updatedBooking);
        });

        ApiResponse<Booking> getResponse =
                bookingClient.get(bookingId);

        assertSoftly(softly -> {
            softly.assertThat(getResponse.statusCode())
                    .as("Get updated booking HTTP status code")
                    .isEqualTo(200);

            softly.assertThat(getResponse.body())
                    .as("Persisted booking data")
                    .isNotNull()
                    .usingRecursiveComparison()
                    .isEqualTo(updatedBooking);
        });
    }

    @Test
    void deleteShouldMakeBookingUnavailable() {
        Booking booking = BookingFactory.validBooking();

        ApiResponse<CreateBookingResponse> createResponse =
                bookingClient.create(booking);

        assertThat(createResponse.statusCode())
                .as("Precondition: create booking HTTP status code")
                .isEqualTo(200);

        assertThat(createResponse.body())
                .as("Precondition: create booking response body")
                .isNotNull();

        int bookingId = createResponse.body().bookingid();

        assertThat(bookingId)
                .as("Precondition: created booking ID")
                .isPositive();

        bookingsToCleanup.add(bookingId);

        ApiResponse<Void> deleteResponse =
                bookingClient.delete(bookingId, token);

        assertThat(deleteResponse.statusCode())
                .as("Delete booking HTTP status code")
                .isEqualTo(201);

        ApiResponse<Booking> getResponse =
                bookingClient.get(bookingId);

        assertSoftly(softly -> {
            softly.assertThat(getResponse.statusCode())
                    .as("HTTP status code after deleting the booking")
                    .isEqualTo(404);

            softly.assertThat(getResponse.body())
                    .as("Deleted booking response body")
                    .isNull();
        });

        bookingsToCleanup.remove(bookingId);
    }

}