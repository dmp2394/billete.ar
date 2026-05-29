package ar.edu.ungs.billetera;

//import java.time.LocalDate;

public abstract class Inversion extends Actividad {

    private static int contadorId = 1;

    protected int plazoDias;
    private int idInversion;
    protected boolean activa;

    public Inversion(String cvu, double monto, int plazoDias) {
        super(Utilitarios.hoy(), monto, cvu);
        if (plazoDias <= 0) {
            throw new IllegalArgumentException("El plazo en días debe ser mayor a cero.");
        }

        this.plazoDias = plazoDias;
        this.idInversion = contadorId++;
        this.activa = true;
    }

    public int getIdInversion() {
        return idInversion; 
    }

    public int getPlazoDias() {
        return plazoDias;
    }

    public boolean estaActiva() {
        return this.activa;
    }

    public abstract double calcularResultado();

    protected void finalizar() {
        this.activa = false;
    }

    public boolean venceHoy() {

        return this.fecha.plusDays(plazoDias).equals(Utilitarios.hoy());
    }


}
