package ar.edu.ungs.billetera;

public abstract class Cuenta {

    private String dniUsuario;
    private String alias;
    protected double saldo;

    public Cuenta(String dniUsuario, String alias) {
        this.dniUsuario = dniUsuario;
        this.alias = alias;
        this.saldo = 0;
    }

    public void validarCampos() {
        if (dniUsuario == null || dniUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI del usuario no puede estar vacío.");
        }
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("El alias de la cuenta no puede estar vacío.");
        }
    }

    public String getDniUsuario() {
        return dniUsuario;
    }

    public String getAlias() {
        return alias;
    }

    public String crearCVU() {
        return String.valueOf((long) (Math.random() * 1000000000));
    }

    public String toString() {
        return "Cuenta [dniUsuario=" + dniUsuario + ", alias=" + alias + "]";
    }

    public double getSaldo() {
        return this.saldo;
    }

}
