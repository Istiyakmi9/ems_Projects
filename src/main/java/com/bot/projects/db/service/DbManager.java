package com.bot.projects.db.service;

import com.bot.projects.db.utils.Template;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DbManager {

    @Autowired
    DbUtils dbUtils;

    @Autowired
    ObjectMapper mapper;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    DbManager(Template template) {
        jdbcTemplate = template.getTemplate();
    }

    public <T> void save(T instance) throws Exception {
        String query = dbUtils.save(instance);
        jdbcTemplate.execute(query);
    }

    public <T> void saveAll(List<T> instance, Class<T> type) throws Exception {
        String query = dbUtils.saveAll(instance, type);
        jdbcTemplate.execute(query);
    }

    public <T> int nextIntPrimaryKey(Class<T> instance) throws Exception {
        int index = 0;
        String lastIndexQuery = dbUtils.lastPrimaryKey(instance);
        try {
            String lastIndex = jdbcTemplate.queryForObject(lastIndexQuery, String.class);
            if (lastIndex != null && !lastIndex.isEmpty()) {
                index = Integer.parseInt(lastIndex);
            }
        } catch (EmptyResultDataAccessException e) {
            index = 0;
        }

        return index + 1;
    }

    public <T> long nextLongPrimaryKey(Class<T> instance) throws Exception {
        long index = 0;
        String lastIndexQuery = dbUtils.lastPrimaryKey(instance);
        String lastIndex = jdbcTemplate.queryForObject(lastIndexQuery, String.class);
        if (lastIndex != null && !lastIndex.isEmpty()) {
            index = Long.parseLong(lastIndex);
        }

        return index + 1;
    }

    public <T> List<T> get(Class<T> type) throws Exception {
        String query = dbUtils.get(type);
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);
        return mapper.convertValue(result, new TypeReference<List<T>>() {});
    }

    public <T> T getById(long id, Class<T> type) throws Exception {
        String query = dbUtils.getById(id, type);
        Map<String, Object> result = jdbcTemplate.queryForMap(query);
        return mapper.convertValue(result, type);
    }

    public <T> T getById(int id, Class<T> type) throws Exception {
        String query = dbUtils.getById(id, type);
        Map<String, Object> result = jdbcTemplate.queryForMap(query);
        return mapper.convertValue(result, type);
    }

    public <T> T queryRaw(String query, Class<T> type) throws Exception {
        Map<String, Object> result = jdbcTemplate.queryForMap(query);
        return mapper.convertValue(result, type);
    }

    public <T> List<T> queryList(String query, Class<T> type) throws Exception {
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);
        return mapper.convertValue(result, new TypeReference<List<T>>() {});
    }
}
