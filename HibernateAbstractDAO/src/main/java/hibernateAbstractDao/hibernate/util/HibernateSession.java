package hibernateAbstractDao.hibernate.util;

import org.hibernate.Session;

/**
 * 
 * @author d.charpentier
 * 
 *         Simple interface for hibernate session and transaction
 */
public interface HibernateSession {

	/**
	 * Open an hibernate Session
	 * 
	 * @return the session
	 */
	Session openSession();

	/**
	 * Close the session parameter
	 * 
	 * @param session
	 *            the hibernate session
	 */
	void closeSession(Session session);

	/**
	 * Open a session with transaction
	 * 
	 * @return the session with transaction
	 */
	Session openSessionWithTransaction();

	/**
	 * close the transaction and the session in parameter
	 * 
	 * @param session
	 *            the hibernate session to close
	 * 
	 */
	void closeSessionWithTransaction(Session session);
}
