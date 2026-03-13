package com.vacacionesclima.infrastructure.adapter.in.rest;

import com.vacacionesclima.domain.model.ConsultaClima;
import com.vacacionesclima.domain.port.in.ConsultarClimaUseCase;
import com.vacacionesclima.domain.port.out.ClimaRepository;
import com.vacacionesclima.infrastructure.adapter.in.rest.dto.ClimaRequest;
import com.vacacionesclima.infrastructure.adapter.in.rest.dto.ClimaResponse;
import com.vacacionesclima.infrastructure.adapter.in.rest.mapper.ClimaMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

// @RestController indica que esta clase maneja
// peticiones HTTP y devuelve JSON automáticamente
@RestController

// @RequestMapping define el prefijo de todas las URLs
// Todas las rutas de este controlador empiezan con /api/clima
@RequestMapping("/api/clima")

// @CrossOrigin permite que el frontend pueda llamar
// al backend aunque estén en puertos diferentes
// origins = "*" acepta peticiones de cualquier origen
@CrossOrigin(origins = "*")
public class ClimaController {

    // Usamos el PUERTO (interfaz) no la implementación
    // Esto permite cambiar la implementación sin tocar
    // el controlador
    private final ConsultarClimaUseCase consultarClimaUseCase;
    private final ClimaMapper mapper;
    private final ClimaRepository climaRepository;

    // Inyección por constructor
    public ClimaController(ConsultarClimaUseCase consultarClimaUseCase,
                           ClimaMapper mapper,
                           ClimaRepository climaRepository) {
        this.consultarClimaUseCase = consultarClimaUseCase;
        this.mapper = mapper;
        this.climaRepository = climaRepository;
    }

    // =========================================================
    // ENDPOINT 1: Consultar clima de una ciudad
    // Método:  POST
    // URL:     /api/clima/consultar
    // Body:    { "ciudad": "Bogota" }
    // =========================================================
    @PostMapping("/consultar")
    public ResponseEntity<ClimaResponse> consultarClima(
            @Valid @RequestBody ClimaRequest request) {

        try {
            // Llamar al caso de uso con la ciudad recibida
            ConsultaClima resultado = consultarClimaUseCase
                    .consultarClima(request.getCiudad().trim());

            // Convertir resultado del dominio a DTO de respuesta
            ClimaResponse response = mapper.toResponse(resultado);

            // HTTP 200 OK
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Si la ciudad no existe o hay error en la API
            ClimaResponse error = new ClimaResponse();
            error.setExitoso(false);
            error.setMensaje("No se pudo obtener el clima de: "
                    + request.getCiudad()
                    + ". Verifica que el nombre sea correcto.");

            // HTTP 404 Not Found
            return ResponseEntity.status(404).body(error);
        }
    }

    @PostMapping("/guardar")
    public ResponseEntity<ClimaResponse> guardar(
            @RequestBody ClimaRequest request) {
        try {
            ConsultaClima consulta = new ConsultaClima();
            consulta.setCiudad(request.getCiudad());
            climaRepository.guardar(consulta);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // =========================================================
    // ENDPOINT 2: Obtener historial de consultas
    // Método:  GET
    // URL:     /api/clima/historial
    // =========================================================
    @GetMapping("/historial")
    public ResponseEntity<List<ClimaResponse>> obtenerHistorial() {

        List<ClimaResponse> historial = consultarClimaUseCase
                .obtenerHistorial()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(historial);
    }

    // =========================================================
    // ENDPOINT 3: Buscar consultas por ciudad
    // Método:  GET
    // URL:     /api/clima/ciudad/{nombre}
    // Ejemplo: /api/clima/ciudad/Bogota
    // =========================================================
    @GetMapping("/ciudad/{nombre}")
    public ResponseEntity<List<ClimaResponse>> buscarPorCiudad(
            @PathVariable String nombre) {

        List<ClimaResponse> consultas = consultarClimaUseCase
                .buscarPorCiudad(nombre)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(consultas);
    }

    // =========================================================
    // ENDPOINT 4: Verificar que el servidor está funcionando
    // Método:  GET
    // URL:     /api/clima/ping
    // =========================================================
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok(
            "Vacaciones-Clima funcionando OK ✅"
        );
    }
}
