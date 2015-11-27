package edu.gatech.sad.project4.hometables;

// Generated Nov 25, 2015 3:17:11 AM by Hibernate Tools 3.4.0.CR1

import edu.gatech.sad.project4.entities.Offeredcoursestable;
import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class Offeredcoursestable.
 * @see edu.gatech.sad.project4.entities.Offeredcoursestable
 * @author Hibernate Tools
 */
public class OfferedcoursestableHome {

	private static final Log log = LogFactory
			.getLog(OfferedcoursestableHome.class);

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

	public void persist(Offeredcoursestable transientInstance) {
		log.debug("persisting Offeredcoursestable instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Offeredcoursestable instance) {
		log.debug("attaching dirty Offeredcoursestable instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Offeredcoursestable instance) {
		log.debug("attaching clean Offeredcoursestable instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Offeredcoursestable persistentInstance) {
		log.debug("deleting Offeredcoursestable instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Offeredcoursestable merge(Offeredcoursestable detachedInstance) {
		log.debug("merging Offeredcoursestable instance");
		try {
			Offeredcoursestable result = (Offeredcoursestable) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Offeredcoursestable findById(int id) {
		log.debug("getting Offeredcoursestable instance with id: " + id);
		try {
			Offeredcoursestable instance = (Offeredcoursestable) sessionFactory
					.getCurrentSession()
					.get("edu.gatech.sad.project4.entities.Offeredcoursestable",
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

	public List findByExample(Offeredcoursestable instance) {
		log.debug("finding Offeredcoursestable instance by example");
		try {
			List results = sessionFactory
					.getCurrentSession()
					.createCriteria(
							"edu.gatech.sad.project4.entities.Offeredcoursestable")
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
