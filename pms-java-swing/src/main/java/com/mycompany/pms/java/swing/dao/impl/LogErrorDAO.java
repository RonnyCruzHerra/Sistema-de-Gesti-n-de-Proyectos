/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pms.java.swing.dao.impl;

/**
 *
 * @author Ronny
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogErrorDAO {

    private static final String SQL_INSERT = "INSERT INTO log_errores (mensaje, stacktrace) VALUES (?, ?)";

    public void log(String mensaje, String stacktrace) {
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setString(1, mensaje);
            ps.setString(2, stacktrace);
            ps.executeUpdate();
        } catch (SQLException ex) {
            // Si falla el log en BD, lo volcamos a stderr (no hacer más)
            ex.printStackTrace();
        }
    }
}
