package dao;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.hibernate.Session;

import clases.TallerException;

public class DaoGenerico<T> {

	public void guardar(T entidad) throws TallerException {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			session.save(entidad);
			session.getTransaction().commit();
			
		} catch (ConstraintViolationException cve) {
			session.getTransaction().rollback();
			session.clear();
			System.err.println("No se ha podido insertar el cliente debido a los siguientes errores:");
			
			for (ConstraintViolation constraintViolation : cve.getConstraintViolations()) {
				System.out.println("En el campo '" + constraintViolation.getPropertyPath() + "': "
						+ constraintViolation.getMessage());
			}
			throw new TallerException("\nError. No se ha podido crear.");
		}
	}

	public void borrar(T entidad) throws TallerException {

		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		
		try{
			session.beginTransaction();
			session.delete(entidad);
			session.getTransaction().commit();
		} catch (Exception e) {
			session.getTransaction().rollback();
			System.out.println(e.getMessage() + "\n");
		}
		
	}
}
