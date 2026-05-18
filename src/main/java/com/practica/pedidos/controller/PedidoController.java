package com.examen.pedidos.controller;

import com.examen.pedidos.dto.EstadoUpdateDTO;
import com.examen.pedidos.dto.PedidoRequestDTO;
import com.examen.pedidos.dto.PedidoResponseDTO;
import com.examen.pedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crear(@Valid @RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoUpdateDTO dto) {
        return ResponseEntity.ok(service.actualizarEstado(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
