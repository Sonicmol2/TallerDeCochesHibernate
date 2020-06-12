package clases;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Type;

@Entity()
@Table(name = "Revision")
public class Revision implements Serializable{

	@Id
	@Type(type = "integer")
	@Column(name = "idRevision")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int idRevision;
	@Column(name = "Fecha")
	private LocalDate fecha;
	@Column(name = "Descripcion")
	@Size(min = 3, max = 30)
	private String descripcion;
	@Column(name = "Tipo_Revision")
	@Enumerated(EnumType.STRING)
	private TipoRevision tipoRevision;
	@Column(name = "Precio_Revision")
	private double precioRevision;

	@ManyToOne
	@Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@JoinColumn(name = "Matricula_Revisiones")
	private Coche coche;

	public Revision(LocalDate fecha, String descripcion, TipoRevision tipoRevision, double precioRevision, Coche coche) {
		
		this.fecha = fecha;
		this.descripcion = descripcion;
		this.tipoRevision = tipoRevision;
		this.precioRevision = precioRevision;
		this.coche = coche;
	}
	
	public Revision() {
		
	}
	
	public Revision(int idRevision) {
		this.idRevision = idRevision;
	}
	
	public int getIdRevision() {
		return idRevision;
	}

	public void setIdRevision(int idRevision) {
		this.idRevision = idRevision;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public TipoRevision getTipoRevision() {
		return tipoRevision;
	}

	public void setTipoRevision(TipoRevision tipoRevision) {
		this.tipoRevision = tipoRevision;
	}
	
	public double getPrecioRevision() {
		return precioRevision;
	}

	public void setPrecioRevision(double precioRevision) {
		this.precioRevision = precioRevision;
	}

	public Coche getCoche() {
		return coche;
	}

	public void setCoche(Coche coche) {
		this.coche = coche;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((coche == null) ? 0 : coche.hashCode());
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
		Revision other = (Revision) obj;
		if (coche == null) {
			if (other.coche != null)
				return false;
		} else if (!coche.equals(other.coche))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "\nRevision: \n\tIdRevision: " + idRevision 
				+ " \n\tFecha: " + fecha 
				+ " \n\tDescripción: " + descripcion
				+ " \n\tTipo de Revision: " + tipoRevision 
				+ " \n\tPrecio Revisión: " + precioRevision + "\n" ;
	}
	
	
	
	
}
