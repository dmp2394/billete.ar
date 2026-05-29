package ar.edu.ungs.billetera;

import java.time.temporal.ChronoUnit;

public class InversionLiquidez extends Inversion {

	// Tasa sujeta a un activo particular (fijo) del 8%
	final double TASA_ACTIVO_FLE = 0.08; 

	public InversionLiquidez(String cvu, double monto, int plazoDias) {
		super(cvu, monto, plazoDias);
	}

	protected double calcularInteres() {

		int dias = (int) ChronoUnit.DAYS.between(this.fecha, Utilitarios.hoy());
		return (this.monto * (TASA_ACTIVO_FLE / 365) * dias);
	}

	@Override
	public double calcularResultado() {
		double montoAAcreditar = (this.monto + this.calcularInteres());

		this.finalizar();

		return montoAAcreditar;
	}

	public String toString() {
		String montoFormateado = String.format("%.2f", Math.floor(monto * 100) / 100.0);
		return "InversionLiquidez [fecha=" + fecha + ", monto=" + montoFormateado + ", tasa=" + (TASA_ACTIVO_FLE * 100)
				+ "%, plazoDias=" + plazoDias + ", idInversion=" + getIdInversion() + ", cvu=" + getCvu() + "]";
	}

}
