package com.bot.projects.db.utils;

import com.bot.projects.model.DbParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LowLevelExecution {
    @Autowired
    DatabaseConfiguration databaseConfiguration;
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    private void setUpJdbc() {
        Template template = new Template();
        jdbcTemplate = template.getTemplate(databaseConfiguration);
    }

    public <T> Map<String, Object> executeProcedure(String procedureName, List<DbParameters> sqlParams) {
        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(procedureName);

        Map<String, Object> paramSet = new HashMap<>();
        for (DbParameters dbParameters : sqlParams) {
            paramSet.put(dbParameters.parameter, dbParameters.value);
            simpleJdbcCall.addDeclaredParameter(
                    new SqlParameter(
                            dbParameters.parameter,
                            dbParameters.type
                    ));
        }

        return simpleJdbcCall.execute(paramSet);
    }
}
