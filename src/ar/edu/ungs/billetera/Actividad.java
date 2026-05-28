package ar.edu.ungs.billetera;

import java.time.LocalDate;

public abstract class Actividad {
    protected LocalDate fecha;
    protected double monto;
    private String cvu;
    protected boolean estado;

    public Actividad(LocalDate fecha, double monto, String cvu) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula.");
        }
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero.");
        }
        if (cvu == null || cvu.trim().isEmpty()) {
            throw new IllegalArgumentException("El CVU no puede estar vacío.");
        }

        this.fecha = fecha;
        this.monto = monto;
        this.cvu = cvu;
        this.estado = true; // Por defecto: aprobado
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado; // true = aprobado, false = rechazado
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getMonto() {
        return monto;
    }

    public String getCvu() {
        return cvu;
    }

    public void actualizarFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

}
