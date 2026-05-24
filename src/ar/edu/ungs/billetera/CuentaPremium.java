package ar.edu.ungs.billetera;

public class CuentaPremium extends Cuenta {

    private double depositoInicial;

    public CuentaPremium(String dniUsuario, String alias, double depositoInicial) {
        super(dniUsuario, alias);
        this.saldo = depositoInicial;
        this.depositoInicial = depositoInicial;
    }

    @Override
    public void validarCampos() {
        super.validarCampos();
        if (depositoInicial < 500000) {
            throw new IllegalArgumentException(
                    "El depósito inicial para una cuenta Premium debe ser al menos $500000.");
        }
    }

    public double getDepositoInicial() {
        return depositoInicial;
    }

    public String toString() {
        return "CuentaPrenium [dniUsuario=" + getDniUsuario() + ", alias=" + getAlias()
                + ", depositoInicial=" + depositoInicial + "]";
    }

}
