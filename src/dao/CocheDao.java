package dao;

import java.util.List;
import java.util.Scanner;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import clases.Cliente;
import clases.Coche;
import clases.TallerException;

public class CocheDao extends DaoGenerico<Coche> {

	public static List<Coche> consultarTodosLosCoches() {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		List<Coche> listaCoches;

		Query query = session.createQuery("SELECT c FROM Coche c");

		listaCoches = query.list();

		return listaCoches;

	}

	
	public static Coche buscarCochePorMatricula(String matricula) throws TallerException {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		Coche coche = (Coche) session.createQuery("SELECT c FROM Coche c WHERE c.matricula = '" + matricula + "'").uniqueResult();

		return coche;
	}
	
	public static void cambiarMatricula(Coche coche, String nuevaMatricula) throws TallerException {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		Coche c = new Coche();
		c.setCliente(coche.getCliente());
		c.setMarca(coche.getMarca());
		c.setModelo(coche.getModelo());
		c.setListaRevisiones(coche.getListaRevisiones());
		c.setMatricula(nuevaMatricula);
		
		session.delete(coche);
		
		session.save(c);
		
		session.getTransaction().commit();
	}


	public List<Coche> consultarCochesCliente(String dniCliente) {
		
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();

		List<Coche> listaCoches;

		Query query = session.createQuery("SELECT c FROM Coche c WHERE c.Cliente_Pertenece = '" + dniCliente + "'");

		listaCoches = query.list();

		return listaCoches;
	}

	
}
