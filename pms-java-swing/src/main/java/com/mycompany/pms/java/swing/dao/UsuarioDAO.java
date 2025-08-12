/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pms.java.swing.dao;

/**
 *
 * @author Ronny
 */
import com.mycompany.pms.java.swing.modelo.Usuario;
import com.mycompany.pms.java.swing.util.ConexionBD;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UsuarioDAO {

    private final LogErrorDAO logErrorDAO = new LogErrorDAO();

    // Create (registro) - recibe contraseña en texto claro y la guarda hasheada
    public Usuario create(Usuario usuario, String plainPassword) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, email, password_hash, rol) "
                + "VALUES (?, ?, ?, ?) RETURNING id_usuario, fecha_creacion";
        String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));

        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, hashed);
            ps.setString(4, usuario.getRol());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UUID id = (UUID) rs.getObject("id_usuario");
                    Timestamp ts = rs.getTimestamp("fecha_creacion");
                    usuario.setIdUsuario(id);
                    usuario.setFechaCreacion(ts != null ? ts.toLocalDateTime() : null);
                    usuario.setPasswordHash(hashed);
                    return usuario;
                }
            }
        } catch (SQLException ex) {
            logErrorDAO.log(ex.getMessage(), getStackTraceString(ex));
            throw ex;
        }
        return null;
    }

    // Read por email
    public Usuario findByEmail(String email) throws SQLException {
        String sql = "SELECT id_usuario, nombre, email, password_hash, rol, fecha_creacion FROM usuario WHERE email = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            logErrorDAO.log(ex.getMessage(), getStackTraceString(ex));
            throw ex;
        }
        return null;
    }

    // Read por id
    public Usuario findById(UUID id) throws SQLException {
        String sql = "SELECT id_usuario, nombre, email, password_hash, rol, fecha_creacion FROM usuario WHERE id_usuario = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException ex) {
            logErrorDAO.log(ex.getMessage(), getStackTraceString(ex));
            throw ex;
        }
        return null;
    }

    // Update (no actualiza password aquí; crea un método aparte si lo deseas)
    public boolean update(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET nombre = ?, rol = ? WHERE id_usuario = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getRol());
            ps.setObject(3, usuario.getIdUsuario());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logErrorDAO.log(ex.getMessage(), getStackTraceString(ex));
            throw ex;
        }
    }

    // Delete
    public boolean delete(UUID id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            logErrorDAO.log(ex.getMessage(), getStackTraceString(ex));
            throw ex;
        }
    }

    // List all
    public List<Usuario> listAll() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id_usuario, nombre, email, password_hash, rol, fecha_creacion FROM usuario ORDER BY fecha_creacion DESC";
        try (Connection conn = ConexionBD.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            logErrorDAO.log(ex.getMessage(), getStackTraceString(ex));
            throw ex;
        }
        return lista;
    }

    // Verificar contraseña
    public boolean verifyPassword(String email, String plainPassword) throws SQLException {
        Usuario u = findByEmail(email);
        if (u == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, u.getPasswordHash());
    }

    // Mapeador
    private Usuario mapRow(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario((UUID) rs.getObject("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRol(rs.getString("rol"));
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        u.setFechaCreacion(ts != null ? ts.toLocalDateTime() : null);
        return u;
    }

    private String getStackTraceString(Throwable t) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement e : t.getStackTrace()) {
            sb.append(e.toString()).append("\n");
        }
        return sb.toString();
    }
}
