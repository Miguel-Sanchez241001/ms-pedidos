package com.examen.pedidos.controller;

import com.examen.pedidos.dto.EstadoUpdateDTO;
import com.examen.pedidos.dto.PedidoRequestDTO;
import com.examen.pedidos.dto.PedidoResponseDTO;
import com.examen.pedidos.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Pedidos", description = "Operaciones de gestión de pedidos. El total se calcula automáticamente en el backend.")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @Operation(summary = "Crear pedido", description = "Registra un nuevo pedido. El total se calcula automáticamente: cantidad × precioUnitario")
    @ApiResponse(responseCode = "201", description = "Pedido creado con total calculado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crear(@Valid @RequestBody PedidoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @Operation(summary = "Listar pedidos", description = "Retorna todos los pedidos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de pedidos")
    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @Operation(summary = "Buscar por ID", description = "Retorna un pedido según su identificador")
    @ApiResponse(responseCode = "200", description = "Pedido encontrado")
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(summary = "Actualizar estado", description = "Actualiza únicamente el estado del pedido. Estados válidos: REGISTRADO, PAGADO, ENVIADO, CANCELADO")
    @ApiResponse(responseCode = "200", description = "Estado actualizado")
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PedidoResponseDTO> actualizarEstado(
            @PathVariable Long id,
            @Valid @RequestBody EstadoUpdateDTO dto) {
        return ResponseEntity.ok(service.actualizarEstado(id, dto));
    }

    @Operation(summary = "Eliminar pedido", description = "Eliminación lógica: cambia estado a CANCELADO")
    @ApiResponse(responseCode = "204", description = "Pedido cancelado")
    @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
