package com.novatech.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Esta es la clase principal: el punto donde arranca toda la aplicacion.
// @SpringBootApplication activa la "magia" de Spring Boot:
// busca los controllers, services y repositorios y los conecta solo.
@SpringBootApplication
public class BackendApplication {

    // El metodo main es lo primero que se ejecuta cuando prendemos el backend.
    public static void main(String[] args) {
        // Esta linea levanta el servidor web y deja todo listo para recibir pedidos.
        SpringApplication.run(BackendApplication.class, args);
    }
}
