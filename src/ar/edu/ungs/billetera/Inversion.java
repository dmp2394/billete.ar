package ar.edu.ungs.billetera;

import java.time.LocalDate;

public abstract class Inversion extends Actividad {

    private static int contadorId = 1;

    protected int plazoDias;
    private int idInversion;
    protected boolean activa;

    public Inversion(String cvu, double monto, int plazoDias) {
        super(LocalDate.now(), monto, cvu);
        if (plazoDias <= 0) {
            throw new IllegalArgumentException("El plazo en días debe ser mayor a cero.");
        }

        this.plazoDias = plazoDias;
        this.idInversion = contadorId++;
        this.activa = true;
        this.fecha = Utilitarios.hoy();
    }


	public int getIdInversion() {
        return idInversion; // Retornar el ID almacenado
    }

    public int getPlazoDias() {
        return plazoDias;
    }

    public boolean estaActiva() {
        return this.activa;
    }

    public abstract double calcularResultado();

}
