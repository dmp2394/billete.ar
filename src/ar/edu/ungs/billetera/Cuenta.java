package ar.edu.ungs.billetera;

public abstract class Cuenta {

    protected String dniUsuario;
    protected String alias;
    protected double saldo;
    protected String cvu;

    public Cuenta(String dniUsuario, String alias) {
        if (dniUsuario == null || dniUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI del usuario no puede estar vacío.");
        }
        if (alias == null || alias.trim().isEmpty()) {
            throw new IllegalArgumentException("El alias de la cuenta no puede estar vacío.");
        }

        this.dniUsuario = dniUsuario;
        this.alias = alias;
        this.saldo = 0;
        this.cvu = Utilitarios.generarSiguienteCvu();
    }

    public String getDniUsuario() {
        return dniUsuario;
    }

    public String getAlias() {
        return alias;
    }

    public double getSaldo() {
        return this.saldo;
    }

    public String getCvu() {
        return cvu;
    }

    // metodos abstractos asi las subclases deben implementarlos si o si
    public abstract void debitar(double monto);

    // este no suma al disponible
    public abstract void debitarParaInversion(double monto);

    public abstract void acreditar(double monto);

}
