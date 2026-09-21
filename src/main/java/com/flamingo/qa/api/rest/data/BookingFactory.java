package com.flamingo.qa.api.rest.data;

import com.flamingo.qa.api.rest.model.Booking;
import com.flamingo.qa.api.rest.model.BookingDates;

import java.time.LocalDate;
import java.util.UUID;

public final class BookingFactory {
    private BookingFactory() {
    }

    public static Booking validBooking() {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        LocalDate checkin = LocalDate.now().plusDays(7);
        LocalDate checkout = checkin.plusDays(3);

        return new Booking(
                "Maria-" + suffix,
                "Automation",
                250,
                true,
                new BookingDates(checkin.toString(), checkout.toString()),
                "Breakfast"
        );
    }

    public static Booking updatedFrom(Booking original) {
        return new Booking(
                original.firstname(),
                "Updated",
                original.totalprice() + 75,
                false,
                original.bookingdates(),
                "Late checkout"
        );
    }
}
