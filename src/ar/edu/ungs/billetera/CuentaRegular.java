package ar.edu.ungs.billetera;

public class CuentaRegular extends Cuenta {

    private final double SALDO_MAXIMO = 5000000;

    private double depositoFinal;

    public CuentaRegular(String dniUsuario, String alias) {
        super(dniUsuario, alias);

    }

    public String toString() {
        return "CuentaRegular [dniUsuario=" + getDniUsuario() + ", alias=" + getAlias() + "]";
    }

    // cuenta regular tiene su manera de acreditar, ya que tiene que validar que no
    // se supere el saldo maximo de 5M
    public void acreditar(double monto) {
        if (this.saldo + monto > SALDO_MAXIMO)
            throw new IllegalStateException(
                    "las cuentas regulares no pueden recibir transferencias mayores a " + SALDO_MAXIMO);
        if (depositoFinal > SALDO_MAXIMO)
            throw new IllegalArgumentException(
                    "El depósito inicial para una cuenta Regular no puede superar los $5000000.");

        this.saldo += monto;
    }

    public void debitar(double monto) {
        if (this.saldo - monto < 0)
            throw new IllegalStateException("saldo insuficiente");

        this.saldo -= monto;
    }

    public double getDepositoFinal() {
        return depositoFinal;
    }
}