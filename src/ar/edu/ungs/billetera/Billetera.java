package ar.edu.ungs.billetera;

import java.util.HashMap;
import java.util.List;

public class Billetera implements IBilletera {

	// ATRIBUTOS
	private HashMap<String,Empresa> diccEmpresasPorCuit;
	
	
	// CONSTRUCTOR. INICIALIZO VARIABLES
	public Billetera() {
		this.diccEmpresasPorCuit = new HashMap<>();
	}
	
	
	// METODOS PUBLICOS:
	@Override
	public void registrarEmpresa(String cuit, String nombreFantasia, String telefono, String email, String nombreContacto) {
		// Lanza error si la empresa ya esta registrada o algun campo es inválido.
		validarCamposRegistrarEmpresa(cuit, nombreFantasia, telefono, email, nombreContacto);

		Empresa empresa = new Empresa(cuit,nombreFantasia,telefono,email,nombreContacto);
				
		diccEmpresasPorCuit.put(cuit, empresa);
	}

	@Override
	public void agregarPersonaAutorizada(String cuitEmpresa, String dniAutorizado) {
		// Lanza error si la empresa no existe
		if(!diccEmpresasPorCuit.containsKey(cuitEmpresa))
			throw new IllegalArgumentException("la empresa no existe");
		
		Empresa empresa = diccEmpresasPorCuit.get(cuitEmpresa);
		
		// Lanza error si la persona ya está autorizada.
		if (empresa.contienePersonaAutorizada(dniAutorizado))
			throw new IllegalArgumentException("la persona ya esta autorizada");
		
		empresa.agregarPersonaAutorizada(dniAutorizado);
		
	}

	@Override
	public void registrarUsuario(String dni, String nombre, String telefono, String email) {
		// TODO Auto-generated method stub

	}

	@Override
	public String crearCuentaRegular(String dniUsuario, String alias) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String crearCuentaPremium(String dniUsuario, String alias, double depositoInicial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String crearCuentaCorporativa(String dniUsuario, String alias, String cuitEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> obtenerCuentas(String dniUsuario) {
		// TODO Auto-generated method stub
		return null;
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
	 * */
	// TODO: validaciones correctas? se puede validar que ademas telefono sea un numero, que nombre no sea un numero etc
	private void validarCamposRegistrarEmpresa(String cuit, String nombreFantasia, String telefono, String email, String nombreContacto) {
		
		if(diccEmpresasPorCuit.containsKey(cuit))
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
