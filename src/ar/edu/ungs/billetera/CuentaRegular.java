package ar.edu.ungs.billetera;

public class CuentaRegular extends Cuenta {

    private final double SALDO_MAXIMO = 5000000;

    public CuentaRegular(String dniUsuario, String alias) {
        super(dniUsuario, alias);

    }

    public String toString() {
        return "CuentaRegular [dniUsuario=" + getDniUsuario() + ", alias=" + getAlias() + "]";
    }

    // Evalúa máximo de 5M
    public void acreditar(double monto) {
        if (this.saldo + monto > SALDO_MAXIMO)
            throw new IllegalStateException(
                    "las cuentas regulares no pueden recibir transferencias mayores a " + SALDO_MAXIMO);

        this.saldo += monto;
    }

    public void debitar(double monto) {
        if (this.saldo - monto < 0)
            throw new IllegalStateException("saldo insuficiente");

        this.saldo -= monto;
    }

    public void debitarParaInversion(double monto) {
        debitar(monto);
    }

}