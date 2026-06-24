package com.novatech.store.dto;

import com.novatech.store.entity.Usuario;

// DTO que devolvemos al frontend cuando alguien se registra o inicia sesion.
// Importante: aca NO existe el campo "contrasena", asi nos aseguramos de que la
// clave (ni siquiera el hash) salga nunca en las respuestas del login/registro.
/**
 * DTO `UsuarioResponse`: objeto de transferencia (entrada/salida) sin mapeo directo 1:1 a tabla.
 */
public record UsuarioResponse(
        Integer idUsuario,
        String nombre,
        String email,
        String rol) {

    // Metodo de ayuda: toma un Usuario completo (el de la base de datos) y arma
    // la version "segura" para mostrar, copiando solo los datos que se pueden ver.
    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol());
    }
}
