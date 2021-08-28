package com.cornershop.counterstest.exceptions

/**
 * Exception when name for counter is invalid.
 */
class InvalidTitleCounterException: IllegalArgumentException()

/**
 * Exception when user tries to delete counters but has not selected anyone.
 */
class NotItemsSelectedForDeletion: IllegalArgumentException()