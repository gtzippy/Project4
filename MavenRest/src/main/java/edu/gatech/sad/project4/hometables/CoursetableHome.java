package edu.gatech.sad.project4.hometables;

// Generated Nov 25, 2015 3:17:11 AM by Hibernate Tools 3.4.0.CR1

import edu.gatech.sad.project4.entities.Coursetable;
import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class Coursetable.
 * @see edu.gatech.sad.project4.entities.Coursetable
 * @author Hibernate Tools
 */
public class CoursetableHome {

	private static final Log log = LogFactory.getLog(CoursetableHome.class);

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

	public void persist(Coursetable transientInstance) {
		log.debug("persisting Coursetable instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Coursetable instance) {
		log.debug("attaching dirty Coursetable instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Coursetable instance) {
		log.debug("attaching clean Coursetable instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Coursetable persistentInstance) {
		log.debug("deleting Coursetable instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Coursetable merge(Coursetable detachedInstance) {
		log.debug("merging Coursetable instance");
		try {
			Coursetable result = (Coursetable) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Coursetable findById(java.lang.String id) {
		log.debug("getting Coursetable instance with id: " + id);
		try {
			Coursetable instance = (Coursetable) sessionFactory
					.getCurrentSession().get(
							"edu.gatech.sad.project4.entities.Coursetable", id);
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

	public List findByExample(Coursetable instance) {
		log.debug("finding Coursetable instance by example");
		try {
			List results = sessionFactory
					.getCurrentSession()
					.createCriteria(
							"edu.gatech.sad.project4.entities.Coursetable")
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
