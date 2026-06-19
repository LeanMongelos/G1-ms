package com.novatech.store.controller;

import com.novatech.store.dto.LoginRequest;
import com.novatech.store.dto.RegistroRequest;
import com.novatech.store.dto.UsuarioResponse;
import com.novatech.store.entity.Usuario;
import com.novatech.store.repository.UsuarioRepository;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

// Controller de autenticacion (login simple).
// Todas las rutas empiezan con /auth.
// OJO: esto NO usa spring-boot-starter-security, asi que no bloquea ninguna ruta.
// Es un login "casero": validamos email + contrasena a mano y devolvemos los datos del usuario.
@RestController
@RequestMapping("/auth")
public class AuthController {

    // Repositorio para buscar/guardar usuarios en la base de datos.
    private final UsuarioRepository usuarioRepository;
    // Herramienta de BCrypt para encriptar y comparar contrasenas.
    private final BCryptPasswordEncoder passwordEncoder;

    // Spring nos pasa solo estas dos herramientas (inyeccion de dependencias).
    public AuthController(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================================================
    //  POST /auth/register  -> registrar un cliente nuevo
    //  Body esperado: { "nombre": "...", "email": "...", "contrasena": "..." }
    // ============================================================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistroRequest datos) {
        // 1) Validacion basica: que vengan los datos minimos.
        if (datos.email() == null || datos.email().isBlank()
                || datos.contrasena() == null || datos.contrasena().isBlank()
                || datos.nombre() == null || datos.nombre().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Faltan datos: nombre, email y contrasena son obligatorios."));
        }

        // 2) Si ya existe un usuario con ese email, no dejamos registrar de nuevo.
        //    Devolvemos 409 (CONFLICT) con un mensaje claro.
        if (usuarioRepository.findByEmail(datos.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe una cuenta con ese email."));
        }

        // 3) Armamos el usuario nuevo con rol CLIENTE y la contrasena HASHEADA con BCrypt.
        Usuario nuevo = new Usuario();
        nuevo.setNombre(datos.nombre());
        nuevo.setEmail(datos.email());
        nuevo.setContrasena(passwordEncoder.encode(datos.contrasena())); // se guarda encriptada
        nuevo.setRol("CLIENTE"); // todo el que se registra desde la tienda es cliente
        nuevo.setFechaRegistro(java.time.LocalDateTime.now());

        Usuario guardado = usuarioRepository.save(nuevo);

        // 4) Respondemos 201 (CREATED) con los datos del usuario SIN la contrasena.
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.desde(guardado));
    }

    // ============================================================
    //  POST /auth/login  -> iniciar sesion
    //  Body esperado: { "email": "...", "contrasena": "..." }
    // ============================================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest datos) {
        // 1) Buscamos el usuario por su email.
        var usuarioOpt = usuarioRepository.findByEmail(datos.email() == null ? "" : datos.email());

        // 2) Si no existe el email, NO decimos "el email no existe" (eso ayudaria a un atacante).
        //    Respondemos 401 con un mensaje generico.
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email o contrasena incorrectos."));
        }

        Usuario usuario = usuarioOpt.get();

        // 3) Comparamos la contrasena escrita contra el hash guardado.
        //    passwordEncoder.matches() hace todo el trabajo de BCrypt por nosotros.
        boolean coincide = passwordEncoder.matches(
                datos.contrasena() == null ? "" : datos.contrasena(),
                usuario.getContrasena());

        if (!coincide) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email o contrasena incorrectos."));
        }

        // 4) Login correcto: devolvemos los datos del usuario (sin la contrasena).
        return ResponseEntity.ok(UsuarioResponse.desde(usuario));
    }
}
