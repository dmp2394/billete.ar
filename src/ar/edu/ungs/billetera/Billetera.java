package ar.edu.ungs.billetera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Billetera implements IBilletera {

	private static final double MONTO_MINIMO_FLE = 20000000;
	// TODO: INFORME, PUNTO BONUS, TAREA DE COMPLEJIDAD, SUMAR TESTS, 
	// REGISTRAR HISTORIAL EN DONDE FALTE, USAR STRINGBUILDER,
	// REVISAR toString DE BILLETERA Y DERIVADOS 
	//
	// debitar saldo al invertir?
	// que se supone que deberia hacer calcularResultado? lo que corrigio el profesor. Devuelve el total y finaliza la inversion? o sea monto + interés y pone estado inactivo
	// esta bien como calculamos el interes del fondo de liquidez? igual que renta fija pero con otra tasa
	
	// ATRIBUTOS
	private HashMap<String, Empresa> diccEmpresasPorCuit;
	private HashMap<String, Usuario> diccUsuariosPorDni;
	private HashMap<String, Cuenta> diccCuentasPorCvu;
	private HashMap<String, List<Actividad>> diccActividadesPorDNI;
	private HashMap<String, List<Actividad>> diccActividadesPorCvu;
	private HashMap<Integer, Inversion> diccInversionesPorId;
	private List<String> historialGlobal;

	// CONSTRUCTOR. INICIALIZO VARIABLES
	public Billetera() {
		this.diccEmpresasPorCuit = new HashMap<>();
		this.diccUsuariosPorDni = new HashMap<>();
		this.diccCuentasPorCvu = new HashMap<>();
		this.diccActividadesPorDNI = new HashMap<>();
		this.diccActividadesPorCvu = new HashMap<>();
		this.diccInversionesPorId = new HashMap<>();
		this.historialGlobal = new ArrayList<>();
	}

	// METODOS PUBLICOS:
	@Override
	public String toString() {
		// muestro estado de la billetera
		StringBuilder sb = new StringBuilder();
		
		sb.append("Usuarios registrados: ")
			.append(diccUsuariosPorDni.size()).append(System.lineSeparator());
		for (Usuario u : diccUsuariosPorDni.values()) {
			sb.append("  ").append(u.toString()).append(System.lineSeparator());
		}
		sb.append("Empresas registradas: ").append(diccEmpresasPorCuit.size()).append(System.lineSeparator());
		for (Empresa e : diccEmpresasPorCuit.values()) {
			sb.append("  ").append(e.toString()).append(System.lineSeparator());
		}
		sb.append("Cuentas registradas: ").append(diccCuentasPorCvu.size()).append(System.lineSeparator());
		for (Cuenta c : diccCuentasPorCvu.values()) {
			sb.append("  ").append(c.toString()).append(" | saldo: ").append(c.getSaldo()).append(System.lineSeparator());
		}
		sb.append("Transferencias registradas: ").append(diccActividadesPorCvu.size()).append(System.lineSeparator());
		for (List<Actividad> listAct : diccActividadesPorCvu.values()) {
			for (Actividad act : listAct)
				if (act instanceof Transferencia)
					sb.append("  ").append(act.toString()).append(System.lineSeparator());
		}
		sb.append("Inversiones registradas: ").append(diccInversionesPorId.size()).append(System.lineSeparator());
		for (Inversion inv : diccInversionesPorId.values()) {
			sb.append("  ").append(inv.toString()).append(System.lineSeparator());
		}
		return sb.toString();
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

		if (diccUsuariosPorDni.containsKey(dni)) // busca si ya esta registrado un usuario con ese dni, si es asi lanza
													// error
			throw new IllegalArgumentException("el usuario ya esta registrado");
		diccUsuariosPorDni.put(dni, usuario); // si no esta registrado, lo agrega al diccionario de usuarios
		diccActividadesPorDNI.put(dni, new java.util.ArrayList<>()); // Inicializar lista de actividades
	}

	@Override
	public String crearCuentaRegular(String dniUsuario, String alias) {

		if (!diccUsuariosPorDni.containsKey(dniUsuario)) // busca si el usuario existe, si no existe lanza error
			throw new IllegalArgumentException("el usuario no esta registrado");
		if (diccCuentasPorCvu.containsKey(alias)) // busca si el alias ya esta registrado, si es asi lanza error
			throw new IllegalArgumentException("el alias ya esta registrado");

		CuentaRegular cuentaRegular = new CuentaRegular(dniUsuario, alias); // crea la cuenta con los datos ingresados

		String cvu = cuentaRegular.getCvu();

		diccCuentasPorCvu.put(cvu, cuentaRegular); // agrega la cuenta al diccionario de cuentas con el cvu como clave
		diccActividadesPorCvu.put(cvu, new java.util.ArrayList<>()); // Inicializar lista de actividades para la cuenta

		return cvu;
	}

	@Override
	public String crearCuentaPremium(String dniUsuario, String alias, double depositoInicial) {

		if (!diccUsuariosPorDni.containsKey(dniUsuario))
			throw new IllegalArgumentException("el usuario no esta registrado");
		if (diccCuentasPorCvu.containsKey(alias))
			throw new IllegalArgumentException("el alias ya esta registrado");

		CuentaPremium cuentaPremium = new CuentaPremium(dniUsuario, alias, depositoInicial);

		String cvu = cuentaPremium.getCvu();

		diccCuentasPorCvu.put(cvu, cuentaPremium);
		diccActividadesPorCvu.put(cvu, new java.util.ArrayList<>());

		return cvu;
	}

	@Override
	public String crearCuentaCorporativa(String dniUsuario, String alias, String cuitEmpresa) {

		if (!diccUsuariosPorDni.containsKey(dniUsuario))
			throw new IllegalArgumentException("el usuario no esta registrado");
		if (diccCuentasPorCvu.containsKey(alias))
			throw new IllegalArgumentException("el alias ya esta registrado");
		if (!diccEmpresasPorCuit.containsKey(cuitEmpresa))
			throw new IllegalArgumentException("la empresa no esta registrada");
		
		Empresa empresa = diccEmpresasPorCuit.get(cuitEmpresa);
		if (!empresa.contienePersonaAutorizada(dniUsuario))
			throw new IllegalArgumentException("el dni no esta autorizado a operar en nombre de la empresa");

		CuentaCorporativa cuentaCorporativa = new CuentaCorporativa(dniUsuario, alias, cuitEmpresa);

		String cvu = cuentaCorporativa.getCvu();
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
				String tipo = cuenta.getClass().getSimpleName().replace("Cuenta", "");
				cuentasUsuario.add(tipo + ": " + cuenta.getAlias() + " (" + cvu + ")");
			}
		}

		return cuentasUsuario;

	}

	@Override
	public double obtenerSaldoDisponible(String cvu) {
		if (!diccCuentasPorCvu.containsKey(cvu))
			throw new IllegalArgumentException("la cuenta no existe");
		return diccCuentasPorCvu.get(cvu).getSaldo();
	}

	@Override
	public void realizarTransferencia(String cvuOrigen, String cvuDestino, double monto) {

		if (!diccCuentasPorCvu.containsKey(cvuOrigen))
			throw new IllegalArgumentException("La cuenta de origen no existe.");

		if (!diccCuentasPorCvu.containsKey(cvuDestino))
			throw new IllegalArgumentException("La cuenta de destino no existe.");

		Transferencia transferencia = new Transferencia(cvuOrigen, cvuDestino, monto);

		Cuenta cuentaOrigen = diccCuentasPorCvu.get(cvuOrigen);

		if (cuentaOrigen.getSaldo() < monto)
			throw new IllegalArgumentException("Saldo insuficiente en la cuenta de origen.");

		Cuenta cuentaDestino = diccCuentasPorCvu.get(cvuDestino);

		cuentaOrigen.debitar(monto);
		cuentaDestino.acreditar(monto);

		diccActividadesPorDNI.get(cuentaOrigen.getDniUsuario()).add(transferencia);
		diccActividadesPorDNI.get(cuentaDestino.getDniUsuario()).add(transferencia);
		diccActividadesPorCvu.get(cvuOrigen).add(transferencia);
		diccActividadesPorCvu.get(cvuDestino).add(transferencia);
		
		// Registrar en historial global
	    historialGlobal.add("Transferencia de " + monto + " desde " + cvuOrigen + " hacia " + cvuDestino);

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
		
		cuenta.debitarParaInversion(monto);
		
		int idInversion = inversion.getIdInversion();

		diccActividadesPorDNI.get(dni).add(inversion);
		diccActividadesPorCvu.get(cvu).add(inversion);
		diccInversionesPorId.put(idInversion, inversion);

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

		cuenta.debitarParaInversion(monto);
		
		int idInversion = inversion.getIdInversion();

		diccActividadesPorDNI.get(dni).add(inversion);
		diccActividadesPorCvu.get(cvu).add(inversion);
		diccInversionesPorId.put(idInversion, inversion);

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

		if (!(cuenta instanceof CuentaCorporativa))
			throw new IllegalArgumentException("Esta inversion solo puede realizarse desde una Cuenta Corporativa");
		if(monto < MONTO_MINIMO_FLE)
			throw new IllegalArgumentException("Esta inversion requiere un monto minimo de " + MONTO_MINIMO_FLE );

		InversionLiquidez inversion = new InversionLiquidez(cvu, monto, plazoDias);

		cuenta.debitarParaInversion(monto);
		
		int idInversion = inversion.getIdInversion();

		diccActividadesPorDNI.get(dni).add(inversion);
		diccActividadesPorCvu.get(cvu).add(inversion);
		diccInversionesPorId.put(idInversion, inversion);

		return idInversion;
	}

	// no hay un diccionario de inversiones, agregarlo esta ok? asi como lo tenemos
	// + diccInversionesPorId
	public void precancelarInversion(String dni, String cvu, int idInversion) {
		
		if (!diccUsuariosPorDni.containsKey(dni))
			throw new IllegalArgumentException("El usuario no está registrado.");

		if (!diccCuentasPorCvu.containsKey(cvu))
			throw new IllegalArgumentException("La cuenta no existe.");

		if (!existeInversion(idInversion))
			throw new IllegalArgumentException("La inversion no existe.");

		if (!inversionEstaActiva(idInversion))
			throw new IllegalArgumentException("La inversion no esta activa.");

		if (!inversionEsPrecancelable(idInversion))
			throw new IllegalArgumentException("Este tipo de inversión no es precancelable.");
		
		Cuenta cuenta = diccCuentasPorCvu.get(cvu);
		if (!cuenta.getDniUsuario().equals(dni))
			throw new IllegalArgumentException("La cuenta no pertenece al usuario.");
		
		InversionPrecancelable inversion = (InversionPrecancelable) diccInversionesPorId.get(idInversion);

	
		double montoInvertidoMasIntereses = inversion.precancelar();
		
		cuenta.acreditar(montoInvertidoMasIntereses);


	}
	

	public void procesarInversionesQueVencenHoy() {
		 /**
	     * [Bonus Track]
	     * 15) Procesa todas las inversiones que vencen el dia de hoy
	     * y actualiza los saldos agregando los intereses generados segun el tipo de
	     * inversion.
	     * Sea por taza fija o por cotización de activos más tasa.
	     * 
	     * El dia actual y las cotizaciones de los activos se deben consultar a
	     * Utilitarios.
	     * 
	     */
		for (Inversion inversion : diccInversionesPorId.values()) {
			if (inversion.venceHoy()) {
				double montoInvertidoMasIntereses = inversion.calcularResultado();
				
				String cvu = inversion.getCvu();
				
				Cuenta cuenta = diccCuentasPorCvu.get(cvu);
				
				// Buscar la cuenta para acreditar
				cuenta.acreditar(montoInvertidoMasIntereses);
				
			}
		}
		
	}
	
	

	private boolean inversionEsPrecancelable(int idInversion) {

		return (diccInversionesPorId.get(idInversion) instanceof InversionPrecancelable);
	}

	private boolean existeInversion(int idInversion) {
		Inversion inversion = diccInversionesPorId.get(idInversion);

		return inversion != null;
	}

	private boolean inversionEstaActiva(int idInversion) {
		return diccInversionesPorId.get(idInversion).estaActiva();
	}

	@Override
	public String consultarCvu(String alias) {

		// podria haber diccAliasPorCvu, no hay un req de que esto debe ser en O(1), por
		// lo que para no extender demasiado las estructuras de datos se implementa asi.
		// Complejidad O(n)
		for (Cuenta cuenta : diccCuentasPorCvu.values())
			if (alias.equals(cuenta.getAlias()))
				return cuenta.getCvu();

		throw new IllegalArgumentException("el alias no esta registrado");
	}

	@Override
	public List<String> consultarHistorialGlobal() {
		
		return historialGlobal;
	}

	@Override
	public List<String> consultarHistorialCuenta(String cvu) {

		List<Actividad> actividades = diccActividadesPorCvu.get(cvu);
		
		if(actividades == null) {
			return new ArrayList<>();
		}else {
			List<String> resultado = new ArrayList<>();
			
			for(Actividad actividad : actividades) {
				resultado.add(actividad.toString());
			}
			
			return resultado;
		}
	}

	@Override
	public List<String> consultarHistorialUsuario(String dniUsuario) {
		
		List<Actividad> actividades = diccActividadesPorDNI.get(dniUsuario);
		
		if(actividades == null) {
			return new ArrayList<>();
		}else {
			List<String> resultado = new ArrayList<>();
			
			for(Actividad actividad : actividades) {
				resultado.add(actividad.toString());
			}
			
			return resultado;
		}
	}

	@Override
	public double obtenerTotalInvertido(String dniUsuario) {

		if (!diccUsuariosPorDni.containsKey(dniUsuario))
			throw new IllegalArgumentException("el usuario no existe");

		double totalInvertido = 0;
		List<Actividad> listaActividades = diccActividadesPorDNI.get(dniUsuario);

		for (Actividad actividad : listaActividades)
			if (actividad instanceof Inversion && ((Inversion) actividad).estaActiva())
				totalInvertido += actividad.getMonto();

		return totalInvertido;
	}

	@Override
	public List<String> cuentasConMayorVolumen(int cantidadTop) {

		List<String> resultado = new ArrayList<>();
		List<String> cvus = new ArrayList<>(diccActividadesPorCvu.keySet()); //guardamos cvus
		
		for (int i= 0; i < cantidadTop; i++) {
			
			String cvuMayor = cvus.get(0); //iniciamos en primer valor
			
			for (String cvu : cvus) {
				
				int valor1 = diccActividadesPorCvu.get(cvu).size(); //guardamos cuantas act tiene el cvu
				int valor2 = diccActividadesPorCvu.get(cvuMayor).size();
				
				if (valor1 > valor2) {
					cvuMayor = cvu;
				}
			}
			
			resultado.add(cvuMayor); //guardamos el mas grande
			
			cvus.remove(cvuMayor); //se elimina para buscar el sig más grande
		}
		
		return resultado;
	}

	// METODOS PRIVADOS
	/*
	 * Lanza error si la empresa ya esta registrada o algun campo es inválido.
	 */
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
