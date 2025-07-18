-- Test Schema for Component Reservation System
-- This script creates the necessary tables for integration testing.

-- Create the component_reservation table
CREATE TABLE IF NOT EXISTS component_reservation (
    reservation_id UUID PRIMARY KEY,
    item_id UUID NOT NULL,
    location_id UUID NOT NULL,
    production_run_id UUID NOT NULL,
    quantity_reserved DECIMAL(19, 4) NOT NULL CHECK (quantity_reserved > 0),
    quantity_unit VARCHAR(50) NOT NULL,
    reserved_by UUID NOT NULL,
    reserved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_reservation_status CHECK (status IN ('ACTIVE', 'CONSUMED', 'EXPIRED', 'CANCELLED')),
    CONSTRAINT chk_reservation_dates CHECK (expires_at > reserved_at),
    CONSTRAINT chk_quantity_unit CHECK (quantity_unit IN ('PIECES', 'KILOGRAMS', 'LITERS', 'METERS', 'SQUARE_METERS'))
);

-- Create the stock_level table (shared table)
CREATE TABLE IF NOT EXISTS stock_level (
    item_id UUID NOT NULL,
    location_id UUID NOT NULL,
    quantity_on_hand DECIMAL(19, 4) NOT NULL DEFAULT 0 CHECK (quantity_on_hand >= 0),
    reserved_qty DECIMAL(19, 4) NOT NULL DEFAULT 0 CHECK (reserved_qty >= 0),
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (item_id, location_id),
    CONSTRAINT chk_stock_level_reserved_qty CHECK (reserved_qty <= quantity_on_hand)
);

-- Clean up tables before each test run to ensure a clean state
DELETE FROM component_reservation;
DELETE FROM stock_level;

-- Insert test data
INSERT INTO stock_level (item_id, location_id, quantity_on_hand, reserved_qty) VALUES
    ('123e4567-e89b-12d3-a456-426614174000', '123e4567-e89b-12d3-a456-426614174001', 100.0000, 0.0000),
    ('123e4567-e89b-12d3-a456-426614174002', '123e4567-e89b-12d3-a456-426614174003', 50.0000, 0.0000),
    ('123e4567-e89b-12d3-a456-426614174004', '123e4567-e89b-12d3-a456-426614174005', 200.0000, 0.0000);

INSERT INTO component_reservation (reservation_id, item_id, location_id, production_run_id, quantity_reserved, quantity_unit, reserved_by, expires_at, status)
VALUES
    ('223e4567-e89b-12d3-a456-426614174000', '123e4567-e89b-12d3-a456-426614174000', '123e4567-e89b-12d3-a456-426614174001', '323e4567-e89b-12d3-a456-426614174000', 10.0000, 'PIECES', '423e4567-e89b-12d3-a456-426614174000', DATEADD(DAY, 1, CURRENT_TIMESTAMP), 'ACTIVE');

UPDATE stock_level SET reserved_qty = 10.0000 WHERE item_id = '123e4567-e89b-12d3-a456-426614174000' AND location_id = '123e4567-e89b-12d3-a456-426614174001';