package com.campfhir.dao;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.xml.sax.SAXException;

import com.campfhir.model.Procedure;

import utils.HibernateBaseDB;

/**
*
* @author  James Champion
* @version 1.0
* @since   2019-02-08 
*/
public class ProcedureDao implements ProcedureDaoInterface<Procedure, String> 
{
	private Session currentSession;
	private SessionFactory sessionFactory;
	
	private Transaction currentTransaction;

	public ProcedureDao() {}

	public Session openCurrentSession() throws ParserConfigurationException, SAXException, IOException 
	{
		sessionFactory = HibernateBaseDB.getSessionFactory();
		currentSession = sessionFactory.openSession();
		return currentSession;
	}

	public Session openCurrentSessionwithTransaction() throws ParserConfigurationException, SAXException, IOException 
	{
		sessionFactory = HibernateBaseDB.getSessionFactory();
		currentSession = sessionFactory.openSession();
		currentTransaction = currentSession.beginTransaction();
		return currentSession;
	}
	
	public void closeCurrentSession() 
	{
		currentSession.close();
	}
	
	public void closeCurrentSessionwithTransaction() 
	{
		currentTransaction.commit();
		currentSession.close();
	}
	
	private static SessionFactory getSessionFactory() 
	{
		Configuration configuration = new Configuration().configure();
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties());
		SessionFactory sessionFactory = configuration.buildSessionFactory(builder.build());
		return sessionFactory;
	}

	public Session getCurrentSession() 
	{
		return currentSession;
	}

	public void setCurrentSession(Session currentSession) 
	{
		this.currentSession = currentSession;
	}

	public Transaction getCurrentTransaction() 
	{
		return currentTransaction;
	}

	public void setCurrentTransaction(Transaction currentTransaction) 
	{
		this.currentTransaction = currentTransaction;
	}

	public void persist(Procedure entity) 
	{
		getCurrentSession().save(entity);
	}

	public void update(Procedure entity) 
	{
		getCurrentSession().update(entity);
	}

	public Procedure findById(String id) 
	{
		Procedure procedure = (Procedure) getCurrentSession().get(Procedure.class, id);
		return procedure; 
	}

	public void delete(Procedure entity) 
	{
		getCurrentSession().delete(entity);
	}

	@SuppressWarnings("unchecked")
	public List<Procedure> findAll() 
	{
		List<Procedure> procedures = (List<Procedure>) getCurrentSession().createQuery("FROM Procedure").list();
		return procedures;
	}

	public void deleteAll() 
	{
		List<Procedure> entityList = findAll();
		for (Procedure entity : entityList) {
			delete(entity);
		}
	}
}
