package mx.tecnm.restaurante.model;

import java.time.LocalDateTime;

public class Pedido {

    private int idPedido;
    private String cliente;
    private LocalDateTime fecha;
    private String estado;

    public Pedido() {
    }

    public Pedido(int idPedido, String cliente, LocalDateTime fecha, String estado) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return String.format("Pedido #%d | Cliente: %s | Fecha: %s | Estado: %s",
                idPedido, cliente, fecha, estado);
    }
}