package com.campfhir.dao;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.xml.sax.SAXException;

import com.campfhir.model.Condition;

import utils.HibernateBaseDB;

/**
*
* @author  James Champion
* @version 1.0
* @since   2019-02-08 
*/
public class ConditionDao implements ConditionDaoInterface<Condition, String> 
{
	private Session currentSession;
	private SessionFactory sessionFactory;
	
	private Transaction currentTransaction;

	public ConditionDao() {}

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

	public void persist(Condition entity) 
	{
		getCurrentSession().save(entity);
	}

	public void update(Condition entity) 
	{
		getCurrentSession().update(entity);
	}

	public Condition findById(String id) 
	{
		Condition condition = (Condition) getCurrentSession().get(Condition.class, id);
		return condition; 
	}
	
	@SuppressWarnings("unchecked")
	public List<Condition> findByPatientId(String id)
	{
		Query query = getCurrentSession().createQuery("FROM Condition WHERE con_subject_reference = '"+id+"'");
		
		List<Condition> condition = (List<Condition>) query.list();
		
		return condition;
	}

	public void delete(Condition entity) 
	{
		getCurrentSession().delete(entity);
	}

	@SuppressWarnings("unchecked")
	public List<Condition> findAll() 
	{
		Query query = getCurrentSession().createQuery("FROM Condition");
//		
//		query.setFirstResult(start);
//		query.setMaxResults(max);
		
		List<Condition> condition = (List<Condition>) query.list();
		
		return condition;
	}

	public void deleteAll() 
	{
//		List<Condition> entityList = findAll();
//		for (Condition entity : entityList) {
//			delete(entity);
//		}
	}
}
