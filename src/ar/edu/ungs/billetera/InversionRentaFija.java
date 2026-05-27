package ar.edu.ungs.billetera;

//import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class InversionRentaFija extends InversionPrecancelable {

	private final double TASA_INTERES_TNA = 0.20;

	public InversionRentaFija(String cvu, double monto, int plazoDias) {
		super(cvu, monto, plazoDias);
	}

	protected double calcularInteres() {

		int dias = (int) ChronoUnit.DAYS.between(this.fecha, Utilitarios.hoy());
		return (this.monto * (TASA_INTERES_TNA / 365) * dias);
	}

	public double calcularResultado() {
		double montoAAcreditar = (this.monto + this.calcularInteres());

		this.finalizar();

		return montoAAcreditar;
	}

	@Override
	public double precancelar() {
		double montoAAcreditar = (this.monto + this.calcularInteres() / 2);

		this.finalizar();

		return montoAAcreditar;
	}

	public String toString() {
		return "InversionRentaFija [TASA_INTERES_TNA=" + TASA_INTERES_TNA + ", plazoDias=" + plazoDias
				+ ", idInversion=" + getIdInversion() + ", activa=" + estaActiva() + ", fecha=" + fecha
				+ ", monto=" + monto + ", cvu=" + getCvu() + "]";
	}

}
