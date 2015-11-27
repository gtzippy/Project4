package edu.gatech.sad.project4.hometables;

// Generated Nov 25, 2015 3:17:11 AM by Hibernate Tools 3.4.0.CR1

import edu.gatech.sad.project4.entities.Studentpreferencestable;
import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class Studentpreferencestable.
 * @see edu.gatech.sad.project4.entities.Studentpreferencestable
 * @author Hibernate Tools
 */
public class StudentpreferencestableHome {

	private static final Log log = LogFactory
			.getLog(StudentpreferencestableHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(Studentpreferencestable transientInstance) {
		log.debug("persisting Studentpreferencestable instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Studentpreferencestable instance) {
		log.debug("attaching dirty Studentpreferencestable instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Studentpreferencestable instance) {
		log.debug("attaching clean Studentpreferencestable instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Studentpreferencestable persistentInstance) {
		log.debug("deleting Studentpreferencestable instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Studentpreferencestable merge(
			Studentpreferencestable detachedInstance) {
		log.debug("merging Studentpreferencestable instance");
		try {
			Studentpreferencestable result = (Studentpreferencestable) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Studentpreferencestable findById(java.lang.Integer id) {
		log.debug("getting Studentpreferencestable instance with id: " + id);
		try {
			Studentpreferencestable instance = (Studentpreferencestable) sessionFactory
					.getCurrentSession()
					.get("edu.gatech.sad.project4.entities.Studentpreferencestable",
							id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(Studentpreferencestable instance) {
		log.debug("finding Studentpreferencestable instance by example");
		try {
			List results = sessionFactory
					.getCurrentSession()
					.createCriteria(
							"edu.gatech.sad.project4.entities.Studentpreferencestable")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
