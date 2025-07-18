package ai.shreds.application.ports;

import ai.shreds.application.dtos.ApplicationStockLevelUpdatedDTO;

/**
 * Input port for handling inbound stock level update messages.
 */
public interface ApplicationHandleStockLevelUpdatedInputPort {

    /**
     * Handle an updated stock level event by validating existing reservations.
     *
     * @param message the stock level updated DTO
     */
    void handleStockLevelUpdated(ApplicationStockLevelUpdatedDTO message);
}
