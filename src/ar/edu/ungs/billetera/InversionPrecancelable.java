package ar.edu.ungs.billetera;


public abstract class InversionPrecancelable extends Inversion {

	
    public InversionPrecancelable(String cvu, double monto, int plazoDias) {
    	super(cvu,monto,plazoDias);
    }

    public double precancelar() {
    	
    	
    	return 0;
    }
    
    public abstract double calcularResultado();
}
