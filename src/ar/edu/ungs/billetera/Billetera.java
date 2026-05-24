package ar.edu.ungs.billetera;

import java.util.HashMap;
import java.util.List;

public class Billetera implements IBilletera {

	// ATRIBUTOS
	private HashMap<String, Empresa> diccEmpresasPorCuit;
	private HashMap<String, Usuario> diccUsuariosPorDni;
	private HashMap<String, Cuenta> diccCuentasPorCvu;
	private HashMap<String, List<Actividad>> diccActividadesPorDNI;
	private HashMap<String, List<Actividad>> diccActividadesPorCvu;

	// CONSTRUCTOR. INICIALIZO VARIABLES
	public Billetera() {
		this.diccEmpresasPorCuit = new HashMap<>();
		this.diccUsuariosPorDni = new HashMap<>();
		this.diccCuentasPorCvu = new HashMap<>();
		this.diccActividadesPorDNI = new HashMap<>();
		this.diccActividadesPorCvu = new HashMap<>();
	}

	// METODOS PUBLICOS:
	@Override
	public String toString() {
		// TODO: actualizar esto al final
		return ("cantidad de usuarios, empresas, cuentas, etc");
	}

	@Override
	public void registrarEmpresa(String cuit, String nombreFantasia, String telefono, String email,
			String nombreContacto) {
		// Lanza error si la empresa ya esta registrada o algun campo es inválido.
		validarCamposRegistrarEmpresa(cuit, nombreFantasia, telefono, email, nombreContacto);

		Empresa empresa = new Empresa(cuit, nombreFantasia, telefono, email, nombreContacto);

		diccEmpresasPorCuit.put(cuit, empresa);
	}

	@Override
	public void agregarPersonaAutorizada(String cuitEmpresa, String dniAutorizado) {
		// Lanza error si la empresa no existe
		if (!diccEmpresasPorCuit.containsKey(cuitEmpresa))
			throw new IllegalArgumentException("la empresa no existe");

		Empresa empresa = diccEmpresasPorCuit.get(cuitEmpresa);

		// Lanza error si la persona ya está autorizada.
		if (empresa.contienePersonaAutorizada(dniAutorizado))
			throw new IllegalArgumentException("la persona ya esta autorizada");

		empresa.agregarPersonaAutorizada(dniAutorizado);

	}

	@Override
	public void registrarUsuario(String dni, String nombre, String telefono, String email) {

		Usuario usuario = new Usuario(dni, nombre, telefono, email); // creo el usuario con los datos ingresados
		usuario.validarCampos(); // valida que los valores ingresados sean correctos, sino lanza error
		if (diccUsuariosPorDni.containsKey(dni)) // busca si ya esta registrado un usuario con ese dni, si es asi lanza
													// error
			throw new IllegalArgumentException("el usuario ya esta registrado");
		diccUsuariosPorDni.put(dni, usuario); // si no esta registrado, lo agrega al diccionario de usuarios
		diccActividadesPorDNI.put(dni, new java.util.ArrayList<>()); // Inicializar lista de actividades
	}

	@Override
	public String crearCuentaRegular(String dniUsuario, String alias) {

		CuentaRegular cuentaRegular = new CuentaRegular(dniUsuario, alias); // crea la cuenta con los datos ingresados
		cuentaRegular.validarCampos(); // valida que los valores ingresados sean correctos, sino lanza error
		if (!diccUsuariosPorDni.containsKey(dniUsuario)) // busca si el usuario existe, si no existe lanza error
			throw new IllegalArgumentException("el usuario no esta registrado");
		if (diccCuentasPorCvu.containsKey(alias)) // busca si el alias ya esta registrado, si es asi lanza error
			throw new IllegalArgumentException("el alias ya esta registrado");

		String cvu = cuentaRegular.crearCVU(); // si el usuario existe y el alias no esta registrado, crea un cvu para
												// la cuenta y la agrega al diccionario de cuentas
		diccCuentasPorCvu.put(cvu, cuentaRegular); // agrega la cuenta al diccionario de cuentas con el cvu como clave
		diccActividadesPorCvu.put(cvu, new java.util.ArrayList<>()); // Inicializar lista de actividades para la cuenta

		return cvu;
	}

	@Override
	public String crearCuentaPremium(String dniUsuario, String alias, double depositoInicial) {

		CuentaPremium cuentaPremium = new CuentaPremium(dniUsuario, alias, depositoInicial);
		cuentaPremium.validarCampos();
		if (!diccUsuariosPorDni.containsKey(dniUsuario))
			throw new IllegalArgumentException("el usuario no esta registrado");
		if (diccCuentasPorCvu.containsKey(alias))
			throw new IllegalArgumentException("el alias ya esta registrado");
		String cvu = cuentaPremium.crearCVU();
		diccCuentasPorCvu.put(cvu, cuentaPremium);
		diccActividadesPorCvu.put(cvu, new java.util.ArrayList<>());

		return cvu;
	}

