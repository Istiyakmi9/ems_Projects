package com.bot.projects.db.service;

import com.bot.projects.db.annotations.Column;
import com.bot.projects.db.annotations.Id;
import com.bot.projects.db.annotations.Table;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@RequestScope
public class DbUtils {
    public <T> String save(T instance) throws Exception {
        String tableName = getTableName(instance);
        String primayKey = getPrimaryKey(instance);

        HashMap<String, Field> fields = getFields(instance);
        StringBuilder columnsName = new StringBuilder();
        columnsName.append("INSERT INTO ").append(tableName).append("(");

        StringBuilder columnsValue = new StringBuilder();
        columnsValue.append("VALUES(");

        StringBuilder updateStatement = new StringBuilder();

        try {
            String delimiter = "";
            for (Map.Entry<String, Field> field : fields.entrySet()) {
                field.getValue().setAccessible(true);
                Object value = field.getValue().get(instance);
                Class<?> type = field.getValue().getType();

                columnsName.append(delimiter + field.getKey());

                if (type == String.class) {
                    if (value != null){
                        value = "'" + value + "'";
                    }
                } else if (type == Date.class) {
                    if (value != null){
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        value = "'" + formatter.format(value) + "'";
                    }
                }

                columnsValue.append(delimiter + value);
                if(!field.getKey().equals(primayKey)) {
                    updateStatement.append(delimiter + field.getKey() + " = " + value);
                }

                delimiter = ",";
            }

            columnsName.append(") ");
            columnsValue.append(") ON DUPLICATE KEY UPDATE ");
        } catch (IllegalAccessException ex) {
            throw new Exception(ex.getMessage());
        }

        return columnsName.toString() + columnsValue.toString() + updateStatement.toString();
    }

    public <T> String saveAll(List<T> instances, Class<T> classType) throws Exception {
        T instanceType = classType.getDeclaredConstructor().newInstance();
        String tableName = getTableName(instanceType);
        String primayKey = getPrimaryKey(instanceType);

        HashMap<String, Field> fields = getFields(instanceType);

        StringBuilder columnsName = new StringBuilder();
        StringBuilder columnsValue = new StringBuilder();
        StringBuilder updateStatement = new StringBuilder();

        try {
            String delimiter = "";
            String rowDelimiter = "";
            int i = 0;
            for (T instance : instances) {
                columnsValue.append(rowDelimiter + "(");
                delimiter = "";

                for (Map.Entry<String, Field> field : fields.entrySet()) {
                    field.getValue().setAccessible(true);
                    Object value = field.getValue().get(instance);
                    Class<?> type = field.getValue().getType();

                    if (type == String.class) {
                        if (value != null){
                            value = "'" + value + "'";
                        }
                    } else if (type == Date.class) {
                        if (value != null){
                            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            value = "'" + formatter.format(value) + "'";
                        }
                    }

                    columnsValue.append(delimiter + value);

                    if (i == 0) {
                        columnsName.append(delimiter + field.getKey());
                        if(!field.getKey().equals(primayKey)) {
                            updateStatement.append(delimiter + field.getKey() + " = VALUES(" + field.getKey() + ")");
                        }
                    }

                    delimiter = ",";
                }

                columnsValue.append(")");
                rowDelimiter = ",";
                i++;
            }
        } catch (IllegalAccessException ex) {
            throw new Exception(ex.getMessage());
        }

        return "INSERT INTO " + tableName + "(" + columnsName + ") VALUES " + columnsValue + " ON DUPLICATE KEY UPDATE " + updateStatement;
    }

    public <T> String lastPrimaryKey(Class<T> type) throws Exception {
        T instance = type.getDeclaredConstructor().newInstance();
        String primaryKeyName = getPrimaryKey(instance);
        String tableName = getTableName(instance);
        return "select " + primaryKeyName + " from " + tableName + " Order by " + primaryKeyName + " desc limit 1";
    }

    public <T> String get(Class<T> type) throws Exception {
        var instance = type.getDeclaredConstructor().newInstance();
        String tableName = getTableName(instance);
        return createSelectStatement(instance, tableName);
    }

    public <T> String getById(long id, Class<T> type) throws Exception {
        var instance = type.getDeclaredConstructor().newInstance();
        String primaryKey = getPrimaryKey(instance);
        String tableName = getTableName(instance);

        return createSelectStatement(instance, tableName) + " WHERE " + primaryKey + " = " + id;
    }

    public <T> String getById(int id, Class<T> type) throws Exception {
        var instance = type.getDeclaredConstructor().newInstance();
        String primaryKey = getPrimaryKey(instance);
        String tableName = getTableName(instance);

        return createSelectStatement(instance, tableName) + " WHERE " + primaryKey + " = " + id;
    }

    public <T> String getPrimaryKey(T instance) throws Exception {
        String primaryKey = null;
        Field[] fields = instance.getClass().getDeclaredFields();
        for(Field field : fields) {
            if (field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(Column.class)) {
                Column column = (Column) field.getAnnotation(Column.class);
                primaryKey = column.name();
                break;
            }
        }

        if(primaryKey == null) {
            throw new Exception("Modal does not have primary key declared.");
        }

        return primaryKey;
    }

    private <T> String getTableName(T instance) throws Exception {
        String tableName = null;
        var annotations = instance.getClass().getAnnotations();
        for (var annotation : annotations) {
            if(annotation.annotationType().equals(Table.class)) {
                tableName = ((Table)annotation).name();
                break;
            }
        }

        if(tableName == null) {
            throw new Exception("Modal does not have table name declared.");
        }

        return tableName;
    }

    private <T> String createSelectStatement(T instance, String tableName) throws Exception {
        HashMap<String, Field> fields = getFields(instance);
        StringBuilder columnsName = new StringBuilder();
        columnsName.append("SELECT ");

        try {
            String delimiter = "";
            for (Map.Entry<String, Field> field : fields.entrySet()) {
                columnsName.append(delimiter + field.getKey());
                delimiter = ",";
            }

            columnsName.append(" FROM " + tableName);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }

        return columnsName.toString();
    }

    private <T> HashMap<String, Field> getFields(T instance) {
        Optional<Column> annotation;
        HashMap<String, Field> fieldsCollection = new HashMap<>();
        Field[] fields = instance.getClass().getDeclaredFields();

        for (Field field : fields) {
            annotation = Arrays.stream(field.getAnnotationsByType(Column.class)).findFirst();
            annotation.ifPresent(column -> fieldsCollection.put(column.name(), field));
        }

        return fieldsCollection;
    }
}
