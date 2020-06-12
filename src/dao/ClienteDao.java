package dao;

import java.util.List;
import java.util.Scanner;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import clases.Cliente;
import clases.Coche;
import clases.TallerException;

public class ClienteDao extends DaoGenerico<Cliente> {

	/**
	 * Método para consultar todos los clientes que se llamen iguales
	 * 
	 * @param nombre de los clientes a buscar
	 * @return una lista con todos los clientes con ese nombre
	 */
	public static List<Cliente> consultarClientesPorApellidos(String apellidos) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		List<Cliente> listaClientesNombres;

		Query query = session.createQuery("SELECT c FROM Cliente c WHERE Apellidos  LIKE'%" + apellidos + "%' ORDER BY c.dni desc");
		listaClientesNombres = query.list();

		return listaClientesNombres;

	}

	/**
	 * Métoto para buscar un cliente por su dni
	 * 
	 * @param dni del cliente a buscar
	 * @return devuelve el cliente con ese dni
	 */
	public static Cliente buscarClientePorDni(String dni) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		Cliente cliente = (Cliente) session.get(Cliente.class, dni);

		return cliente;
	}

	/**
	 * Método para consultar todos los clientes
	 * 
	 * @return devuelve una lista con todos los clientes
	 */
	public static List<Cliente> consultarTodosClientes() {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		List<Cliente> listaClientes;

		Query query = session.createQuery("SELECT c FROM Cliente c");

		listaClientes = query.list();

		return listaClientes;

	}

}
