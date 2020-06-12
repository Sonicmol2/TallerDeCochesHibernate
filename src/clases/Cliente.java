package clases;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotBlank;

@Entity()
@Table(name = "Cliente")
public class Cliente implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "DNI")
	@NotBlank
	@Pattern(regexp = "[0-9]{8}[A-Z]", message = "Error. Formato DNI del cliente incorrecto")
	@Size(max = 9)
	private String dni;
	@Column(name = "Nombre")
	@Size(min = 3, max = 20)
	private String nombre;
	@Column(name = "Apellidos")
	@Size(min = 3, max = 30)
	private String apellidos;

	@OneToMany
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	@JoinColumn(name = "Cliente_Pertenece")
	private List<Coche> listaCoches;

	public Cliente(String nombre, String apellidos, String dni) {

		this.nombre = nombre;
		this.apellidos = apellidos;
		this.dni = dni;
		this.listaCoches = new LinkedList<Coche>();
	}

	public Cliente() {
		this.listaCoches = new LinkedList<Coche>();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) throws TallerException {
		this.dni = dni;
	}

	public List<Coche> getListaCoches() {
		return listaCoches;
	}

	public void setListaCoches(List<Coche> listaCoches) {
		this.listaCoches = listaCoches;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dni == null) ? 0 : dni.hashCode());
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
		Cliente other = (Cliente) obj;
		if (dni == null) {
			if (other.dni != null)
				return false;
		} else if (!dni.equals(other.dni))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Cliente \n\tNombre: " + nombre + " \n\tApellidos: " + apellidos + " \n\tdni: " + dni
				+ " \n\tListaCoches: " + listaCoches + "\n";
	}

	public void annadirCoche(Coche coche) {

		this.listaCoches.add(coche);

	}

	public void borrarCoche(Coche coche) {

		this.listaCoches.remove(coche);

	}

}
