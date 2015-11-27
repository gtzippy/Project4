package edu.gatech.sad.project4.hometables;

// Generated Nov 25, 2015 3:17:11 AM by Hibernate Tools 3.4.0.CR1

import edu.gatech.sad.project4.entities.Studentcourseassignmenttable;
import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class Studentcourseassignmenttable.
 * @see edu.gatech.sad.project4.entities.Studentcourseassignmenttable
 * @author Hibernate Tools
 */
public class StudentcourseassignmenttableHome {

	private static final Log log = LogFactory
			.getLog(StudentcourseassignmenttableHome.class);

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

	public void persist(Studentcourseassignmenttable transientInstance) {
		log.debug("persisting Studentcourseassignmenttable instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(Studentcourseassignmenttable instance) {
		log.debug("attaching dirty Studentcourseassignmenttable instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(Studentcourseassignmenttable instance) {
		log.debug("attaching clean Studentcourseassignmenttable instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(Studentcourseassignmenttable persistentInstance) {
		log.debug("deleting Studentcourseassignmenttable instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public Studentcourseassignmenttable merge(
			Studentcourseassignmenttable detachedInstance) {
		log.debug("merging Studentcourseassignmenttable instance");
		try {
			Studentcourseassignmenttable result = (Studentcourseassignmenttable) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public Studentcourseassignmenttable findById(
			edu.gatech.sad.project4.entities.StudentcourseassignmenttableId id) {
		log.debug("getting Studentcourseassignmenttable instance with id: "
				+ id);
		try {
			Studentcourseassignmenttable instance = (Studentcourseassignmenttable) sessionFactory
					.getCurrentSession()
					.get("edu.gatech.sad.project4.entities.Studentcourseassignmenttable",
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

	public List findByExample(Studentcourseassignmenttable instance) {
		log.debug("finding Studentcourseassignmenttable instance by example");
		try {
			List results = sessionFactory
					.getCurrentSession()
					.createCriteria(
							"edu.gatech.sad.project4.entities.Studentcourseassignmenttable")
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
