package com.novatech.store.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Esta clase de configuracion solo sirve para crear UNA herramienta y dejarla
// disponible para toda la app: el "encriptador" de contrasenas.
// @Configuration le avisa a Spring que aca adentro hay objetos que tiene que
// crear y guardar para prestarselos a quien los necesite (inyeccion de dependencias).
/**
 * Configuración Spring `PasswordConfig`: beans, seguridad, seeders o ajustes de arranque.
 */
@Configuration
public class PasswordConfig {

    // @Bean significa: "Spring, crea este objeto una sola vez y guardalo".
    // Despues, cualquier clase (por ejemplo el AuthController o el UsuarioService)
    // puede pedir un BCryptPasswordEncoder en su constructor y Spring le pasa ESTE.
    //
    // BCrypt es un algoritmo que transforma una contrasena (ej: "admin123") en un
    // texto largo e irreconocible (un "hash"). Nunca se puede volver atras para
    // saber la contrasena original. Para chequear el login, BCrypt vuelve a aplicar
    // el mismo proceso y compara, asi nunca guardamos la contrasena en texto plano.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
