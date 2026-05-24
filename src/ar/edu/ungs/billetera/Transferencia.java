package ar.edu.ungs.billetera;

import java.time.LocalDate;

public class Transferencia extends Actividad {
    private String cvuDestino;

    public Transferencia(String cvuOrigen, String cvuDestino, double monto) {
        super(LocalDate.now(), monto, cvuOrigen);
        this.cvuDestino = cvuDestino;
    }

    @Override
    public void validarCampos() {
        super.validarCampos();
        if (cvuDestino == null || cvuDestino.trim().isEmpty()) {
            throw new IllegalArgumentException("El CVU de destino no puede estar vacío.");
        }
    }

    public String getCvuDestino() {
        return cvuDestino;
    }

}
