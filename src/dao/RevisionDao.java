package dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import clases.Cliente;
import clases.Coche;
import clases.Revision;
import clases.TallerException;
import clases.TipoRevision;

public class RevisionDao extends DaoGenerico<Revision> {

	public static Revision consultarRevisionPorId(int id) throws TallerException {

		Revision revision;

		Session session2 = HibernateUtil.getSessionFactory().getCurrentSession();

		revision = (Revision) session2.get(Revision.class, id);

		return revision;

	}

	public static List<Revision> consultarRevisionesPorMatricula(String matricula) throws TallerException {

		Session session2 = HibernateUtil.getSessionFactory().getCurrentSession();

		List<Revision> listaRevisionesFechaMatricula;

		// Consulta por una fecha y por matricula
		Query query = session2.createQuery("SELECT r FROM Revision r, Coche c WHERE c.matricula = '" + matricula + "'");
		listaRevisionesFechaMatricula = query.list();

		return listaRevisionesFechaMatricula;

	}

	public static List<Object[]> consultarRevisionesDeUnCliente(String dni) throws TallerException {

		Session session2 = HibernateUtil.getSessionFactory().getCurrentSession();
		
		List<Object[]> listaRevisionesDeUnCliente = null;

		// Consulta las revisiones de un cliente
		
		String sentenciaHQL = "select r.coche.cliente.dni as DNI, r.coche.matricula as Matricula, r.idRevision as Id, r.fecha as Fecha, r.descripcion as Descripción, r.precioRevision as Precio  "
				+ "from Revision r where r.coche.cliente.dni = '" + dni + "' order by r.fecha desc";
		
		Query query2 = session2.createQuery(sentenciaHQL);

		listaRevisionesDeUnCliente = query2.list();

		return listaRevisionesDeUnCliente;

	}

	public List<Revision> consultarTodasRevisionesEntreDosFechas(LocalDate fechaStart, LocalDate fechaEnd) {

		Session session2 = HibernateUtil.getSessionFactory().getCurrentSession();

		List<Revision> listaRevisiones;

		Query query = session2.createQuery("SELECT r FROM Revision r WHERE fecha BETWEEN :start AND :end ORDER BY r.fecha desc");
		query.setParameter("start", fechaStart);
		query.setParameter("end", fechaEnd);

		listaRevisiones = query.list();

		return listaRevisiones;
	}

	public double hacerMediaRevisiones(String matricula) {
		Session session2 = HibernateUtil.getSessionFactory().getCurrentSession();
		
		String sentenciaSQL ="SELECT avg(Precio_Revision) FROM Revision r WHERE r.Matricula_Revisiones = '" + matricula + "'";
		
		return (double) session2.createSQLQuery(sentenciaSQL).uniqueResult();

	}
}
