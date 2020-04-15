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

	public static List<Object[]> consultarRevisionesDeUnCliente(String dni)
			throws TallerException {

		Session session2 = HibernateUtil.getSessionFactory().getCurrentSession();
		String sentenciaSQL = "select c.DNI as \"DNI Cliente\", co.Matricula as \"Matricula Coche\", r.idRevision as \"Cod. Revision\", r.Descripcion as \"Descripción\" "
				+ "from Cliente c inner join Coche co on c.DNI = co.Cliente_Pertenece inner join Revision r on co.Matricula = r.Matricula_Revisiones "
				+ "where c.DNI = '" + dni + "'";

		List<Object[]> listaRevisionesDeUnCliente = null;

		
		//Consulta las revisiones de un cliente
		Query query = session2
				.createQuery("SELECT c.dni, co.matricula, r.idRevision, r.descripcion, r.precioRevision FROM Revision r JOIN r.coche co JOIN"
						+ " co.cliente c WHERE co.cliente = '" + dni + "' ORDER BY r.fecha desc");
		
//		Query query2 = session2
//				.createSQLQuery(sentenciaSQL);
		
		listaRevisionesDeUnCliente = query.list();

		return listaRevisionesDeUnCliente;

	}

	public List<Revision> consultarTodasRevisiones(LocalDate fechaDate, LocalDate fechaActual) {

		Session session2 = HibernateUtil.getSessionFactory().getCurrentSession();

		List<Revision> listaRevisiones;

		Query query = session2.createQuery("SELECT r FROM Revision r WHERE r.fecha BETWEEN " + fechaDate + " AND " + fechaActual + " ORDER BY r.fecha desc");

		listaRevisiones = query.list();

		return listaRevisiones;
	}
}
