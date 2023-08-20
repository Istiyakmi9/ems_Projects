package com.bot.projects.db.utils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

@Component
public class Template {
    public DriverManagerDataSource getDatasource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://192.168.0.101:3306/onlinedatabuilder");
        dataSource.setUsername("istiyak");
        dataSource.setPassword("live@Bottomhalf_001");
        return dataSource;
    }

    public JdbcTemplate getTemplate() {
        JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(getDatasource());
        return template;
    }
}
