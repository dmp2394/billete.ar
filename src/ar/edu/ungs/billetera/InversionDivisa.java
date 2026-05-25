package ar.edu.ungs.billetera;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class InversionDivisa extends InversionPrecancelable {

    private String divisa;
    private double tasa;

    public InversionDivisa(String cvu, double monto, int plazoDias, String divisa, double tasa) {
        super(cvu, monto, plazoDias);
        if (divisa == null || divisa.trim().isEmpty()) {
            throw new IllegalArgumentException("La divisa no puede estar vacía.");
        }
        if (tasa <= 0) {
            throw new IllegalArgumentException("La tasa debe ser mayor a cero.");
        }

        this.divisa = divisa;
        this.tasa = tasa;
    }

    public String getDivisa() {
        return divisa;
    }

    public double getTasa() {
        return tasa;
    }

	@Override
	public double calcularResultado() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double calcularInteres(LocalDate aFecha) {
        double divisasEquivalente = this.monto / Utilitarios.consultarCotizacion("USD");
        int dias = (int) ChronoUnit.DAYS.between(this.fecha, aFecha);
        
        return divisasEquivalente * (this.tasa / 365) * dias;
	}

	
    
    

}
