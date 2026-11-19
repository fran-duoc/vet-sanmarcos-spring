package com.veterinaria.microservices.controller;

import com.veterinaria.microservices.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(
    origins = {"http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000", "http://127.0.0.1:3000"},
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS}
)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String username = credenciales.get("username");
        String email = credenciales.get("email");
        String password = credenciales.get("password");

        String identificador = username != null ? username.trim() : (email != null ? email.trim() : "");
        String pass = password != null ? password.trim() : "";

        // Validar credenciales de Administrador:
        // Acepta "admin" / "admin123", "admin" / "Admin123*", "admin@vetsanmarcos.cl" / "Admin123*", etc.
        boolean esAdminValido =
                ("admin".equalsIgnoreCase(identificador) && ("admin123".equals(pass) || "Admin123*".equals(pass))) ||
                ("admin@vetsanmarcos.cl".equalsIgnoreCase(identificador) && ("Admin123*".equals(pass) || "admin123".equals(pass)));

        if (esAdminValido) {
            String token = jwtUtil.generarToken(identificador, "admin");

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("accessToken", token);
            response.put("tokenType", "Bearer");
            response.put("type", "Bearer");
            response.put("username", identificador);
            response.put("nombre", "Administrador San Marcos");
            response.put("email", "admin@vetsanmarcos.cl");
            response.put("rol", "admin");
            response.put("mensaje", "Autenticación exitosa. Token Bearer generado.");

            return ResponseEntity.ok(response);
        }

        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        error.put("error", "Unauthorized");
        error.put("message", "Credenciales incorrectas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            if (jwtUtil.validarToken(token)) {
                Map<String, Object> res = new HashMap<>();
                res.put("valido", true);
                res.put("rol", "admin");
                res.put("mensaje", "Token Bearer válido");
                return ResponseEntity.ok(res);
            }
        }
        Map<String, Object> error = new HashMap<>();
        error.put("valido", false);
        error.put("status", HttpStatus.UNAUTHORIZED.value());
        error.put("mensaje", "Token Bearer inválido, ausente o expirado");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
