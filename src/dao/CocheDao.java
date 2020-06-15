package dao;

import java.util.List;
import java.util.Scanner;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import clases.Cliente;
import clases.Coche;
import clases.Revision;
import clases.TallerException;

public class CocheDao extends DaoGenerico<Coche> {

	public List<Coche> consultarTodosLosCoches() {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		List<Coche> listaCoches;

		Query query = session.createQuery("SELECT c FROM Coche c");

		listaCoches = query.list();

		return listaCoches;

	}

	public Coche buscarCochePorMatricula(String matricula) throws TallerException {

		Coche coche;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		// Coche coche = (Coche) session.createQuery("SELECT c FROM Coche c WHERE
		// c.matricula = '" + matricula + "'").uniqueResult();
		// Session session2 = HibernateUtil.getSessionFactory().getCurrentSession();

		coche = (Coche) session.get(Coche.class, matricula);

		return coche;
	}

	public List<Coche> consultarCochesCliente(String dniCliente) {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		List<Coche> listaCoches;

		Query query = session.createQuery("SELECT c FROM Coche c WHERE c.Cliente_Pertenece = '" + dniCliente + "'");

		listaCoches = query.list();

		return listaCoches;
	}

}
