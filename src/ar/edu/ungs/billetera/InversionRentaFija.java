package ar.edu.ungs.billetera;

public class InversionRentaFija extends InversionPrecancelable {

    public InversionRentaFija(String cvu, double monto, int plazoDias) {
        super(cvu, monto, plazoDias);
    }

//    public double precancelar() {
//        return super.getMonto() + super.calcularInteres();
//    }

	@Override
	public double calcularResultado() {
		// 
		return 0;
	}

}
