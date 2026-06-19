package com.novatech.store.entity;

// Importamos las herramientas de JPA. JPA es lo que nos permite que una clase de Java
// se transforme en una tabla de la base de datos sin tener que escribir SQL a mano.
import jakarta.persistence.*;
// Anotaciones de Bean Validation (Jakarta) para validar los datos de entrada.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// @JsonProperty nos deja controlar como se convierte este objeto a JSON.
import com.fasterxml.jackson.annotation.JsonProperty;
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

    // Nombre y apellido del usuario. Es obligatorio y no puede ir vacio.
    @NotBlank(message = "El nombre es obligatorio.")
    @Column(name = "nombre")
    private String nombre;

    // Correo electronico del usuario. Es obligatorio y debe tener formato de email valido.
    @NotBlank(message = "El email es obligatorio.")
    @Email(message = "El email no tiene un formato valido.")
    @Column(name = "email")
    private String email;

    // Contrasena del usuario. Se guarda ENCRIPTADA (hasheada con BCrypt), nunca en texto plano.
    // @JsonProperty(access = WRITE_ONLY) significa: este campo se puede RECIBIR desde el
    // frontend (cuando alguien se registra o cambia la clave), pero NUNCA se ENVIA en las
    // respuestas JSON. Asi, aunque pidamos GET /usuarios, la contrasena jamas se filtra.
    // Es obligatoria al crear el usuario (no puede ir vacia) y debe tener al menos 6 caracteres.
    // OJO: esto se valida solo en el POST (crear). El PUT de edicion NO usa @Valid,
    // asi que sigue permitiendo editar sin mandar la contrasena (se conserva la anterior).
    @NotBlank(message = "La contrasena es obligatoria.")
    @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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
