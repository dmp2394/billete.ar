package ar.edu.ungs.billetera;

public class Usuario {
    private String dni;
    private String nombre;
    private String telefono;
    private String email;

    public Usuario(String dni, String nombre, String telefono, String email) {
        this.dni = dni;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    private void validarCampos() {
        if (dni == null || dni.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono no puede estar vacío.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }
        if (!email.contains("@"))
            throw new IllegalArgumentException("El email cargado debe tener el caracter @");

    }

    public void validarCamposPublico() {
        validarCampos();
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public String toString() {
        return "Usuario [dni=" + dni + ", nombre=" + nombre + ", telefono=" + telefono + ", email=" + email + "]";
    }
}
