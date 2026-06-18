package com.novatech.store.entity;

// Importamos las herramientas de JPA. JPA es lo que nos permite que una clase de Java
// se transforme en una tabla de la base de datos sin tener que escribir SQL a mano.
import jakarta.persistence.*;
// LocalDateTime sirve para guardar una fecha junto con la hora (ej: 2026-06-17 12:30).
import java.time.LocalDateTime;

// @Entity le avisa a Spring que esta clase es una tabla de la base de datos.
@Entity
// @Table sirve para elegir el nombre que va a tener la tabla en MySQL.
@Table(name = "Usuario")
public class Usuario {

    // @Id marca cual es la clave primaria, o sea el dato que identifica de forma unica a cada fila.
    @Id
    // @GeneratedValue con IDENTITY hace que MySQL genere el numero solo (1, 2, 3, ...).
    // Asi no tenemos que inventar el id nosotros.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column conecta este atributo de Java con la columna "id_usuario" de la tabla.
    @Column(name = "id_usuario")
    private Integer idUsuario;

    // Nombre y apellido del usuario.
    @Column(name = "nombre")
    private String nombre;

    // Correo electronico del usuario.
    @Column(name = "email")
    private String email;

    // Contrasena del usuario (en un proyecto real deberia ir encriptada).
    @Column(name = "contrasena")
    private String contrasena;

    // Rol del usuario, por ejemplo "ADMIN" o "CLIENTE".
    @Column(name = "rol")
    private String rol;

    // Fecha y hora en que el usuario se registro.
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // Constructor vacio. JPA lo necesita para poder crear objetos Usuario por su cuenta.
    public Usuario() {
    }

    // De aca para abajo van los getters y setters.
    // Un "getter" sirve para leer el valor de un atributo.
    // Un "setter" sirve para cambiar el valor de un atributo.

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
