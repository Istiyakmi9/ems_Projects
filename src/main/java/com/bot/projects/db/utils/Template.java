package com.bot.projects.db.utils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class Template {

    public DriverManagerDataSource getDatasource(DatabaseConfiguration databaseConfiguration) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(databaseConfiguration.getSchema() + ":" +
                databaseConfiguration.getDatabaseName() + "://" +
                databaseConfiguration.getServer() + ":" +
                databaseConfiguration.getPort() + "/" +
                databaseConfiguration.getDatabase());
        dataSource.setUsername(databaseConfiguration.getUserId());
        dataSource.setPassword(databaseConfiguration.getPassword());
        return dataSource;
    }

    public JdbcTemplate getTemplate(DatabaseConfiguration databaseConfiguration) {
        JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(getDatasource(databaseConfiguration));
        return template;
    }
}
