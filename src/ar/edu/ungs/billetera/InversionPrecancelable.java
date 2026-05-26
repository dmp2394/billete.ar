package ar.edu.ungs.billetera;

import java.time.LocalDate;

public abstract class InversionPrecancelable extends Inversion {


	public InversionPrecancelable(String cvu, double monto, int plazoDias) {
		super(cvu, monto, plazoDias);
	}
	
	public abstract double precancelar();
    
	protected abstract double calcularInteres();
}
