/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.pms.java.swing.dao;

import com.mycompany.pms.java.swing.modelo.Usuario;

/**
 *
 * @author Ronny
 */
public class TestUsuarioDAO {

    public static void main(String[] args) {
        UsuarioDAO dao = new UsuarioDAO();
        try {
            Usuario u = new Usuario();
            u.setNombre("Ronny Cruz");
            u.setEmail("ronny@example.com");
            u.setRol("ADMIN");
            dao.create(u, "MiPassSegura123!");
            System.out.println("Creado: " + u);

            boolean ok = dao.verifyPassword("ronny@example.com", "MiPassSegura123!");
            System.out.println("Verificación: " + ok);

        } catch (Exception e) {
            new LogErrorDAO().log(e.getMessage(), e.toString());
            e.printStackTrace();
        }
    }
}
