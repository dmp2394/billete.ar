package ar.edu.ungs.billetera;

import java.time.LocalDate;

public abstract class InversionPrecancelable extends Inversion {


	public InversionPrecancelable(String cvu, double monto, int plazoDias) {
		super(cvu, monto, plazoDias);
	}
	

	public void precancelar() {
    	// al precancelar el monto de esta inversion desaparece
    	this.monto = 0;
    	this.actualizarFecha(Utilitarios.hoy());
    	
    	
    }
    
    public abstract double calcularInteres(LocalDate localDate);
}
