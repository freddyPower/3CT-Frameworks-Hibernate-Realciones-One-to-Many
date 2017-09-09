package com.tresct.test;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.management.OperationsException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.tresct.dto.Avaluo;
import com.tresct.dto.DiarioCliente;
import com.tresct.dto.DiarioCliente_;
import com.tresct.dto.Tramite;
import com.tresct.dto.Tramite_;
import com.tresct.util.HibernateUtil;

//Esta clase demeustra como realizar operaciones crud en relaciones one to one
public class Test {

	public static void main(String[] args) {

		try {
			// configuracion();
			// consultarTramitesDeDiario();

			consultarDiariosDeTramite();

		} catch (Exception hbe) {
			hbe.printStackTrace();
			System.out.println("HibernateeXCEPTION: " + hbe.getMessage());
		}
	}

	public static void configuracion() {

		Session sesion = null;

		try {
			sesion = HibernateUtil.getSessionFactory().openSession();
			sesion.beginTransaction();

			Timestamp time = new Timestamp(new Date().getTime());
			Tramite t1 = new Tramite("Ampliacion", time);
			Tramite t2 = new Tramite("Ampliacion2", time);

			sesion.save(t1);
			sesion.save(t2);

			DiarioCliente dc1 = new DiarioCliente("Entrada1", time);
			DiarioCliente dc2 = new DiarioCliente("Entrada2", time);
			DiarioCliente dc3 = new DiarioCliente("Entrada3", time);

			// Asiganr a cada diario de cliente un tramite
			dc1.setTramite(t1);
			dc2.setTramite(t1);
			dc3.setTramite(t2);

			sesion.save(dc1);
			sesion.save(dc2);
			sesion.save(dc3);

			// Que pasa si quiero asosciar un tramite existente a un nuevo diario de cliente
			// Tramite tx = sesion.load(Tramite.class, 10);
			// DiarioCliente dcx = new DiarioCliente("NuevaEntrada" , time);
			// dcx.setTramite(tx);
			// sesion.save(dcx);

		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			sesion.getTransaction().rollback();
			sesion.close();
		} finally {
			sesion.close();
		}
	}

	// Consultas para relaciones onetoMany tramites que aparecen en diarioCliente
	public static void consultarTramitesDeDiario() {
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();

			// Consulta de todos los tramites que aparecen en diarioCliente
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<Tramite> criteria = builder.createQuery(Tramite.class);
			Root<Tramite> root = criteria.from(Tramite.class);

			Join<Tramite, DiarioCliente> join = root.join(Tramite_.diarioClienteSet);
			criteria.select(root).distinct(true);

			List<Tramite> results = session.createQuery(criteria).getResultList();
			System.out.println("Numero de tramites: " + results.size());
			for (Tramite t : results) {
				System.out.println("Elemento: " + t.toString());
			}

		} catch (HibernateException hbe) {
			System.out.println("HibernateException: " + hbe);
			session.getTransaction().rollback();
			session.close();

		} finally {
			session.close();
		}

	}

	public static void consultarDiariosDeTramite() {
		Session session = null;

		try {
			session = HibernateUtil.getSessionFactory().openSession();

			// Consultar todos los diarios de un trámite
			// Consulta de todos los tramites que aparecen en diarioCliente
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<DiarioCliente> criteria = builder.createQuery( DiarioCliente.class );
			Root<DiarioCliente> root = criteria.from( DiarioCliente.class );
			
			Join<DiarioCliente, Tramite> join = root.join(DiarioCliente_.tramite);
			
			criteria.where(
					builder.equal(root.get(DiarioCliente_.tramite), session.load(Tramite.class, 17))
					);
			
			List<DiarioCliente> results = session.createQuery(criteria).getResultList();
			System.out.println("Resultados: " + results.size());
			System.out.println(results);

		} catch (HibernateException hbe) {
			System.out.println("HibernateException: " + hbe.getMessage());
			session.getTransaction().rollback();
			session.close();

		} finally {

			session.close();
		}

	}

}
