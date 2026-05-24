package ar.edu.ungs.billetera;

import java.util.HashSet;
import java.util.Set;

public class Empresa {

	private String cuit;
	private String nombreFantasia;
	private String telefono;
	private String email;
	private String nombreContacto;
	private Set<String> dnisPersonasAutorizadas;

	public Empresa(String cuit, String nombreFantasia, String telefono, String email, String nombreContacto) {
		this.cuit = cuit;
		this.nombreFantasia = nombreFantasia;
		this.telefono = telefono;
		this.email = email;
		this.nombreContacto = nombreContacto;
		this.dnisPersonasAutorizadas = new HashSet<>();
	}

	public void agregarPersonaAutorizada(String dniAutorizado) {
		this.dnisPersonasAutorizadas.add(dniAutorizado);
	}

	public boolean contienePersonaAutorizada(String dniAutorizado) {
		return dnisPersonasAutorizadas.contains(dniAutorizado);
	}

}
