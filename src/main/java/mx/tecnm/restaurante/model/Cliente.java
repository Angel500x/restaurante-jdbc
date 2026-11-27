package mx.tecnm.restaurante.model;

import java.time.LocalDate;

public class Cliente {

    private int idCliente;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;
    private LocalDate fechaRegistro;

    public Cliente() {
    }

    // Constructor para registrar un nuevo cliente (sin ID ni fecha, los genera PostgreSQL)
    public Cliente(String nombre, String apellido, String telefono, String correo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }

    // Constructor completo
    public Cliente(int idCliente, String nombre, String apellido, String telefono, String correo, LocalDate fechaRegistro) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return String.format("Cliente [ID: %d | %s %s | Tel: %s | Correo: %s | Registro: %s]",
                idCliente, nombre, apellido, telefono, correo, fechaRegistro);
    }
}