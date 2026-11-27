package mx.tecnm.restaurante.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import mx.tecnm.restaurante.dao.PedidoDAO;
import mx.tecnm.restaurante.model.DetallePedido;
import mx.tecnm.restaurante.model.Pedido;

public class PedidoService {

    private final PedidoDAO pedidoDAO;

    public PedidoService() {
        this.pedidoDAO = new PedidoDAO();
    }

    public int crearPedidoCompleto(int idCliente, List<DetallePedido> detalles) throws SQLException {
        if (idCliente <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser válido.");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("No se puede registrar un pedido sin productos.");
        }
        return pedidoDAO.registrarPedido(idCliente, detalles);
    }

    public List<Pedido> listarPedidos() throws SQLException {
        return pedidoDAO.obtenerPedidos();
    }

    public BigDecimal calcularTotalPedido(int idPedido) throws SQLException {
        return pedidoDAO.obtenerTotal(idPedido);
    }

    public void actualizarEstadoPedido(int idPedido, String nuevoEstado) throws SQLException {
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new IllegalArgumentException("El estado no puede estar vacío.");
        }
        pedidoDAO.cambiarEstado(idPedido, nuevoEstado.trim().toUpperCase());
    }

    public List<String> obtenerHistorial(int idPedido) throws SQLException {
        return pedidoDAO.obtenerHistorialEstado(idPedido);
    }
}