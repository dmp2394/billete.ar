package ar.edu.ungs.billetera;

public class Transferencia extends Actividad {
    private String cvuDestino;

    public Transferencia(String cvuOrigen, String cvuDestino, double monto) {
        super(Utilitarios.hoy(), monto, cvuOrigen);
        if (cvuDestino == null || cvuDestino.trim().isEmpty()) {
            throw new IllegalArgumentException("El CVU de destino no puede estar vacío.");
        }

        this.cvuDestino = cvuDestino;
    }

    public String getCvuDestino() {
        return cvuDestino;
    }

    public String toString() {
        String montoFormateado = String.format("%.2f", Math.floor(getMonto() * 100) / 100.0);
        return "Transferencia [fecha=" + getFecha() + ", monto=" + montoFormateado+ ", cvuOrigen=" + getCvu()
                + ", cvuDestino=" + cvuDestino + "]";
    }

}
