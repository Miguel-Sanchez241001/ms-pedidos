package com.examen.pedidos.service;

import com.examen.pedidos.dto.EstadoUpdateDTO;
import com.examen.pedidos.dto.PedidoRequestDTO;
import com.examen.pedidos.dto.PedidoResponseDTO;
import com.examen.pedidos.entity.EstadoPedido;
import com.examen.pedidos.entity.Pedido;
import com.examen.pedidos.exception.PedidoNotFoundException;
import com.examen.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PedidoService {

    private final PedidoRepository repository;

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    public PedidoResponseDTO crear(PedidoRequestDTO dto) {
        Pedido pedido = new Pedido();
        pedido.setCliente(dto.getCliente());
        pedido.setCorreoCliente(dto.getCorreoCliente());
        pedido.setProductoId(dto.getProductoId());
        pedido.setNombreProducto(dto.getNombreProducto());
        pedido.setCantidad(dto.getCantidad());
        pedido.setPrecioUnitario(dto.getPrecioUnitario());
        pedido.setTotal(dto.getPrecioUnitario().multiply(BigDecimal.valueOf(dto.getCantidad())));
        pedido.setEstado(EstadoPedido.REGISTRADO);
        return toResponse(repository.save(pedido));
    }

    public List<PedidoResponseDTO> listar() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
        return toResponse(pedido);
    }

    public PedidoResponseDTO actualizarEstado(Long id, EstadoUpdateDTO dto) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
        pedido.setEstado(dto.getEstado());
        return toResponse(repository.save(pedido));
    }

    public void eliminar(Long id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
        pedido.setEstado(EstadoPedido.CANCELADO);
        repository.save(pedido);
    }

    private PedidoResponseDTO toResponse(Pedido pedido) {
        PedidoResponseDTO response = new PedidoResponseDTO();
        response.setId(pedido.getId());
        response.setCliente(pedido.getCliente());
        response.setCorreoCliente(pedido.getCorreoCliente());
        response.setProductoId(pedido.getProductoId());
        response.setNombreProducto(pedido.getNombreProducto());
        response.setCantidad(pedido.getCantidad());
        response.setPrecioUnitario(pedido.getPrecioUnitario());
        response.setTotal(pedido.getTotal());
        response.setEstado(pedido.getEstado());
        response.setFechaPedido(pedido.getFechaPedido());
        return response;
    }
}