	@Override
	public String crearCuentaCorporativa(String dniUsuario, String alias, String cuitEmpresa) {

		CuentaCorporativa cuentaCorporativa = new CuentaCorporativa(dniUsuario, alias, cuitEmpresa);
		cuentaCorporativa.validarCampos();
		if (!diccUsuariosPorDni.containsKey(dniUsuario))
			throw new IllegalArgumentException("el usuario no esta registrado");
		if (diccCuentasPorCvu.containsKey(alias))
			throw new IllegalArgumentException("el alias ya esta registrado");
		if (!diccEmpresasPorCuit.containsKey(cuitEmpresa))
			throw new IllegalArgumentException("la empresa no esta registrada");
		String cvu = cuentaCorporativa.crearCVU();
		diccCuentasPorCvu.put(cvu, cuentaCorporativa);
		diccActividadesPorCvu.put(cvu, new java.util.ArrayList<>());
		return cvu;
	}

	@Override
	public List<String> obtenerCuentas(String dniUsuario) {

		if (!diccUsuariosPorDni.containsKey(dniUsuario)) // busca si el usuario existe, si no existe lanza error
			throw new IllegalArgumentException("el usuario no esta registrado");

		List<String> cuentasUsuario = new java.util.ArrayList<>(); // creo una lista vacia para almacenar las cuentas
																	// del usuario

		for (String cvu : diccCuentasPorCvu.keySet()) { // recorro el diccionario de cuentas
			Cuenta cuenta = diccCuentasPorCvu.get(cvu); // obtengo la cuenta asociada al cvu
			if (cuenta.getDniUsuario().equals(dniUsuario)) { // si el dni de la cuenta coincide con el dni del usuario,
																// agrego a la lista el tipo de cuenta, el alias y el
																// cvu
				String tipo = cuenta.getClass().getSimpleName(); // obtengo el tipo de cuenta (Regular, Premium o
																	// Corporativa) a partir del nombre de la clase
				cuentasUsuario.add(tipo + ": " + cuenta.getAlias() + " " + cvu); // agrego a la lista el tipo de cuenta,
																					// el alias y el cvu
			}
		}

		return cuentasUsuario;

	}

	@Override
	public double obtenerSaldoDisponible(String cvu) {
		return diccCuentasPorCvu.get(cvu).getSaldo();
	}

	@Override
	public void realizarTransferencia(String cvuOrigen, String cvuDestino, double monto) {

		if (!diccCuentasPorCvu.containsKey(cvuOrigen))
			throw new IllegalArgumentException("La cuenta de origen no existe.");

		if (!diccCuentasPorCvu.containsKey(cvuDestino))
			throw new IllegalArgumentException("La cuenta de destino no existe.");

		Cuenta cuentaOrigen = diccCuentasPorCvu.get(cvuOrigen);
		Cuenta cuentaDestino = diccCuentasPorCvu.get(cvuDestino);

		if (cuentaOrigen.getSaldo() < monto)
			throw new IllegalArgumentException("Saldo insuficiente en la cuenta de origen.");

		Transferencia transferencia = new Transferencia(cvuOrigen, cvuDestino, monto);
		transferencia.validarCampos();

		cuentaOrigen.saldo -= monto;
		cuentaDestino.saldo += monto;

		diccActividadesPorDNI.get(cuentaOrigen.getDniUsuario()).add(transferencia);
		diccActividadesPorDNI.get(cuentaDestino.getDniUsuario()).add(transferencia);
		diccActividadesPorCvu.get(cvuOrigen).add(transferencia);
		diccActividadesPorCvu.get(cvuDestino).add(transferencia);

	}

	@Override
	public int realizarInversionRentaFija(String dni, String cvu, double monto, int plazoDias) {

		if (!diccUsuariosPorDni.containsKey(dni))
			throw new IllegalArgumentException("El usuario no está registrado.");

		if (!diccCuentasPorCvu.containsKey(cvu))
			throw new IllegalArgumentException("La cuenta no existe.");

		Cuenta cuenta = diccCuentasPorCvu.get(cvu);
		if (!cuenta.getDniUsuario().equals(dni))
			throw new IllegalArgumentException("La cuenta no pertenece al usuario.");

		if (cuenta.getSaldo() < monto)
			throw new IllegalArgumentException("Saldo insuficiente para realizar la inversión.");

		InversionRentaFija inversion = new InversionRentaFija(cvu, monto, plazoDias);

		inversion.validarCampos();
		int idInversion = inversion.getIdInversion();

		cuenta.saldo -= monto;

		diccActividadesPorDNI.get(dni).add(inversion);
		diccActividadesPorCvu.get(cvu).add(inversion);

		return idInversion;

	}

