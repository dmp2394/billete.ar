package ar.edu.ungs.billetera;

public class CuentaRegular extends Cuenta {

    public CuentaRegular(String dniUsuario, String alias) {
        super(dniUsuario, alias);
    }

    @Override
    public void validarCampos() {
        super.validarCampos();
    }

    public String toString() {
        return "CuentaRegular [dniUsuario=" + getDniUsuario() + ", alias=" + getAlias() + "]";
    }

}