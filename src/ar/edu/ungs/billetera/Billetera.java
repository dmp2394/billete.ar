package ar.edu.ungs.billetera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Billetera implements IBilletera {



	private final double MONTO_MINIMO_FLE = 20000000;

	// ATRIBUTOS
	private HashMap<String, Empresa> diccEmpresasPorCuit;
	private HashMap<String, Usuario> diccUsuariosPorDni;
	private HashMap<String, Cuenta> diccCuentasPorCvu;
	private HashMap<String, List<Actividad>> diccActividadesPorDNI;
	private HashMap<String, List<Actividad>> diccActividadesPorCvu;
	private HashMap<Integer, Inversion> diccInversionesPorId;
	private HashMap<String, Double> diccTotalInvertidoPorDni;

	// CONSTRUCTOR. INICIALIZO VARIABLES
	public Billetera() {
		this.diccEmpresasPorCuit = new HashMap<>();
		this.diccUsuariosPorDni = new HashMap<>();
		this.diccCuentasPorCvu = new HashMap<>();
		this.diccActividadesPorDNI = new HashMap<>();
		this.diccActividadesPorCvu = new HashMap<>();
		this.diccInversionesPorId = new HashMap<>();
		this.diccTotalInvertidoPorDni = new HashMap<>();
	}

	// METODOS PUBLICOS:
	@Override
	public String toString() {
		// muestro estado de la billetera con stringbuilder. Para saltos de linea se usa System.lineSeparator()
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
			sb.append("  ").append(c.toString()).append(" | saldo: ").append(c.getSaldo())
					.append(System.lineSeparator());
		}
		
		// esto lo hago para mostrar 1 sola transferencia en el estado de la billetera, sino se va a mostrar el mismo registro por duplicado
		// convierto a HashSet y agrego Las transferencias que encuentro. Esto funciona porque ambas transferencias son el mismo objeto, pues 
		// en realizarTransferencia se agrega la misma tanto en el cvu de origen como de destino
		Set<Actividad> transferenciasUnicas = new HashSet<>();
		for (List<Actividad> listAct : diccActividadesPorCvu.values())
			for (Actividad act : listAct)
				if (act instanceof Transferencia)
					transferenciasUnicas.add(act);
		
		sb.append("Transferencias registradas: ").append(transferenciasUnicas.size()).append(System.lineSeparator());
		for (Actividad act : transferenciasUnicas)
			sb.append("  ").append(act.toString()).append(System.lineSeparator());
		
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

		if (diccUsuariosPorDni.containsKey(dni)) // busca si ya esta registrado un usuario con ese dni, si es asi lanza error
			throw new IllegalArgumentException("el usuario ya esta registrado");

		Usuario usuario = new Usuario(dni, nombre, telefono, email); // creo el usuario con los datos ingresados
		diccUsuariosPorDni.put(dni, usuario); // si no esta registrado, lo agrega al diccionario de usuarios
		
		// inicializar para el usuario
		diccActividadesPorDNI.put(dni, new ArrayList<>());
		diccTotalInvertidoPorDni.put(dni, 0.0); // Inicializar total invertido en 0.0
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

		cuentaDestino.acreditar(monto);
		cuentaOrigen.debitar(monto);

		diccActividadesPorDNI.get(cuentaOrigen.getDniUsuario()).add(transferencia);
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

		cuenta.debitarParaInversion(monto);

		int idInversion = inversion.getIdInversion();

		diccActividadesPorDNI.get(dni).add(inversion);
		diccActividadesPorCvu.get(cvu).add(inversion);
		diccInversionesPorId.put(idInversion, inversion);
		
		double totalInvertidoPorDni = diccTotalInvertidoPorDni.get(dni) + monto;
		diccTotalInvertidoPorDni.put(dni, totalInvertidoPorDni);

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

		double totalInvertidoPorDni = diccTotalInvertidoPorDni.get(dni) + monto;
		diccTotalInvertidoPorDni.put(dni, totalInvertidoPorDni);
		
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

		if (monto < MONTO_MINIMO_FLE) {
			String montoFormateado = String.format(
				    Locale.forLanguageTag("es-AR"), 
				    "%,.0f", 
				    MONTO_MINIMO_FLE
				);
			throw new IllegalArgumentException("Esta inversion requiere un monto minimo de $" + montoFormateado);
		}

		if (cuenta.getSaldo() < monto)
			throw new IllegalArgumentException("Saldo insuficiente para realizar la inversión.");

		if (!(cuenta instanceof CuentaCorporativa))
			throw new IllegalArgumentException("Esta inversion solo puede realizarse desde una Cuenta Corporativa");

		InversionLiquidez inversion = new InversionLiquidez(cvu, monto, plazoDias);

		cuenta.debitarParaInversion(monto);

		int idInversion = inversion.getIdInversion();

		diccActividadesPorDNI.get(dni).add(inversion);
		diccActividadesPorCvu.get(cvu).add(inversion);
		diccInversionesPorId.put(idInversion, inversion);
		
		double totalInvertidoPorDni = diccTotalInvertidoPorDni.get(dni) + monto;
		diccTotalInvertidoPorDni.put(dni, totalInvertidoPorDni);

		return idInversion;
	}


	@Override
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
		
		// como es una inversion que se terminó, actualizo el diccTotalInvertidoPorDni
		double totalInvertidoPorDni = diccTotalInvertidoPorDni.get(dni) - inversion.getMonto();
		diccTotalInvertidoPorDni.put(dni, totalInvertidoPorDni);

		cuenta.acreditar(montoInvertidoMasIntereses);

	}

	public void procesarInversionesQueVencenHoy() {
		
		for (Inversion inversion : diccInversionesPorId.values()) {
			if (inversion.estaActiva() && inversion.venceHoy()) {
				double montoInvertidoMasIntereses = inversion.calcularResultado();

				String cvu = inversion.getCvu();

				Cuenta cuenta = diccCuentasPorCvu.get(cvu);

				cuenta.acreditar(montoInvertidoMasIntereses);
				
				String dni = cuenta.getDniUsuario();
				double totalInvertidoPorDni = diccTotalInvertidoPorDni.get(dni) - inversion.getMonto();
				diccTotalInvertidoPorDni.put(dni, totalInvertidoPorDni);

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
		List<String> historial = new ArrayList<>();

		for (String dniUsuario : diccActividadesPorDNI.keySet()) {
			List<Actividad> actividades = diccActividadesPorDNI.get(dniUsuario);

			for (Actividad act : actividades) {
				if (act instanceof Transferencia) {
					Transferencia trans = (Transferencia) act;
					String cvuOrigen = trans.getCvu();
					String cvuDestino = trans.getCvuDestino();
					String dniDestino = diccCuentasPorCvu.get(cvuDestino).getDniUsuario();
					String estado = trans.getEstado() ? "Aprobado" : "Rechazado";

					historial.add("- transferencia:\n" +
							"fecha: " + trans.getFecha() + "\n" +
							"origen: " + dniUsuario + " (" + cvuOrigen + ")\n" +
							"destino: " + dniDestino + " (" + cvuDestino + ")\n" +
							"monto: " + trans.getMonto() + "\n" +
							estado);
				} else if (act instanceof Inversion) {
					Inversion inv = (Inversion) act;
					String cvu = inv.getCvu();
					String tipo = obtenerTipoInversion(inv);
					String estado = inv.getEstado() ? "Aprobado" : "Rechazado";

					historial.add("- inversion:\n" +
							"fecha: " + inv.getFecha() + "\n" +
							"origen: " + dniUsuario + " (" + cvu + ")\n" +
							"desc: " + tipo + "\n" +
							"monto: " + inv.getMonto() + "\n" +
							"plazo: " + inv.getPlazoDias() + "\n" +
							estado);
				}
			}
		}

		return historial;
	}

	@Override
	public List<String> consultarHistorialCuenta(String cvu) {

		if (!diccCuentasPorCvu.containsKey(cvu))
			throw new IllegalArgumentException("La cuenta no existe.");

		List<String> historial = new ArrayList<>();
		List<Actividad> actividades = diccActividadesPorCvu.get(cvu);

		for (Actividad act : actividades) {
			if (act instanceof Transferencia) {
				Transferencia trans = (Transferencia) act;
				String cvuReal = trans.getCvu();
				String dniReal = diccCuentasPorCvu.get(cvuReal).getDniUsuario();
				String dniDestino = diccCuentasPorCvu.get(trans.getCvuDestino()).getDniUsuario();
				String cvuDestino = trans.getCvuDestino();
				String estado = trans.getEstado() ? "Aprobado" : "Rechazado";

				historial.add("- transferencia:\n" +
						"fecha: " + trans.getFecha() + "\n" +
						"origen: " + dniReal + " (" + cvuReal + ")\n" +
						"destino: " + dniDestino + " (" + cvuDestino + ")\n" +
						"monto: " + trans.getMonto() + "\n" +
						estado);

			} else if (act instanceof Inversion) {
				Inversion inv = (Inversion) act;
				String dniOrigen = diccCuentasPorCvu.get(cvu).getDniUsuario();
				String tipo = obtenerTipoInversion(inv);
				String estado = inv.getEstado() ? "Aprobado" : "Rechazado";

				historial.add("- inversion:\n" +
						"fecha: " + inv.getFecha() + "\n" +
						"origen: " + dniOrigen + " (" + cvu + ")\n" +
						"desc: " + tipo + "\n" +
						"monto: " + inv.getMonto() + "\n" +
						"plazo: " + inv.getPlazoDias() + "\n" +
						estado);
			}
		}

		return historial;
	}

	private String obtenerTipoInversion(Inversion inversion) {
		if (inversion instanceof InversionRentaFija) {
			return "renta fija";
		} else if (inversion instanceof InversionDivisa) {
			return "divisa";
		} else if (inversion instanceof InversionLiquidez) {
			return "liquidez";
		}
		return "desconocida";
	}

	@Override
	public List<String> consultarHistorialUsuario(String dniUsuario) {
		if (!diccUsuariosPorDni.containsKey(dniUsuario))
			throw new IllegalArgumentException("El usuario no existe.");

		List<String> historial = new ArrayList<>();
		List<Actividad> actividades = diccActividadesPorDNI.get(dniUsuario);

		for (Actividad act : actividades) {
			if (act instanceof Transferencia) {
				Transferencia trans = (Transferencia) act;
				String cvuOrigen = trans.getCvu();
				String cvuDestino = trans.getCvuDestino();
				String dniDestino = diccCuentasPorCvu.get(cvuDestino).getDniUsuario();
				String estado = trans.getEstado() ? "Aprobado" : "Rechazado";

				historial.add("- transferencia:\n" +
						"fecha: " + trans.getFecha() + "\n" +
						"origen: " + dniUsuario + " (" + cvuOrigen + ")\n" +
						"destino: " + dniDestino + " (" + cvuDestino + ")\n" +
						"monto: " + trans.getMonto() + "\n" +
						estado);
			} else if (act instanceof Inversion) {
				Inversion inv = (Inversion) act;
				String cvu = inv.getCvu();
				String tipo = obtenerTipoInversion(inv);
				String estado = inv.getEstado() ? "Aprobado" : "Rechazado";

				historial.add("- inversion:\n" +
						"fecha: " + inv.getFecha() + "\n" +
						"origen: " + dniUsuario + " (" + cvu + ")\n" +
						"desc: " + tipo + "\n" +
						"monto: " + inv.getMonto() + "\n" +
						"plazo: " + inv.getPlazoDias() + "\n" +
						estado);
			}
		}

		return historial;
	}

	
	@Override
	public double obtenerTotalInvertido(String dniUsuario) {

		if (!diccUsuariosPorDni.containsKey(dniUsuario))
			throw new IllegalArgumentException("el usuario no existe");
		

		return diccTotalInvertidoPorDni.get(dniUsuario);
	}

	@Override
	public List<String> cuentasConMayorVolumen(int cantidadTop) {

		if (cantidadTop <= 0)
			throw new IllegalArgumentException("cantidadTop debe ser mayor a cero.");
		if (cantidadTop > diccCuentasPorCvu.size())
			throw new IllegalArgumentException("cantidadTop no debe exceder la cantidad de cuentas creadas");

		List<String> resultado = new ArrayList<>();
		List<String> cvus = new ArrayList<>(diccActividadesPorCvu.keySet()); // guardamos cvus

		for (int i = 0; i < cantidadTop; i++) {

			String cvuMayor = cvus.get(0); // iniciamos en primer valor

			for (String cvu : cvus) {

				int valor1 = diccActividadesPorCvu.get(cvu).size(); // guardamos cuantas act tiene el cvu
				int valor2 = diccActividadesPorCvu.get(cvuMayor).size();

				if (valor1 > valor2) {
					cvuMayor = cvu;
				}
			}

			Cuenta cuentaMayor = diccCuentasPorCvu.get(cvuMayor);
			String tipo = cuentaMayor.getClass().getSimpleName().replace("Cuenta", "");
			resultado.add(tipo + ": " + cuentaMayor.getAlias() + " (" + cvuMayor + ")");

			// uso Iterator para eliminar de forma segura durante la iteracion
			Iterator<String> it = cvus.iterator();
			while (it.hasNext()) {
				if (it.next().equals(cvuMayor)) {
					it.remove();
					break;
				}
			}
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
