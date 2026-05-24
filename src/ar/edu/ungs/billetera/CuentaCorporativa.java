package ar.edu.ungs.billetera;

public class CuentaCorporativa extends Cuenta {

    private String cuitEmpresa;

    public CuentaCorporativa(String dniUsuario, String alias, String cuitEmpresa) {
        super(dniUsuario, alias);
        this.cuitEmpresa = cuitEmpresa;
    }

    @Override
    public void validarCampos() {
        super.validarCampos();
        if (cuitEmpresa == null || cuitEmpresa.trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIT de la empresa no puede estar vacío.");
        }
    }

    public String getCuitEmpresa() {
        return cuitEmpresa;
    }

    public String toString() {
        return "CuentaCorporativa [dniUsuario=" + getDniUsuario() + ", alias=" + getAlias()
                + ", cuitEmpresa=" + cuitEmpresa + "]";
    }
    
    public void acreditar(double monto) {
    	this.saldo += monto;
    }
    
    public void debitar(double monto) {
    	if (this.saldo - monto < 0)
    		throw new IllegalStateException("saldo insuficiente");
    	
    	this.saldo-=monto;
    }

}
