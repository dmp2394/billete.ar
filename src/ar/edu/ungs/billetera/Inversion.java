package ar.edu.ungs.billetera;

import java.time.LocalDate;


public class Inversion extends Actividad {

	private static int contadorId = 1;

    private int plazoDias;
    private int idInversion;

    public Inversion(String cvu, double monto, int plazoDias) {
        super(LocalDate.now(), monto, cvu);
        this.plazoDias = plazoDias;
        this.idInversion = contadorId++;
    }

    @Override
    public void validarCampos() {
        super.validarCampos();
        if (plazoDias <= 0) {
            throw new IllegalArgumentException("El plazo en días debe ser mayor a cero.");
        }
    }

    public double calcularInteres() {
        double cotizacion = Utilitarios.consultarCotizacion(getCvu());
        return getMonto() * cotizacion * plazoDias / 365;
    }

    public int getIdInversion() {
        return idInversion; // Retornar el ID almacenado
    }

    public int getPlazoDias() {
        return plazoDias;
    }
}
