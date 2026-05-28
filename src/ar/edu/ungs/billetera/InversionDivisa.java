package ar.edu.ungs.billetera;

//import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class InversionDivisa extends InversionPrecancelable {

    private String divisa;
    private double tasa;
    private double divisaEquivalente;

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
        this.divisaEquivalente = this.monto / Utilitarios.consultarCotizacion(this.divisa);
    }

    public String getDivisa() {
        return divisa;
    }

    public double getTasa() {
        return tasa;
    }

    @Override
    protected double calcularInteres() {
        int dias = (int) ChronoUnit.DAYS.between(this.fecha, Utilitarios.hoy());

        return this.divisaEquivalente * (this.tasa / 365) * dias;

    }

    @Override
    public double calcularResultado() {
        double montoAAcreditar = (this.divisaEquivalente + calcularInteres())
                * Utilitarios.consultarCotizacion(this.divisa);
        this.finalizar();

        return montoAAcreditar;
    }

    public double precancelar() {
        double montoAAcreditar = (this.divisaEquivalente + calcularInteres() / 2)
                * Utilitarios.consultarCotizacion(this.divisa);
        this.finalizar();

        return montoAAcreditar;
    }

    public String toString() {
        String montoFormateado = String.format("%.2f", Math.floor(monto * 100) / 100.0);
        String divisaFormateado = String.format("%.2f", Math.floor(divisaEquivalente * 100) / 100.0);
        return "InversionDivisa [fecha=" + fecha + ", monto=" + montoFormateado + ", divisa=" + divisa + ", tasa=" + (tasa * 100)
                + "%, divisaEquivalente=" + divisaFormateado + ", plazoDias=" + plazoDias
                + ", idInversion=" + getIdInversion() + ", cvu=" + getCvu() + "]";
    }

}
