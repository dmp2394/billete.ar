package ar.edu.ungs.billetera;

public class InversionLiquidez extends Inversion {

    protected double activoFLE = 0.08; // tasa fija de 8%

    public InversionLiquidez(String cvu, double monto, int plazoDias) {
        super(cvu, monto, plazoDias);
    }

    // si la inversión fué precancelada, se paga la mitad de la
    // rentabilidad generada hasta al momento

    // @Deprecated
    // public double calcularInteres() {
    // double cotizacion = Utilitarios.consultarCotizacion(getCvu());
    // return getMonto() * activoFLE * cotizacion * getPlazoDias() / 365;
    // }

    public double getActivoFLE() {
        return activoFLE;
    }

	@Override
	public double calcularResultado() {
		// TODO Auto-generated method stub
		return 0;
	}
}
