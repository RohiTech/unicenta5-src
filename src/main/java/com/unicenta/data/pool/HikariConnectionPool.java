package com.unicenta.data.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import java.sql.Connection;
import java.sql.SQLException;

public final class HikariConnectionPool {

    private static HikariDataSource ds;

    private HikariConnectionPool() {
    }

    public static synchronized void init(
            String driver,
            String url,
            String user,
            String password,
            boolean poolEnabled,
            int maximumPoolSize,
            int minimumIdle,
            long connectionTimeout,
            long idleTimeout,
            long maxLifetime,
            long keepaliveTime) {

        // Si el pool está deshabilitado no hacemos nada
        if (!poolEnabled) {
            System.out.println("HikariCP deshabilitado. Se utilizará DriverManager.");
            return;
        }

        // Ya fue inicializado
        if (ds != null) {
            return;
        }

        HikariConfig cfg = new HikariConfig();

        // Configuración JDBC
        cfg.setDriverClassName(driver);
        cfg.setJdbcUrl(url);

        if (user != null && !user.trim().isEmpty()) {
            cfg.setUsername(user);
        }

        if (password != null) {
            cfg.setPassword(password);
        }

        // Configuración del pool
        cfg.setPoolName("uniCentaPool");
        cfg.setMaximumPoolSize(maximumPoolSize);
        cfg.setMinimumIdle(minimumIdle);
        cfg.setConnectionTimeout(connectionTimeout);
        cfg.setIdleTimeout(idleTimeout);
        cfg.setMaxLifetime(maxLifetime);
        cfg.setKeepaliveTime(keepaliveTime);

        // Validación de conexión
        cfg.setValidationTimeout(5000);

        System.out.println("====================================");
        System.out.println("Inicializando HikariCP");
        System.out.println("Driver        : " + driver);
        System.out.println("URL           : " + url);
        System.out.println("Usuario       : " + user);
        System.out.println("Pool Name     : " + cfg.getPoolName());
        System.out.println("Maximum Pool  : " + maximumPoolSize);
        System.out.println("Minimum Idle  : " + minimumIdle);
        System.out.println("Connection TO : " + connectionTimeout);
        System.out.println("Idle TO       : " + idleTimeout);
        System.out.println("Max Lifetime  : " + maxLifetime);
        System.out.println("Keepalive     : " + keepaliveTime);
        System.out.println("Validation TO : 5000");
        System.out.println("====================================");

        ds = new HikariDataSource(cfg);

        printStats();
    }

    public static Connection getConnection() throws SQLException {
        if (ds == null) {
            throw new SQLException("HikariCP no ha sido inicializado.");
        }

        return ds.getConnection();
    }

    public static void shutdown() {
        if (ds != null) {
            ds.close();
            ds = null;
        }
    }

    public static void printStats() {

        if (ds == null) {
            System.out.println("HikariCP no está inicializado.");
            return;
        }

        HikariPoolMXBean pool = ds.getHikariPoolMXBean();

        System.out.println("Active : " + pool.getActiveConnections());
        System.out.println("Idle   : " + pool.getIdleConnections());
        System.out.println("Total  : " + pool.getTotalConnections());
    }
}