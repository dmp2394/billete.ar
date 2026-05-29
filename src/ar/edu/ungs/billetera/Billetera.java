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

	// CONSTRUCTOR
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
		// Formatea los montos truncando a 2 decimales para mejorar legibilidad sin redondear valores.

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
			sb.append("  ").append(c.toString()).append(" | saldo: ").append(String.format("%.2f", Math.floor(c.getSaldo() * 100) / 100.0))
					.append(System.lineSeparator());
		}
		
		// Evita mostrar transferencias duplicada en el resumen general
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
		validarCamposRegistrarEmpresa(cuit, nombreFantasia, telefono, email, nombreContacto);

		Empresa empresa = new Empresa(cuit, nombreFantasia, telefono, email, nombreContacto);

		diccEmpresasPorCuit.put(cuit, empresa);
	}

	@Override
	public void agregarPersonaAutorizada(String cuitEmpresa, String dniAutorizado) {
		if (!diccEmpresasPorCuit.containsKey(cuitEmpresa))
			throw new IllegalArgumentException("la empresa no existe");

		Empresa empresa = diccEmpresasPorCuit.get(cuitEmpresa);

		if (empresa.contienePersonaAutorizada(dniAutorizado))
			throw new IllegalArgumentException("la persona ya esta autorizada");

		empresa.agregarPersonaAutorizada(dniAutorizado);

	}

	@Override
	public void registrarUsuario(String dni, String nombre, String telefono, String email) {

		//Evalua que no exista
		if (diccUsuariosPorDni.containsKey(dni)) 
			throw new IllegalArgumentException("el usuario ya esta registrado");

		Usuario usuario = new Usuario(dni, nombre, telefono, email); 
		diccUsuariosPorDni.put(dni, usuario); 
		
		diccActividadesPorDNI.put(dni, new ArrayList<>());
		diccTotalInvertidoPorDni.put(dni, 0.0);
	}

	@Override
	public String crearCuentaRegular(String dniUsuario, String alias) {
		
		//Valida existencia de usuario o alias
		if (!diccUsuariosPorDni.containsKey(dniUsuario))
			throw new IllegalArgumentException("el usuario no esta registrado");
		if (diccCuentasPorCvu.containsKey(alias))
			throw new IllegalArgumentException("el alias ya esta registrado");

		CuentaRegular cuentaRegular = new CuentaRegular(dniUsuario, alias); 

		String cvu = cuentaRegular.getCvu();

		diccCuentasPorCvu.put(cvu, cuentaRegular); 
		diccActividadesPorCvu.put(cvu, new java.util.ArrayList<>()); 

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

		if (!diccUsuariosPorDni.containsKey(dniUsuario)) 
			throw new IllegalArgumentException("el usuario no esta registrado");

		List<String> cuentasUsuario = new java.util.ArrayList<>(); 

		for (String cvu : diccCuentasPorCvu.keySet()) { 
			Cuenta cuenta = diccCuentasPorCvu.get(cvu); 
			if (cuenta.getDniUsuario().equals(dniUsuario)) {																
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
		
		// Al ser una inversion que se terminó, actualiza el diccTotalInvertidoPorDni
		double totalInvertidoPorDni = diccTotalInvertidoPorDni.get(dni) - inversion.getMonto();
		diccTotalInvertidoPorDni.put(dni, totalInvertidoPorDni);

		cuenta.acreditar(montoInvertidoMasIntereses);

	}

	public void procesarInversionesQueVencenHoy() {
		
		for (Inversion inversion : diccInversionesPorId.values()) {
			// Procesa únicamente inversiones activas que vencen en la fecha actual
			if (inversion.estaActiva() && inversion.venceHoy()) {
				double montoInvertidoMasIntereses = inversion.calcularResultado();

				String cvu = inversion.getCvu();

				Cuenta cuenta = diccCuentasPorCvu.get(cvu);

				cuenta.acreditar(montoInvertidoMasIntereses);
				
				String dni = cuenta.getDniUsuario();
				// Se descuenta del total invertido porque la inversion finalizó
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
		// Búsqueda lineal de cuentas. Complejidad O(n)
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
				
				//Formatea la actividad segun el tipo correspondiente
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

	//Determina el tipo de inversión para mostrarlo en texto
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
		List<String> cvus = new ArrayList<>(diccActividadesPorCvu.keySet()); 

		//Busca la cuenta con mayor cantidad de actividades
		for (int i = 0; i < cantidadTop; i++) {

			String cvuMayor = cvus.get(0); 

			for (String cvu : cvus) {
				//Guardar cantidad de actividad por cvu
				int valor1 = diccActividadesPorCvu.get(cvu).size(); 
				int valor2 = diccActividadesPorCvu.get(cvuMayor).size();

				if (valor1 > valor2) {
					cvuMayor = cvu;
				}
			}

			Cuenta cuentaMayor = diccCuentasPorCvu.get(cvuMayor);
			String tipo = cuentaMayor.getClass().getSimpleName().replace("Cuenta", "");
			resultado.add(tipo + ": " + cuentaMayor.getAlias() + " (" + cvuMayor + ")");

			// Uso Iterator para eliminar de forma segura durante la iteracion
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
