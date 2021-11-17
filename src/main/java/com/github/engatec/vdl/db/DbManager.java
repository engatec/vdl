package com.github.engatec.vdl.db;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.VdlManager;
import com.github.engatec.vdl.core.annotation.Order;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

@Order(1)
public class DbManager extends VdlManager {

    private static final Logger LOGGER = LogManager.getLogger(DbManager.class);
    private final String url;

    private SqlSessionFactory sessionFactory;

    public DbManager(String url) {
        this.url = url;
    }

    @Override
    public void init() {
        try {
            updateSchema();
            sessionFactory = buildSqlSessionFactory();
        } catch (IOException | FlywayException e) {
            LOGGER.error("Error during DB initialization", e);
            throw new RuntimeException(e);
        }
    }

    private void updateSchema() {
        Flyway.configure()
                .dataSource(url, null, null)
                .locations("db/migrations")
                .load()
                .migrate();
    }

    private SqlSessionFactory buildSqlSessionFactory() throws IOException {
        InputStream is = Resources.getResourceAsStream("db/mybatis/mybatis-config.xml");
        Properties props = new Properties();
        props.setProperty("driver", "org.sqlite.JDBC");
        props.setProperty("url", url);
        return new SqlSessionFactoryBuilder().build(is, props);
    }

    public <R, M> CompletableFuture<R> doQueryAsync(Class<M> mapperClass, Function<M, R> func) {
        return doQueryAsync(mapperClass, func, AppExecutors.COMMON_EXECUTOR);
    }

    public <R, M> CompletableFuture<R> doQueryAsync(Class<M> mapperClass, Function<M, R> func, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            R result;
            try (SqlSession session = sessionFactory.openSession()) {
                M mapper = session.getMapper(mapperClass);
                result = func.apply(mapper);
                session.commit();
            }
            return result;
        }, executor);
    }
}
