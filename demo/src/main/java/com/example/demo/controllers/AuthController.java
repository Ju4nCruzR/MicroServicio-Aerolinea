package com.example.demo.controllers;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.dto.LoginResponseDTO;
import com.example.demo.security.JwtUtil;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/v1/auth")
@Validated
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * POST /v1/auth/login
     * Autenticar administrador y generar token JWT
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            // Buscar usuario en la base de datos
            Optional<Usuario> optionalUsuario = usuarioRepository.findByUsername(loginRequest.getUsername());
            
            if (optionalUsuario.isEmpty()) {
                return ResponseEntity.status(401).body("Error: Usuario no encontrado");
            }

            Usuario usuario = optionalUsuario.get();
            
            if (!usuario.getActivo()) {
                return ResponseEntity.status(401).body("Error: Usuario inactivo");
            }

            if (!passwordEncoder.matches(loginRequest.getPassword(), usuario.getPassword())) {
                return ResponseEntity.status(401).body("Error: Contraseña incorrecta");
            }

            // Generar token JWT
            String jwt = jwtUtil.generateJwtToken(loginRequest.getUsername());

            // Preparar respuesta
            LoginResponseDTO response = new LoginResponseDTO(
                jwt, 
                loginRequest.getUsername(),
                86400000L // 24 horas en milisegundos
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    /**
     * GET /v1/auth/validate
     * Validar si el token JWT es válido (opcional)
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (jwtUtil.validateJwtToken(token)) {
                    String username = jwtUtil.getUsernameFromJwtToken(token);
                    return ResponseEntity.ok("Token válido para usuario: " + username);
                }
            }
            return ResponseEntity.status(401).body("Token inválido");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido");
        }
    }
}