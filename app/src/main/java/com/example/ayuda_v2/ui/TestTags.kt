package com.example.ayuda_v2.ui

/**
 * Objeto centralizado de constantes para Test Tags.
 * Evita errores tipográficos y facilita mantenimiento
 * al usar los mismos tags en UI y tests.
 *
 * Recomendación del profesor aplicada.
 */
object TestTags {
    // ServicesScreen
    const val SCREEN_SERVICES = "screen_services"
    const val BTN_MY_BOOKINGS = "btn_my_bookings"
    const val CATEGORY_FILTER = "category_filter"
    const val SERVICE_CARD_PREFIX = "service_card_"

    // BookingFormScreen
    const val SCREEN_BOOKING_FORM = "screen_booking_form"
    const val INPUT_NAME = "input_name"
    const val INPUT_PHONE = "input_phone"
    const val INPUT_ADDRESS = "input_address"
    const val INPUT_NOTES = "input_notes"
    const val DATE_PICKER = "date_picker"
    const val TIME_PICKER = "time_picker"
    const val BTN_CONFIRM_BOOKING = "btn_confirm_booking"
    const val BTN_BACK = "btn_back"

    // MyBookingsScreen
    const val SCREEN_MY_BOOKINGS = "screen_my_bookings"
    const val BOOKING_CARD_PREFIX = "booking_card_"
    const val BTN_COMPLETE_PREFIX = "btn_complete_"
    const val BTN_CANCEL_PREFIX = "btn_cancel_"
    const val EMPTY_STATE = "empty_state"

    // General
    const val LOADING_INDICATOR = "loading_indicator"
    const val ERROR_MESSAGE = "error_message"

    // Helper functions
    fun serviceCard(serviceId: String) = "$SERVICE_CARD_PREFIX$serviceId"
    fun bookingCard(bookingId: String) = "$BOOKING_CARD_PREFIX$bookingId"
    fun btnComplete(bookingId: String) = "$BTN_COMPLETE_PREFIX$bookingId"
    fun btnCancel(bookingId: String) = "$BTN_CANCEL_PREFIX$bookingId"
}

