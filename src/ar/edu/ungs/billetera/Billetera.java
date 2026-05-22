package ar.edu.ungs.billetera;

import java.util.HashMap;
import java.util.List;

public class Billetera implements IBilletera {

	// ATRIBUTOS
	private HashMap<String, Empresa> diccEmpresasPorCuit;
	private HashMap<String, Usuario> diccUsuariosPorDni;
	private HashMap<String, Cuenta> diccCuentasPorCvu;

	// CONSTRUCTOR. INICIALIZO VARIABLES
	public Billetera() {
		this.diccEmpresasPorCuit = new HashMap<>();
	}

	// METODOS PUBLICOS:
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

		return cvu;
	}

	@Override
	public String crearCuentaPremium(String dniUsuario, String alias, double depositoInicial) {

		CuentaPrenium cuentaPremium = new CuentaPrenium(dniUsuario, alias, depositoInicial);
		cuentaPremium.validarCampos();
		if (!diccUsuariosPorDni.containsKey(dniUsuario))
			throw new IllegalArgumentException("el usuario no esta registrado");
		if (diccCuentasPorCvu.containsKey(alias))
			throw new IllegalArgumentException("el alias ya esta registrado");
		String cvu = cuentaPremium.crearCVU();
		diccCuentasPorCvu.put(cvu, cuentaPremium);

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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void realizarTransferencia(String cvuOrigen, String cvuDestino, double monto) {
		// TODO Auto-generated method stub

	}

	@Override
	public int realizarInversionRentaFija(String dni, String cvu, double monto, int plazoDias) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int realizarInversionDivisa(String dni, String cvu, double monto, int plazoDias, String divisa,
			double tasa) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int realizarInversionLiquidez(String dni, String cvu, double monto, int plazoDias) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void precancelarInversion(String dni, String cvu, int idInversion) {
		// TODO Auto-generated method stub

	}

	@Override
	public String consultarCvu(String alias) {
		// TODO Auto-generated method stub
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
