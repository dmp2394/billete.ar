package ar.edu.ungs.billetera;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class InversionRentaFija extends InversionPrecancelable {

    private final double TASA_INTERES_TNA = 0.20;


	public InversionRentaFija(String cvu, double monto, int plazoDias) {
        super(cvu, monto, plazoDias);
    }


    
    public double calcularInteres(LocalDate aFecha) {
  	
    	int dias = (int) ChronoUnit.DAYS.between(this.fecha, aFecha);
    	return this.monto * (TASA_INTERES_TNA/365) * dias;
    }
    
    
	public double calcularResultado() {

		return 0;
	}


}
