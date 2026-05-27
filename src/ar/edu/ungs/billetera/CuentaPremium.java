package ar.edu.ungs.billetera;

public class CuentaPremium extends Cuenta {

    private final double SALDO_MINIMO = 500000;

    private double depositoInicial;

    public CuentaPremium(String dniUsuario, String alias, double depositoInicial) {
        super(dniUsuario, alias);

        if (depositoInicial < 500000)
            throw new IllegalArgumentException(
                    "El depósito inicial para una cuenta Premium debe ser al menos $500000.");

        this.saldo = depositoInicial;
        this.depositoInicial = depositoInicial;

    }

    public double getDepositoInicial() {
        return depositoInicial;
    }

    public String toString() {
        return "CuentaPremium [dniUsuario=" + getDniUsuario() + ", alias=" + getAlias()
                + ", depositoInicial=" + depositoInicial + "]";
    }

    public void acreditar(double monto) {
        this.saldo += monto;
    }

    // cuenta premium tiene su manera de debitar, ya que tiene que mantener un monto
    // mínimo en cuenta de 500 mil pesos
    public void debitar(double monto) {

    	if ((this.saldo - monto) < SALDO_MINIMO)
            throw new IllegalStateException("se debe mantener un saldo minimo en cuenta de " + SALDO_MINIMO);

        this.saldo -= monto;
    }

    public void debitarParaInversion(double monto) {
        if (this.saldo < monto)
            throw new IllegalStateException("saldo insuficiente");
        this.saldo -= monto;
    }
}
