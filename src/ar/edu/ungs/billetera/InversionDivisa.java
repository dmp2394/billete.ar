package ar.edu.ungs.billetera;

public class InversionDivisa extends Inversion {

    private String divisa;
    private double tasa;

    public InversionDivisa(String cvu, double monto, int plazoDias, String divisa, double tasa) {
        super(cvu, monto, plazoDias);
        this.divisa = divisa;
        this.tasa = tasa;
    }

    public void validarCampos() {
        super.validarCampos();
        if (divisa == null || divisa.trim().isEmpty()) {
            throw new IllegalArgumentException("La divisa no puede estar vacía.");
        }
        if (tasa <= 0) {
            throw new IllegalArgumentException("La tasa debe ser mayor a cero.");
        }
    }

    public double calcularInteres() {
        double cotizacion = Utilitarios.consultarCotizacion(getCvu());
        return getMonto() * cotizacion * tasa * getPlazoDias() / 365;
    }

    public double precancelar() {
        double interesGenerado = calcularInteres();
        return getMonto() + (interesGenerado / 2);
    }

    public String getDivisa() {
        return divisa;
    }

    public double getTasa() {
        return tasa;
    }

}
