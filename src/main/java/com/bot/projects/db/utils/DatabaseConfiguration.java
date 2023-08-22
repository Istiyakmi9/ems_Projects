package com.bot.projects.db.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class DatabaseConfiguration {
    @JsonProperty("OrganizationCode")
    String organizationCode;
    @JsonProperty("Code")
    String code;
    @JsonProperty("Schema")
    String schema;
    @JsonProperty("DatabaseName")
    String databaseName;
    @JsonProperty("Server")
    String server;
    @JsonProperty("Port")
    String port;
    @JsonProperty("Database")
    String database;
    @JsonProperty("UserId")
    String userId;
    @JsonProperty("Password")
    String password;
    @JsonProperty("ConnectionTimeout")
    int connectionTimeout;
    @JsonProperty("ConnectionLifetime")
    int connectionLifetime;
    @JsonProperty("MinPoolSize")
    int minPoolSize;
    @JsonProperty("MaxPoolSize")
    int maxPoolSize;
    @JsonProperty("Pooling")
    boolean pooling;

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getConnectionLifetime() {
        return connectionLifetime;
    }

    public void setConnectionLifetime(int connectionLifetime) {
        this.connectionLifetime = connectionLifetime;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public boolean getPooling() {
        return pooling;
    }

    public void setPooling(boolean pooling) {
        this.pooling = pooling;
    }
}
