package principal;

import java.util.*;

import clases.Cliente;
import clases.Coche;
import clases.Revision;
import clases.TallerException;
import clases.TipoRevision;
import dao.ClienteDao;
import dao.CocheDao;
import dao.DaoGenerico;
import dao.HibernateUtil;
import dao.RevisionDao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.text.DateFormatter;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class PrincipalTaller {

	private static Scanner teclado = new Scanner(System.in);
	private static Session session;
	private static ClienteDao clienteDAO = new ClienteDao();
	private static CocheDao cocheDAO = new CocheDao();
	private static RevisionDao revisionDao = new RevisionDao();

	public static void main(String[] args) {

		int opcion;

		configurarSesion();

		try {

			do {
				opcion = mostrarMenu();
				tratarMenu(opcion);
			} while (opcion != 4);

			cerrarSesion();
		} catch (TallerException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Método para tratar el menu principal
	 * 
	 * @param opcion elegida del menú
	 * @throws TallerException
	 */
	private static void tratarMenu(int opcion) throws TallerException {

		int opcionCliente, opcionCoche, opcionRevision;

		switch (opcion) {
		case 1: {
			do {
				opcionCliente = mostrarMenuCliente();
				tratarMenuCliente(opcionCliente);
			} while (opcionCliente != 6);
			break;
		}
		case 2: {
			do {
				opcionCoche = mostrarMenuCoche();
				tratarMenuCoches(opcionCoche);
			} while (opcionCoche != 7);
			break;
		}
		case 3: {
			do {
				opcionRevision = mostrarMenuRevision();
				tratarMenuRevision(opcionRevision);
			} while (opcionRevision != 7);
			break;
		}
		}

	}

	/**
	 * Método para tratar el menú de las revisiones
	 * 
	 * @param opcionRevision elegida por el usuario
	 * @throws TallerException si los datos introducidos son correctos, si no existe
	 *                         el coche al que asignar la revisión
	 */
	private static void tratarMenuRevision(int opcionRevision) throws TallerException {

		String textoDescripcion, tipoRevision, matricula, dni;
		int idRevision;
		LocalDate fechaDate;

		try {
			switch (opcionRevision) {
			case 1:
				// Alta de revisión
				System.out.println("Vamos a introduir los datos de la revisión.");
				fechaDate = solicitarYFormatearFecha("Introduce la fecha(dd-MM-yyyy): ");
				textoDescripcion = solicitarCadena("Introduce la descripción de la revisión: ");
				tipoRevision = solicitarCadena("Introduce el tipo de revisión(Aceite, ruedas, motor, electronica): ")
						.toUpperCase();
				matricula = solicitarCadena("Introduce la matricula del coche de la revisión: ");
				darAltaRevision(fechaDate, textoDescripcion, tipoRevision, matricula);
				break;
			case 2:
				// Consulta de todas las revisiones entre dos fechas(Preguntar por que está mal
				// hecho)
				System.out.println("Vamos a introducir dos fechas para mostrar revisiones entre las 2 fechas.");
				consultarTodasLasRevisionesEntreFechas();
				break;
			case 3:
				// Consultar las revisiones de un cliente
				dni = solicitarCadena("Introduce el dni del cliente: ");
				consultarRevisionesDeUnCliente(dni);
				break;
			case 4:
				// Hacer la media de las revisiones
				matricula = solicitarCadena("Introduce la matricula del coche: ");
				hacerMediaPrecioRevisionesUnCoche(matricula);
				break;
			case 5:
				// Modificar algun dato de la revision del coche
				matricula = solicitarCadena("Introduce la matricula del coche: ");
				System.out.println(matricula);
				modificarDatoRevisionDeUnCoche(matricula);
				break;
			case 6:
				// Borrar la revisión por su id, pero antes buscamos por matricula ese coche
				// para borrar la revisión
				matricula = solicitarCadena("Introduce la matricula del coche: ");
				borrarRevisionPorSuId(matricula);
				break;
			}
		} catch (TallerException e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Método que sirve para hacer la media del precio de las revisiones de un coche
	 * en concreto
	 * 
	 * @param matricula del coche para calcular la media
	 * @throws TallerException false si no encuentra el coche o la lista de
	 *                         revisiones está vacía
	 */
	private static void hacerMediaPrecioRevisionesUnCoche(String matricula) throws TallerException {

		double mediaRevisiones, sumaPrecioRevisiones = 0;

		Coche coche = cocheDAO.buscarCochePorMatricula(matricula);

		if (coche == null) {
			throw new TallerException("Error. No hay coche con esa matricula");
		}

		mediaRevisiones = revisionDao.hacerMediaRevisiones(matricula);

		System.out.println("Coche con matrícula " + matricula + ", su media de precio de revisiones es: "
				+ mediaRevisiones + " €.");

	}

	/**
	 * Método para modificar la fecha de la revisión de un coche
	 * 
	 * @param idRevision a buscar para modificar
	 * @param matricula  del coche a buscar
	 * @throws TallerException erro si no hay coche con esa matrícula o no hay
	 *                         revisión con ese id
	 */

	// Este método habra que modificarlo para cambiar el dato que diga el usuario
	private static void modificarDatoRevisionDeUnCoche(String matricula) throws TallerException {

		int opcion, idRevision;
		char confirmar;

		Coche coche = cocheDAO.buscarCochePorMatricula(matricula);

		if (coche == null) {
			throw new TallerException("Error. No hay coche con esa matricula");
		}

		List<Revision> listaRevisionesDeUnCoche = coche.getListaRevisiones();

		listaRevisionesDeUnCoche.stream().forEach(revision -> System.out.println(revision));

		idRevision = solicitarNumero("Introduce el id de la revisión a modificar: ");

		Revision revision = revisionDao.consultarRevisionPorId(idRevision);

		if (revision == null) {
			throw new TallerException("Error. No hay revision con esa id");
		}

		do {
			opcion = mostrarMenuCambiarDatosRevision();

			revision = cambiarDatosRevision(opcion, revision);

			confirmar = solicitarConfirmacion("¿Quieres seguir modificando datos de la revisión?(S/N): ");
		} while (confirmar == 'S');

		revisionDao.guardar(revision);

		System.out.println("\nLos datos se han modificado correctamente.");
		System.out.println();
		System.out.println(revision + "\n");

	}

	private static Revision cambiarDatosRevision(int opcion, Revision revision) {

		LocalDate fechaNueva;
		int diaMesNuevo, mesNuevo, annoNuevo;
		String nuevaDescripcion, tipoRevisionNueva;
		double precioNuevo;

		switch (opcion) {
		case 1:
			System.out.println("Introduce la nueva fecha.");
			diaMesNuevo = solicitarNumero("Introduce el dia nuevo: ");
			mesNuevo = solicitarNumero("Introduce el mes nuevo: ");
			annoNuevo = solicitarNumero("Introduce el nuevo año: ");
			// Convertimos la nueva fecha para cambiarla
			fechaNueva = LocalDate.of(annoNuevo, mesNuevo, diaMesNuevo);
			revision.setFecha(fechaNueva);
			break;
		case 2:
			nuevaDescripcion = solicitarCadena("Introduce la nueva descripción de la revisión: ");
			revision.setDescripcion(nuevaDescripcion);
			break;
		case 3:
			tipoRevisionNueva = solicitarCadena("Introduce el nuevo tipo de la revisión: ");
			TipoRevision tipoRevisionNu = TipoRevision.valueOf(tipoRevisionNueva);
			precioNuevo = ponerPrecioDeRevision(tipoRevisionNu);
			revision.setTipoRevision(tipoRevisionNu);
			revision.setPrecioRevision(precioNuevo);
			break;
		}

		return revision;
	}

	private static int mostrarMenuCambiarDatosRevision() {
		int opcion = 0;

		do {
			System.out.println("¿Que quieres modificar de la revision?\n");
			System.out.println("\t|1| Cambiar la fecha de la revisión: ");
			System.out.println("\t|2| Cambiar la descripción de la revisión: ");
			System.out.println("\t|3| Cambiar tipo de Revisión(Al cambiarse se cambiara también el precio): ");
			System.out.println("\nIntroduce la opción: ");

			opcion = Integer.parseInt(teclado.nextLine());

		} while (opcion < 1 || opcion > 3);

		return opcion;

	}

	/**
	 * Método para borrar una revisión por su id
	 * 
	 * @param matricula
	 * @param idRevision de la revisión que queremos borrar
	 * @throws TallerException
	 */
	private static void borrarRevisionPorSuId(String matricula) throws TallerException {

		Coche coche = cocheDAO.buscarCochePorMatricula(matricula);
		int idRevision;

		if (coche == null) {
			throw new TallerException("Error. No coche con esa matricula.");
		}

		List<Revision> listaRevisionesDeUnCoche = coche.getListaRevisiones();

		listaRevisionesDeUnCoche.stream().forEach(revision -> System.out.println(revision));

		idRevision = solicitarNumero("Introduce el id de la revisión a borrar: ");

		// Preguntar a partir de aqui como hacerlo
		Revision revisionBorrar = revisionDao.consultarRevisionPorId(idRevision);

		if (revisionBorrar == null) {
			throw new TallerException("Error. No hay revisión con ese id");
		} else {

			coche.borrarRevision(revisionBorrar);
			revisionDao.borrar(revisionBorrar);

		}

		System.out.println("\nRevisión borrada correctamente.");
		System.out.println();
		System.out.println(revisionBorrar + "\n");
	}

	/**
	 * Método para consultar las revisiones de un cliente(Ordenado por fecha, no por
	 * su id)
	 * 
	 * @param dni del cliente que queremos saber sus revisiones
	 * @throws TallerException error si no tiene ninguna lista de revisiones
	 */
	private static void consultarRevisionesDeUnCliente(String dni) throws TallerException {

		List<Object[]> listaRevisionesDeUnCliente;

		listaRevisionesDeUnCliente = revisionDao.consultarRevisionesDeUnCliente(dni);

		// Si la lista esta vacia es que no hay clientes
		if (listaRevisionesDeUnCliente.isEmpty()) {
			throw new TallerException("Error. Ese cliente no tiene revisiones.");
		}

		// Preguntar como parsear para que muestre las fechas bien
		for (Object[] o : listaRevisionesDeUnCliente) {
			System.out.println("DNI: " + o[0] + ",\n\t Matricula: " + o[1] + ", Revision: " + o[2] + ", Fecha: " + o[3]
					+ ", Descripcion: " + o[4] + ", Precio: " + o[5] + "€.");
		}

	}

	/**
	 * Método para consultar todas las revisiones que hay
	 * 
	 * @throws TallerException error si la base de datos esta vacía
	 */
	private static void consultarTodasLasRevisionesEntreFechas() throws TallerException {

		List<Revision> listaRevisiones;
		LocalDate fechaStart = null;
		LocalDate fechaEnd = null;

		fechaStart = solicitarYFormatearFecha("Introduce la primera fecha(dd-mm-aaaa): ");
		fechaEnd = solicitarYFormatearFecha("Introduce la segunda fecha(dd-mm-aaaa): ");

		listaRevisiones = revisionDao.consultarTodasRevisionesEntreDosFechas(fechaStart, fechaEnd);

		if (listaRevisiones.isEmpty()) {
			throw new TallerException("Error. Base de datos vacía");
		}

		listaRevisiones.stream().forEach(revision -> System.out.println(revision));

	}

	private static LocalDate solicitarYFormatearFecha(String msg) {

		boolean esCorrecta;
		String fechaCadena;
		LocalDate fechaFormateada = null;

		DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		do {
			try {
				System.out.println(msg);
				fechaCadena = teclado.nextLine();
				fechaFormateada = LocalDate.parse(fechaCadena, formato);
				esCorrecta = true;
			} catch (DateTimeParseException ex) {
				System.out.println("Formato fecha incorrecto.");
				esCorrecta = false;
			}
		} while (esCorrecta == false);

		return fechaFormateada;
	}

	/**
	 * Método para dar de alta a la revisión de un coche
	 * 
	 * @param fechaDate2
	 * 
	 * @param diaMes           dia de la fecha
	 * @param mes              de la fecha
	 * @param anno             año de la fecha
	 * @param textoDescripcion descripcion de la revisión
	 * @param tipoRevision     el tipo de revisión si es PERIODICA o NO PERIODICA
	 * @param matricula        del coche que pertenecerá la revisión nueva
	 * @throws TallerException error si no existe el coche que se le asignará la
	 *                         revisión
	 */

	// El precio no tengo que pedirlo se pone automaticamente
	private static void darAltaRevision(LocalDate fechaDate, String textoDescripcion, String tipoRevision,
			String matricula) throws TallerException {

		double precioRevision = 0;

		LocalDate fechaActual = LocalDate.now();// Cogemos fecha actual

		Coche coche = cocheDAO.buscarCochePorMatricula(matricula);

		if (coche == null) {
			throw new TallerException("Error. No hay coche con esa matricula.");
		}

		if (fechaDate.isBefore(fechaActual)) {
			throw new TallerException("Error. No puedes introducir una fecha anterior a la fecha de hoy(actual).");
		} else {
			// Recogemos el tipo de revisión
			if (tipoRevision.isEmpty()) {
				throw new TallerException("Error. No puedes dejar el dato tipo revisión vacío.");
			} else {
				TipoRevision tipoRevisionT = TipoRevision.valueOf(tipoRevision);

				precioRevision = ponerPrecioDeRevision(tipoRevisionT);

				// Creamos la nueva revisión
				Revision revision = new Revision(fechaDate, textoDescripcion, tipoRevisionT, precioRevision, coche);

				// Añadimos la nueva revisión a la lista de revisiones del coche
				coche.annadirRevision(revision);

				// Guardamos el coche para asi crear la nueva revisión y también actualizar la
				// lista de las revisiones
				cocheDAO.guardar(coche);

				System.out.println("\nRevisión creada correctamente.");
				System.out.println();
				System.out.println(revision);
			}
		}

	}

	/**
	 * Método que sirve para poner un precio según el tipo de revisión que hayamos
	 * elegido
	 * 
	 * @param tipoRevisionT
	 * @return
	 */
	private static double ponerPrecioDeRevision(TipoRevision tipoRevisionT) {

		double precio = 0;

		switch (tipoRevisionT) {
		case ACEITE:
			precio = 60;
			break;
		case RUEDAS:
			precio = 80;
			break;
		case MOTOR:
			precio = 100;
			break;
		case ELECTRONICA:
			precio = 130;
			break;
		}

		return precio;
	}

	private static void tratarMenuCoches(int opcionCoche) throws TallerException {

		String marca, modelo, matricula;
		String dniCliente;

		try {
			switch (opcionCoche) {
			case 1:
				// Alta a un nuevo coche
				marca = solicitarCadena("Introduce la marca del coche: ");
				modelo = solicitarCadena("Introduce el modelo del coche: ");
				matricula = solicitarCadena("Introduce la matricula del coche(LLLL-NNN):");
				dniCliente = solicitarCadena("Introduce el dni del cliente del coche:");
				darAltaCoche(marca, modelo, matricula, dniCliente);
				break;
			case 2:
				// Consultar todos los coches, ordenados por su matrícula
				consultarTodosLosCoches();
				break;
			case 3:
				// Consultar las revisiones de un coche en concreto, ordenados por la fecha
				matricula = solicitarCadena("Introduce la matricula del coche(LLLL-NNN) para ver sus revisiones:");
				consultarRevisionesDeUnCoche(matricula);
				break;
			case 4:
				// Consultar los coches que dispone un cliente.
				dniCliente = solicitarCadena("Introduce el dni del cliente del coche:");
				consultarCochesDeUnCliente(dniCliente);
				break;
			case 5:
				// Modificar cualquier dato(Preguntar no se hacerlo)
				matricula = solicitarCadena("Introduce la matricula del coche a modificar(LLLL-NNN):");
				modificarDatoDeUnCoche(matricula);
				break;
			case 6:
				// Borrar el coche
				matricula = solicitarCadena("Introduce la matricula del coche a borrar(LLLL-NNN):");
				borrarCochePorMatricula(matricula);
				break;
			}
		} catch (TallerException e) {
			System.out.println(e.getMessage());
		}

	}

	private static void consultarCochesDeUnCliente(String dniCliente) throws TallerException {

		List<Coche> listaCochesCliente;
		Cliente cliente;

		cliente = clienteDAO.buscarClientePorDni(dniCliente);

		if (cliente == null) {
			throw new TallerException("Error. No hay ningún cliente con ese DNI.");
		}

		listaCochesCliente = cliente.getListaCoches();

		if (listaCochesCliente.isEmpty()) {
			throw new TallerException("Error. Este cliente no tiene coches.");
		}

		// Preguntar si hacer el print del cliente solamente o solo sus coches
		System.out.println(
				"Cliente " + cliente.getNombre() + " " + cliente.getApellidos() + " con DNI: " + cliente.getDni());
		listaCochesCliente.stream().forEach(coche -> System.out.println(coche));

	}

	// Segunda opción
	private static void consultarCochesDeUnCliente2(String dniCliente) throws TallerException {

		List<Coche> listaCochesCliente;

		listaCochesCliente = cocheDAO.consultarCochesCliente(dniCliente);

		if (listaCochesCliente.isEmpty()) {
			throw new TallerException("Error. No hay ningún cliente con ese DNI.");
		}

		listaCochesCliente.stream().forEach(coche -> System.out.println(coche));

	}

	/**
	 * Método para consultar las revisiones de un coche
	 * 
	 * @param matricula del coche que queremos consultar sus revisiones
	 * @throws TallerException error si no hay coche con esa matricula o tiene la
	 *                         lista de revisiones vacia
	 */
	private static void consultarRevisionesDeUnCoche(String matricula) throws TallerException {

		List<Revision> listaCochesRevisiones;
		Coche coche;

		coche = cocheDAO.buscarCochePorMatricula(matricula);

		// Si la lista esta vacia es que no hay clientes
		if (coche == null) {
			throw new TallerException("Error. No hay coche con esa matricula.");
		}

		listaCochesRevisiones = coche.getListaRevisiones();

		if (listaCochesRevisiones.isEmpty()) {
			throw new TallerException("Este coche no tiene revisiones");
		}
		// Expresion Lambda para mostrar la lista de revisiones de un coche ordenada por
		// la fecha
		listaCochesRevisiones.stream().sorted(new Comparator<Revision>() {

			@Override
			public int compare(Revision rev1, Revision rev2) {

				return rev1.getFecha().compareTo(rev2.getFecha());
			}
		}).forEach(revision -> System.out.println(revision));

	}

	/**
	 * Método para borrar un coche por su matrícula
	 * 
	 * @param matricula del coche que queremos borrar
	 * @throws TallerException error si no existe el coche con esa matricula
	 */
	private static void borrarCochePorMatricula(String matricula) throws TallerException {
		char confirmacion;

		Coche coche = cocheDAO.buscarCochePorMatricula(matricula);

		if (coche == null) {
			throw new TallerException("Error. No hay ningún coche con esa matricula.");
		} else {
			confirmacion = solicitarConfirmacion("¿Estás seguro que quieres borrar el coche?(S/N)");
			if (confirmacion == 'S') {

				Cliente cliente = coche.getCliente();

				cliente.borrarCoche(coche);

				cocheDAO.borrar(coche);

				System.out.println("\nCoche borrado correctamente.");
				System.out.println();
				System.out.println(coche + "\n");
			}
		}

	}

	/**
	 * Método para modificar una matricula de un coche
	 * 
	 * @param matricula del coche que queremos cambiar
	 * @throws TallerException error si no hay ningun coche con esa matrícula
	 */
	private static void modificarDatoDeUnCoche(String matricula) throws TallerException {

		String nuevaModelo;
		int opcion = 0;
		char confirmar = 0;

		Coche coche = cocheDAO.buscarCochePorMatricula(matricula);

		if (coche == null) {
			throw new TallerException("Error. Lo sentimos no hay coches con esa matrícula.");
		}

		do {
			opcion = mostrarCambiosDatosCoche();

			coche = cambiarDatosDelCoche(coche, opcion);

			confirmar = solicitarConfirmacion("¿Quieres seguir cambiando datos(S/N)?: ");
		} while (confirmar == 'S');

		cocheDAO.guardar(coche);

		session.refresh(coche);

		System.out.println("\nModelo del coche modificado correctamente.");
		System.out.println();
		System.out.println(coche + "\n");

	}

	private static Coche cambiarDatosDelCoche(Coche coche, int opcion) throws TallerException {

		String marcaCoche, modeloCoche;

		switch (opcion) {
		case 1:
			marcaCoche = solicitarCadena("Introduce la nueva marca del coche: ");
			coche.setMarca(marcaCoche);
			break;
		case 2:
			modeloCoche = solicitarCadena("Introduce el nuevo modelo del coche: ");
			coche.setModelo(modeloCoche);
			break;
		}

		return coche;
	}

	private static int mostrarCambiosDatosCoche() {

		int opcion = 0;

		do {
			System.out.println("¿Que quieres modificar del coche?\n");
			System.out.println("\t|1| Cambiar solamente la marca del coche: ");
			System.out.println("\t|2| Cambiar solamente el modelo del coche: ");
			System.out.println("\nIntroduce la opción: ");

			opcion = Integer.parseInt(teclado.nextLine());

		} while (opcion < 1 || opcion > 2);
		return opcion;
	}

	/**
	 * Método para consultar todos los coches
	 * 
	 * @throws TallerException si la base de datos de coches está vacía
	 */
	private static void consultarTodosLosCoches() throws TallerException {

		List<Coche> listaTodosCoches;

		listaTodosCoches = cocheDAO.consultarTodosLosCoches();

		if (listaTodosCoches.isEmpty()) {
			throw new TallerException("Error. Base de datos de los coches vacia.");
		}

		listaTodosCoches.stream().forEach(coche -> System.out.println(coche));
	}

	/**
	 * Método para dar de alta a un nuevo coche de un cliente
	 * 
	 * @param marca      del coche
	 * @param modelo     del coche
	 * @param matricula  del coche
	 * @param dniCliente del cliente que pertenece el coche
	 * @throws TallerException error si no hay ningún cliente con ese dni o error
	 *                         por ya existir un coche con esa matrícula
	 */
	private static void darAltaCoche(String marca, String modelo, String matricula, String dniCliente)
			throws TallerException {

		// Comprobamos si existe ya el coche con esa matricula
		Coche cochePatron = cocheDAO.buscarCochePorMatricula(matricula);

		if (cochePatron != null) {
			throw new TallerException("Error. Ya hay un coche con esa matricula");
		}

		// Comprobamos que exista el cliente , buscamos por el dni
		Cliente cliente = clienteDAO.buscarClientePorDni(dniCliente);

		if (cliente == null) {
			throw new TallerException("Error. No hay cliente con ese dni");
		}

		// Creamos el nuevo coche con su cliente
		Coche coche = new Coche(marca, modelo, matricula, cliente);

		// Añadimos el coche a la lista de coches del cliente
		cliente.annadirCoche(coche);

		// Guardamos el cliente con el coche ya metido en la lista
		clienteDAO.guardar(cliente);

		System.out.println("\nCoche creado correctamente.");
		System.out.println();
		System.out.println(coche);

	}

	/**
	 * Método para tratar el Menú de los clientes
	 * 
	 * @param opcionCliente la opción que hemos elegido para el menú
	 * @throws TallerException si no introducidos bien los datos, o que la base de
	 *                         datos esta vacía o que no se encuentra el cliente o
	 *                         ya existe ese cliente
	 */
	private static void tratarMenuCliente(int opcionCliente) throws TallerException {

		String nombreCliente, apellidosCliente, dni;

		try {
			switch (opcionCliente) {
			case 1:
				// Alta de un nuevo cliente
				nombreCliente = solicitarCadena("Introduce el nombre del cliente: ");
				apellidosCliente = solicitarCadena("Introduce los apellidos del nuevo cliente: ");
				dni = solicitarCadena("Introduce el dni del nuevo cliente: ");
				altaCliente(nombreCliente, apellidosCliente, dni);
				break;

			case 2:
				// Consultar todos los clientes
				consultarTodosClientes();
				break;
			case 3:
				// Consultar todos los clientes que contengan un apellido
				apellidosCliente = solicitarCadena("Introduce el apellido del o de los clientes a consultar: ");
				consultarClientesPorApellidos(apellidosCliente);
				break;
			case 4:
				// Modificar algun dato del cliente
				dni = solicitarCadena("Introduce el dni del cliente a modificar: ");
				modificarNombreOApellidosDeCliente(dni);
				break;
			case 5:
				// Borrar el cliente por su DNI, se borrará también la lista de coches del
				// cliente elegido
				dni = solicitarCadena("Introduce el dni del cliente que quieres borrar: ");
				borrarClientePorDni(dni);
				break;
			}
		} catch (TallerException e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Método para borrar un cliente(Se borrará todo)
	 * 
	 * @param dni del cliente que queremos borrar
	 * @throws TallerException error si no hay cliente con ese dni
	 */
	private static void borrarClientePorDni(String dni) throws TallerException {

		char confirmacion;

		Cliente cliente = clienteDAO.buscarClientePorDni(dni);

		if (cliente == null) {
			throw new TallerException("Error. No se ha encontrado el cliente con ese dni.");
		} else {
			confirmacion = solicitarConfirmacion("¿Estás seguro que quieres borrar el cliente?(S/N)");

			if (confirmacion == 'S') {
				clienteDAO.borrar(cliente);
				// session.refresh(cliente);
			}

			System.out.println("\nCliente borrado correctamente.");
			System.out.println();
			System.out.println(cliente + "\n");
		}
	}

	/**
	 * Método para modificar un nombre de un cliente
	 * 
	 * @param dni del cliente que queremos modificar el nombre
	 * @throws TallerException error si no hay cliente con ese dni
	 */
	private static void modificarNombreOApellidosDeCliente(String dni) throws TallerException {

		int opcion = 0;
		// Buscamos el cliente con ese dni
		Cliente cliente = clienteDAO.buscarClientePorDni(dni);

		if (cliente == null) {
			throw new TallerException("Error. No se ha encontrado el cliente con ese dni.");
		}

		opcion = mostrarOpcionesCambiarDatos();

		// Puedo meterlo dentro de un método
		cliente = cambiarDatosCliente(opcion, cliente);

		clienteDAO.guardar(cliente);

		System.out.println("\nDatos del cliente modificados correctamente.");
		System.out.println();
		System.out.println(cliente + "\n");

	}

	private static int mostrarOpcionesCambiarDatos() {

		int opcion = 0;

		do {
			System.out.println("¿Que quieres modificar?\n");
			System.out.println("\t|1| Cambiar solamente el nombre del cliente: ");
			System.out.println("\t|2| Cambiar solamente los apellidos del cliente: ");
			System.out.println("\t|3| Cambiar nombre y apellidos: ");
			System.out.println("\nIntroduce la opción: ");

			opcion = Integer.parseInt(teclado.nextLine());

		} while (opcion < 1 || opcion > 3);
		return opcion;
	}

	/**
	 * Método para cambiar los datos de un cliente
	 * 
	 * @param opcion  que elegimos para cambiar los datos
	 * @param cliente que se va a modificar los datos
	 * @return cliente con los datos ya modificados
	 */
	private static Cliente cambiarDatosCliente(int opcion, Cliente cliente) {

		String nombreNuevo, nuevosApellidos;

		switch (opcion) {
		case 1:
			// Pedimos el nuevo nombre del cliente
			nombreNuevo = solicitarCadena("Introduce el nuevo nombre del cliente: ");
			cliente.setNombre(nombreNuevo);
			break;
		case 2:
			// Pedimos el nuevo nombre del cliente
			nuevosApellidos = solicitarCadena("Introduce los nuevos apellidos del cliente: ");
			cliente.setApellidos(nuevosApellidos);
			break;
		case 3:
			nombreNuevo = solicitarCadena("Introduce el nuevo nombre del cliente: ");
			nuevosApellidos = solicitarCadena("Introduce los nuevos apellidos del cliente: ");
			cliente.setNombre(nombreNuevo);
			cliente.setApellidos(nuevosApellidos);
			break;
		}

		return cliente;
	}

	/**
	 * Método para consultar todos los clientes
	 * 
	 * @throws TallerException si no hay clientes en la base de datos
	 */
	public static void consultarTodosClientes() throws TallerException {

		List<Cliente> listaClientes;

		listaClientes = clienteDAO.consultarTodosClientes();

		// Si la lista esta vacia es que no hay clientes
		if (listaClientes.isEmpty()) {
			throw new TallerException("Error. No hay clientes.");
		}

		listaClientes.stream().forEach(cliente -> System.out.println(cliente));

	}

	/**
	 * Método para consultar todos los clientes que se llamen iguales ordenados
	 * alfabeticamente por los apellidos
	 * 
	 * @param nombre de los clientes a buscar
	 * @return una lista con todos los clientes con ese nombre
	 */
	private static void consultarClientesPorApellidos(String apellido) throws TallerException {

		List<Cliente> listaClientes;

		listaClientes = clienteDAO.consultarClientesPorApellidos(apellido);

		// Si la lista esta vacia es que no hay clientes

		if (apellido.length() == 0) {
			throw new TallerException("Error. No puedes dejar los datos vacios.");
		}

		if (listaClientes.isEmpty()) {
			throw new TallerException("Error. No hay clientes con ese apellido.");
		}

		listaClientes.stream().sorted(new Comparator<Cliente>() {

			@Override
			public int compare(Cliente cliente1, Cliente cliente2) {

				return cliente1.getApellidos().compareTo(cliente2.getApellidos());
			}
		}).forEach(cliente -> System.out.println(cliente));
	}

	/**
	 * Método para dar de alta a un nuevo cliente
	 * 
	 * @param nombreCliente    del nuevo cliente
	 * @param apellidosCliente del nuevo cliente
	 * @param dni              del nuevo cliente
	 * @throws TallerException dará error si ya hay un cliente con el mismo DNI
	 */
	private static void altaCliente(String nombreCliente, String apellidosCliente, String dni) throws TallerException {

		Cliente clienteNuevo;
		Cliente cliente = clienteDAO.buscarClientePorDni(dni);

		if (cliente != null) {
			throw new TallerException("Error. Ya existe un cliente con ese dni.");
		} else {

			clienteNuevo = new Cliente(nombreCliente, apellidosCliente, dni);

			clienteDAO.guardar(clienteNuevo);
		}

		System.out.println("\nCliente creado correctamente.");
		System.out.println();
		System.out.println(clienteNuevo + "\n");

	}

	/**
	 * Método que muestra las opciones del menú de Revision y recoge la opción
	 * 
	 * @return opcion que hemos elegido
	 */
	private static int mostrarMenuRevision() {
		int opcion = 0;

		do {
			System.out.println("[1] Alta nueva revisión.");
			System.out.println("[2] Consultar todas las revisiones entre 2 fechas.");
			System.out.println("[3] Consultar revisiónes de un cliente.");
			System.out.println("[4] Hacer la media del precio de las revisiones de un coche.");
			System.out.println("[5] Modificar fecha o algún otro dato de la revisión.");
			System.out.println("[6] Borrar revision.");
			System.out.println("[7] Salir");

			opcion = Integer.parseInt(teclado.nextLine());
		} while (opcion < 1 || opcion > 7);

		return opcion;
	}

	/**
	 * Método que muestra las opciones del menú de Coche y recoge la opción
	 * 
	 * @return opcion que hemos elegido
	 */
	private static int mostrarMenuCoche() {

		int opcion = 0;

		do {
			System.out.println("[1] Alta nuevo coche.");
			System.out.println("[2] Consultar todos los coches.");
			System.out.println("[3] Consulta lista de revisiones de un coche por matrícula.");
			System.out.println("[4] Consultar los coches que dispone un cliente.");
			System.out.println("[5] Modificar modelo u otro dato de un coche.");
			System.out.println("[6] Borrar coche por su matrícula.");
			System.out.println("[7] Salir");

			opcion = Integer.parseInt(teclado.nextLine());

		} while (opcion < 1 || opcion > 7);
		return opcion;
	}

	/**
	 * Método que muestra las opciones del menú de Cliente y recoge la opción
	 * 
	 * @return opcion que hemos elegido
	 */
	private static int mostrarMenuCliente() {

		int opcion = 0;

		do {
			System.out.println("[1] Alta nuevo cliente.");
			System.out.println("[2] Consultar todos los clientes.");
			System.out.println("[3] Consultar clientes por un apellido: ");
			System.out.println("[4] Modificar nombre o apellidos de un cliente:");
			System.out.println("[5] Borrar un cliente por el dni.");
			System.out.println("[6] Salir.");

			opcion = Integer.parseInt(teclado.nextLine());

		} while (opcion < 1 || opcion > 6);
		return opcion;

	}

	/**
	 * Método que muestra las opciones del menú Principal y recoge la opción
	 * 
	 * @return opcion que hemos elegido
	 */
	private static int mostrarMenu() {

		int opcion = 0;

		do {
			System.out.println("[1] Cliente");
			System.out.println("[2] Coche");
			System.out.println("[3] Revision");
			System.out.println("[4] Salir");

			opcion = Integer.parseInt(teclado.nextLine());

		} while (opcion < 1 || opcion > 4);

		return opcion;
	}

	/**
	 * Solicita un número
	 * 
	 * @param msg texto que mostramos a la hora de solicitar
	 * @return numero introducido
	 */
	private static int solicitarNumero(String msg) {

		int numero = 0;

		System.out.println(msg);
		numero = Integer.parseInt(teclado.nextLine());

		return numero;
	}

	private static double solicitarPrecio(String msg) {

		double numero = 0;

		System.out.println(msg);
		numero = Double.parseDouble(teclado.nextLine());

		return numero;
	}

	/**
	 * Método que solicita una cadena
	 * 
	 * @param msg texto que mostramos a la hora de solicitar
	 * @return cadena introducida
	 */
	private static String solicitarCadena(String msg) {

		String cadena;

		System.out.println(msg);
		cadena = teclado.nextLine();

		return cadena;
	}

	private static char solicitarConfirmacion(String msg) {

		char letra;

		System.out.println(msg);
		letra = teclado.nextLine().toUpperCase().charAt(0);

		return letra;
	}

	/**
	 * 
	 */
	private static void cerrarSesion() {
		HibernateUtil.closeSessionFactory();
	}

	/**
	 * 
	 */
	private static void configurarSesion() {
		HibernateUtil.buildSessionFactory();
		HibernateUtil.openSessionAndBindToThread();
		session = HibernateUtil.getSessionFactory().getCurrentSession();

	}

}
