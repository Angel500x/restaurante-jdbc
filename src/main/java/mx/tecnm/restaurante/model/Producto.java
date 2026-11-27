package mx.tecnm.restaurante.model;

import java.math.BigDecimal;

public class Producto {

    private int idProducto;
    private String nombre;
    private BigDecimal precio;
    private boolean disponible;

    public Producto() {
    }

    public Producto(int idProducto, String nombre, BigDecimal precio, boolean disponible) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.disponible = disponible;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return String.format("ID: %-3d | %-35s | $%-8.2f | Disponible: %s",
                idProducto, nombre, precio, disponible ? "Sí" : "No");
    }
}