	@Override
	public int realizarInversionDivisa(String dni, String cvu, double monto, int plazoDias, String divisa,
			double tasa) {
		if (!diccUsuariosPorDni.containsKey(dni))
			throw new IllegalArgumentException("El usuario no está registrado.");

		if (!diccCuentasPorCvu.containsKey(cvu))
			throw new IllegalArgumentException("La cuenta no existe.");

		Cuenta cuenta = diccCuentasPorCvu.get(cvu);
		if (!cuenta.getDniUsuario().equals(dni))
			throw new IllegalArgumentException("La cuenta no pertenece al usuario.");

		if (cuenta.getSaldo() < monto)
			throw new IllegalArgumentException("Saldo insuficiente para realizar la inversión.");

		InversionDivisa inversion = new InversionDivisa(cvu, monto, plazoDias, divisa, tasa);

		inversion.validarCampos();
		int idInversion = inversion.getIdInversion();

		cuenta.saldo -= monto;

		diccActividadesPorDNI.get(dni).add(inversion);
		diccActividadesPorCvu.get(cvu).add(inversion);

		return idInversion;
	}

	@Override
	public int realizarInversionLiquidez(String dni, String cvu, double monto, int plazoDias) {
		if (!diccUsuariosPorDni.containsKey(dni))
			throw new IllegalArgumentException("El usuario no está registrado.");

		if (!diccCuentasPorCvu.containsKey(cvu))
			throw new IllegalArgumentException("La cuenta no existe.");

		Cuenta cuenta = diccCuentasPorCvu.get(cvu);
		if (!cuenta.getDniUsuario().equals(dni))
			throw new IllegalArgumentException("La cuenta no pertenece al usuario.");

		if (cuenta.getSaldo() < monto)
			throw new IllegalArgumentException("Saldo insuficiente para realizar la inversión.");

		InversionLiquidez inversion = new InversionLiquidez(cvu, monto, plazoDias);
		inversion.validarCampos();
		int idInversion = inversion.getIdInversion();
		cuenta.saldo -= monto;
		diccActividadesPorDNI.get(dni).add(inversion);
		diccActividadesPorCvu.get(cvu).add(inversion);
		return idInversion;
	}

	@Override
	public void precancelarInversion(String dni, String cvu, int idInversion) {
		if (!diccUsuariosPorDni.containsKey(dni))
			throw new IllegalArgumentException("El usuario no está registrado.");

		if (!diccCuentasPorCvu.containsKey(cvu))
			throw new IllegalArgumentException("La cuenta no existe.");

	}

	@Override
	public String consultarCvu(String alias) {
		/**
	     * [Nuevo]
	     * 14) Dado un alias consultar el CVU asociado.
	     * Lanza error si el alias no está registrado.
	     *
	     * @param alias Alias para consultar el CVU.
	     * @return cvu asociado al alias. Si el alias no está registrado debe lanzar una
	     *         excepción.
	     */
		

		return null;
	}

	@Override
	public List<String> consultarHistorialGlobal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> consultarHistorialCuenta(String cvu) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> consultarHistorialUsuario(String dniUsuario) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double obtenerTotalInvertido(String dniUsuario) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> cuentasConMayorVolumen(int cantidadTop) {
		// TODO Auto-generated method stub
		return null;
	}

	// METODOS PRIVADOS
	/*
	 * Lanza error si la empresa ya esta registrada o algun campo es inválido.
	 */
	// TODO: validaciones correctas? se puede validar que ademas telefono sea un
	// numero, que nombre no sea un numero etc
	private void validarCamposRegistrarEmpresa(String cuit, String nombreFantasia, String telefono, String email,
			String nombreContacto) {

		if (diccEmpresasPorCuit.containsKey(cuit))
			throw new IllegalArgumentException("la empresa ya esta registrada");

		if (nombreFantasia == null || nombreFantasia.trim().isEmpty())
			throw new IllegalArgumentException("El nombre de fantasia no puede ser vacio o no tener caracteres");

		if (telefono == null || telefono.trim().isEmpty())
			throw new IllegalArgumentException("El telefono no puede ser vacio o no tener caracteres");

		if (email == null || email.trim().isEmpty())
			throw new IllegalArgumentException("El email no puede ser vacio o no tener caracteres");

		if (!email.contains("@"))
			throw new IllegalArgumentException("El email cargado debe tener el caracter @");

		if (nombreContacto == null || nombreContacto.trim().isEmpty())
			throw new IllegalArgumentException("El nombre de contacto no puede ser vacio o no tener caracteres");

	}

}
