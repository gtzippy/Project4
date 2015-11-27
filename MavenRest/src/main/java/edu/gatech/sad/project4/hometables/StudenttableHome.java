package edu.gatech.sad.project4.hometables;

// Generated Nov 25, 2015 3:17:11 AM by Hibernate Tools 3.4.0.CR1

import edu.gatech.sad.project4.entities.Studenttable;
import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class Studenttable.
 * @see edu.gatech.sad.project4.entities.Studenttable
 * @author Hibernate Tools
 */
public class StudenttableHome {

	private static final Log log = LogFactory.getLog(StudenttableHome.class);

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

	public void persist(Studenttable transientInstance) {
		log.debug("persisting Studenttable instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Studenttable instance) {
		log.debug("attaching dirty Studenttable instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Studenttable instance) {
		log.debug("attaching clean Studenttable instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Studenttable persistentInstance) {
		log.debug("deleting Studenttable instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Studenttable merge(Studenttable detachedInstance) {
		log.debug("merging Studenttable instance");
		try {
			Studenttable result = (Studenttable) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Studenttable findById(java.lang.Integer id) {
		log.debug("getting Studenttable instance with id: " + id);
		try {
			Studenttable instance = (Studenttable) sessionFactory
					.getCurrentSession()
					.get("edu.gatech.sad.project4.entities.Studenttable", id);
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

	public List findByExample(Studenttable instance) {
		log.debug("finding Studenttable instance by example");
		try {
			List results = sessionFactory
					.getCurrentSession()
					.createCriteria(
							"edu.gatech.sad.project4.entities.Studenttable")
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
