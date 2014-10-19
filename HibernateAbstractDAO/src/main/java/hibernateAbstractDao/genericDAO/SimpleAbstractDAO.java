package hibernateAbstractDao.genericDAO;

import org.hibernate.criterion.Order;
import org.hibernate.sql.JoinType;

import hibernateAbstractDao.hibernate.util.HibernateSession;

/**
 * 
 * @author d.charpentier
 * 
 *         Simple implementation of abstractDAO, Type of primary key is Long Attribute name is id
 * 
 * @param <T>
 */
public abstract class SimpleAbstractDAO<T> extends AbstractDAO<T, Long> {

	public SimpleAbstractDAO(HibernateSession session) {
		super(session);
	}

	public SimpleAbstractDAO(HibernateSession session, JoinType defaulJointure,
			Order defaultOrder) {
		super(session, defaulJointure, defaultOrder);
	}

	public SimpleAbstractDAO(HibernateSession session, JoinType defaulJointure) {
		super(session, defaulJointure);
	}

	public SimpleAbstractDAO(HibernateSession session, Order defaultOrder) {
		super(session, defaultOrder);
	}

	@Override
	public String getIdName() {
		return "id";
	}
}
