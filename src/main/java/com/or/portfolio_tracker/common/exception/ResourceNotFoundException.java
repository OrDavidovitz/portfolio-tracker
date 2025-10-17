package com.or.portfolio_tracker.common.exception;

/**
 * Exception thrown when a requested resource (e.g. Asset, Holding, etc.)
 * cannot be found in the system.
 *
 * This maps to HTTP 404 (Not Found) via the {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Creates a new ResourceNotFoundException with a custom message.
     *
     * @param message description of the missing resource (e.g. "Asset id 1 not found")
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}