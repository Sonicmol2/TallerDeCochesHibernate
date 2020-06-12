package clases;

import java.io.Serializable;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import org.hibernate.annotations.Type;

@Entity()
@Table(name = "Coche")
public class Coche implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "Matricula")
	@Size(max = 8)
	@Pattern(regexp = "\\d{4}-[A-Z]{3}" , message = "Error. Formato Matricula incorrecto")
	private String matricula;
	@Column(name = "Marca")
	@Size(min = 3, max = 15)
	private String marca;
	@Column(name = "Modelo")
	@Size(min = 2, max = 15)
	private String modelo;

	@ManyToOne
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "Cliente_Pertenece")
	private Cliente cliente;

	@OneToMany
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	@JoinColumn(name = "Matricula_Revisiones")
	private List<Revision> listaRevisiones;

	public Coche(String marca, String modelo, String matricula, Cliente cliente) {
		
		this.marca = marca;
		this.modelo = modelo;
		this.matricula = matricula;
		this.cliente = cliente;
		this.listaRevisiones = new ArrayList<Revision>();
	}

	public Coche() {
		this.listaRevisiones = new ArrayList<Revision>();
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) throws TallerException {

		if (marca.length() <= 0) {
			throw new TallerException("Error. Muy corto la marca.");
		}

		this.marca = marca;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) throws TallerException {

		if (modelo.length() <= 0) {
			throw new TallerException("Error. Muy corto el nombre del modelo.");
		}

		this.modelo = modelo;
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) throws TallerException {
		this.matricula = matricula;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<Revision> getListaRevisiones() {
		return listaRevisiones;
	}

	public void setListaRevisiones(List<Revision> listaRevisiones) {
		this.listaRevisiones = listaRevisiones;
	}
	
	public void annadirRevision(Revision revision) {
		this.listaRevisiones.add(revision);
	}
	
	
	public void borrarRevision(Revision revision) {
		this.listaRevisiones.remove(revision);
	}

	@Override
	public String toString() {
		return "Coche: \n\tMarca: " + marca 
				+ " \n\tModelo: " + modelo 
				+ " \n\tMatricula:" + matricula
				+ " \n\tListaRevisiones: " + listaRevisiones + "\n";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((matricula == null) ? 0 : matricula.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coche other = (Coche) obj;
		if (matricula == null) {
			if (other.matricula != null)
				return false;
		} else if (!matricula.equals(other.matricula))
			return false;
		return true;
	}

}
