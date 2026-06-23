package com.novatech.store.controller;

import com.novatech.store.dto.ActualizarPerfilRequest;
import com.novatech.store.dto.LoginRequest;
import com.novatech.store.dto.RegistroRequest;
import com.novatech.store.dto.UsuarioResponse;
import com.novatech.store.entity.Usuario;
import com.novatech.store.repository.UsuarioRepository;
import com.novatech.store.security.JwtAuthFilter;
import com.novatech.store.security.JwtService;
import com.novatech.store.security.SecurityUtils;
import com.novatech.store.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final boolean cookieSecure;
    private final UsuarioService usuarioService;

    public AuthController(
            UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder,
            JwtService jwtService,
            UsuarioService usuarioService,
            @Value("${app.security.cookie-secure:false}") boolean cookieSecure) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.usuarioService = usuarioService;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistroRequest datos) {
        if (usuarioRepository.findByEmail(datos.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe una cuenta con ese email."));
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(datos.nombre());
        nuevo.setEmail(datos.email());
        nuevo.setContrasena(passwordEncoder.encode(datos.contrasena()));
        nuevo.setRol("CLIENTE");
        nuevo.setFechaRegistro(java.time.LocalDateTime.now());

        Usuario guardado = usuarioRepository.save(nuevo);
        UsuarioResponse response = UsuarioResponse.desde(guardado);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, crearCookieSesion(response).toString())
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest datos) {
        var usuarioOpt = usuarioRepository.findByEmail(datos.email() == null ? "" : datos.email());

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email o contrasena incorrectos."));
        }

        Usuario usuario = usuarioOpt.get();
        boolean coincide = passwordEncoder.matches(
                datos.contrasena() == null ? "" : datos.contrasena(),
                usuario.getContrasena());

        if (!coincide) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email o contrasena incorrectos."));
        }

        UsuarioResponse response = UsuarioResponse.desde(usuario);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, crearCookieSesion(response).toString())
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me() {
        UsuarioResponse u = SecurityUtils.usuarioActual();
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(u);
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(@Valid @RequestBody ActualizarPerfilRequest datos) {
        UsuarioResponse sesion = SecurityUtils.requerirAutenticado();
        Usuario actualizado = usuarioService.actualizarPerfil(sesion.idUsuario(), datos);
        UsuarioResponse response = UsuarioResponse.desde(actualizado);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, crearCookieSesion(response).toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, borrarCookieSesion().toString());
        return ResponseEntity.noContent().build();
    }

    private ResponseCookie crearCookieSesion(UsuarioResponse usuario) {
        String token = jwtService.generarToken(usuario);
        return ResponseCookie.from(JwtAuthFilter.COOKIE_NAME, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(86400)
                .build();
    }

    private ResponseCookie borrarCookieSesion() {
        return ResponseCookie.from(JwtAuthFilter.COOKIE_NAME, "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
    }
}
