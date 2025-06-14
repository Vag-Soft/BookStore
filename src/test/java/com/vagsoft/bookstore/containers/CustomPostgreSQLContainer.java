package com.vagsoft.bookstore.containers;

import org.testcontainers.containers.PostgreSQLContainer;

/** Class for Test PostgreSQL Container configuration */
public class CustomPostgreSQLContainer extends PostgreSQLContainer<CustomPostgreSQLContainer> {
    private static final String IMAGE_VERSION = "postgres";
    private static CustomPostgreSQLContainer container;

    private CustomPostgreSQLContainer() {
        super(IMAGE_VERSION);
    }

    public static CustomPostgreSQLContainer getInstance() {
        if (container == null) {
            container = new CustomPostgreSQLContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        // do nothing, JVM handles shut down
    }
}
