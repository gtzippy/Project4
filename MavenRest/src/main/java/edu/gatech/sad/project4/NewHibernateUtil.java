/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gatech.sad.project4;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.gatech.sad.project4.Resources.ResourceBase;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author Daniel
 */
public class NewHibernateUtil {

    private static final SessionFactory sessionFactory;
    static {
        ResourceBase.setObjectMapper(new ObjectMapper());
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml) 
            // config file.
            Configuration configuration = new Configuration().configure();
        	ServiceRegistry registry = new ServiceRegistryBuilder().applySettings(configuration.getProperties())
        			.buildServiceRegistry();
        	sessionFactory = configuration.buildSessionFactory(registry);
            InteractionLayer.Instance();
        } catch (Throwable ex) {
            // Log the exception. 
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
