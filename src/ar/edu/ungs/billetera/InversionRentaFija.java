package ar.edu.ungs.billetera;

public class InversionRentaFija extends Inversion {

    public InversionRentaFija(String cvu, double monto, int plazoDias) {
        super(cvu, monto, plazoDias);
    }

    public double precancelar() {
        double interesGenerado = calcularInteres();
        return getMonto() + interesGenerado;
    }

}
