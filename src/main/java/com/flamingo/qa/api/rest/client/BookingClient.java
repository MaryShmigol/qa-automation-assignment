package com.flamingo.qa.api.rest.client;

import com.flamingo.qa.api.common.ApiResponse;
import com.flamingo.qa.api.rest.model.Booking;
import com.flamingo.qa.api.rest.model.CreateBookingResponse;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public final class BookingClient extends RestClientSupport {

    public ApiResponse<CreateBookingResponse> create(Booking booking) {
        Response response = given()
                .spec(requestSpec())
                .body(booking)
                .log().all()
                .when()
                .post("/booking");

        response.then().log().all();

        CreateBookingResponse responseBody =
                response.statusCode() == 200
                        ? deserialize(response, CreateBookingResponse.class)
                        : null;

        return new ApiResponse<>(
                response.statusCode(),
                responseBody,
                response.asString()
        );
    }

    public ApiResponse<Booking> get(int bookingId) {
        Response response = given()
                .spec(requestSpec())
                .when()
                .get("/booking/{id}", bookingId);

        Booking body = response.statusCode() == 200
                ? deserialize(response, Booking.class)
                : null;

        return new ApiResponse<>(
                response.statusCode(),
                body,
                response.asString()
        );
    }

    public ApiResponse<Booking> update(
            int bookingId,
            Booking booking,
            String token
    ) {
        Response response = given()
                .spec(requestSpec())
                .header("Cookie", "token=" + token)
                .body(booking)
                .when()
                .put("/booking/{id}", bookingId);

        Booking body = response.statusCode() == 200
                ? deserialize(response, Booking.class)
                : null;

        return new ApiResponse<>(
                response.statusCode(),
                body,
                response.asString()
        );
    }

    public ApiResponse<Void> delete(int bookingId, String token) {
        Response response = given()
                .spec(requestSpec())
                .header("Cookie", "token=" + token)
                .when()
                .delete("/booking/{id}", bookingId);

        return new ApiResponse<>(
                response.statusCode(),
                null,
                response.asString()
        );
    }
}