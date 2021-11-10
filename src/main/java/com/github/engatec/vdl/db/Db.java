package com.github.engatec.vdl.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;
import java.util.function.Function;

import com.github.engatec.vdl.core.ApplicationContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

public class Db {

    private static final Logger LOGGER = LogManager.getLogger(Db.class);
    private static final Path DB_PATH =  ApplicationContext.CONFIG_PATH.resolve("data.db");

    public static final Db INSTANCE = new Db();

    private final SqlSessionFactory sessionFactory;

    private Db() {
        try {
            updateSchema();
            this.sessionFactory = buildSqlSessionFactory();
        } catch (IOException | FlywayException e) {
            LOGGER.error("Error during DB initialization", e);
            throw new RuntimeException(e);
        }
    }

    private void updateSchema() {
        Flyway.configure()
                .dataSource("jdbc:sqlite:" + DB_PATH, null, null)
                .locations("db/migrations")
                .load()
                .migrate();
    }

    private SqlSessionFactory buildSqlSessionFactory() throws IOException {
        InputStream is = Resources.getResourceAsStream("db/mybatis/mybatis-config.xml");
        Properties props = new Properties();
        props.setProperty("driver", "org.sqlite.JDBC");
        props.setProperty("url", "jdbc:sqlite:" + DB_PATH.toString());
        return new SqlSessionFactoryBuilder().build(is, props);
    }

    public <R, M> R doQuery(Class<M> mapperClass, Function<M, R> func) {
        R result;
        try (SqlSession session = sessionFactory.openSession()) {
            M mapper = session.getMapper(mapperClass);
            result = func.apply(mapper);
            session.commit();
        }
        return result;
    }
}
