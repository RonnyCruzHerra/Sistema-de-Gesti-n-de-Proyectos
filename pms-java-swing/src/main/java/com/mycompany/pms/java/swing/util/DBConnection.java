/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pms.java.swing.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {

    private static final HikariDataSource ds;

    static {
        try (InputStream is = DBConnection.class.getResourceAsStream("/config.properties")) {
            Properties props = new Properties();
            if (is == null) {
                throw new IllegalStateException("No se encontró config.properties en resources");
            }
            props.load(is);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.user"));
            config.setPassword(props.getProperty("db.password"));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.max", "10")));

            ds = new HikariDataSource(config);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Error inicializando pool de conexiones: " + e.getMessage());
        }
    }

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
