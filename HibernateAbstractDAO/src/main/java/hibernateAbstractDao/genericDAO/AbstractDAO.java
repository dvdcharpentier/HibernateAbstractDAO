package hibernateAbstractDao.genericDAO;

import hibernateAbstractDao.hibernate.util.HibernateSession;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

/**
 * 
 * @author d.charpentier
 * 
 *         the implementation of DAO interface with hibernateSessionIm
 */
public abstract class AbstractDAO<T, R> implements DAO<T, R>{

	private Order[]	orders;

	private JoinType joinType;

	private JoinType defaultJointure = JoinType.LEFT_OUTER_JOIN;

	private Order defaultOrder = OrderOperation.ASC.getOrder(getIdName());

	private Class<T>	clasz;

	private final HibernateSession	hibernateSession;

	public AbstractDAO(HibernateSession session) {
		this.hibernateSession = session;
		initClasz();
	}

	public AbstractDAO(HibernateSession session, JoinType defaulJointure) {
		this(session);
		this.defaultJointure = defaulJointure;
	}

	public AbstractDAO(HibernateSession session, Order defaultOrder) {
		this(session);
		this.defaultOrder = defaultOrder;
	}

	public AbstractDAO(HibernateSession session,JoinType defaulJointure, Order defaultOrder) {
		this(session);
		this.defaultJointure = defaulJointure;
		this.defaultOrder = defaultOrder;
	}

	@Override
	public abstract String getIdName();

	@SuppressWarnings("unchecked")
	private void initClasz() {
		ParameterizedType superclass = (ParameterizedType)
				this
				.getClass().getGenericSuperclass();
		clasz = (Class<T>) superclass.getActualTypeArguments()[0];
	}

	@Override
	public T insert(T obj) {
		Session session = null;
		try {
			session = hibernateSession.openSessionWithTransaction();
			session.persist(obj);
			return obj;
		} catch (RuntimeException ex) {
			session.getTransaction().rollback();
			throw ex;
		} finally {
			if (session != null) {
				hibernateSession.closeSessionWithTransaction(session);
			}
		}
	}

	@Override
	public T update(T obj) {
		Session session = null;
		try {
			session = hibernateSession.openSessionWithTransaction();
			return (T) session.merge(obj);
		} catch (RuntimeException ex) {
			session.getTransaction().rollback();
			throw ex;
		} finally {
			if (session != null) {
				hibernateSession.closeSessionWithTransaction(session);
			}
		}
	}

	@Override
	public void delete(T obj) {
		Session session = null;
		try {
			session = hibernateSession.openSessionWithTransaction();
			session.delete(obj);
		} catch (RuntimeException ex) {
			session.getTransaction().rollback();
			throw ex;
		} finally {
			if (session != null) {
				hibernateSession.closeSessionWithTransaction(session);
			}
		}
	}

	@Override
	public Long count(List<Criterion> restrictions, String... fetchRelations) {
		Session session = null;
		Long result = null;
		try {
			session = hibernateSession.openSession();
			Criteria criteria = session.createCriteria(clasz).setProjection(Projections.rowCount());
			if (restrictions != null) {
				for (Criterion c : restrictions) {
					criteria.add(c);
				}
			}
			addFetchMode(criteria, fetchRelations);
			result = (Long) criteria.uniqueResult();
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			if (session != null) {
				hibernateSession.closeSession(session);
			}
			this.initDefaultJoinTypeAndOrder();
		}
		return result;
	}

	@Override
	public Long count(String attributeName, SqlOperation sqlOperation, Object[] values, String... fetchRelations) {
		Session session = null;
		Long result = null;
		try {
			session = hibernateSession.openSession();
			Criteria criteria = session.createCriteria(clasz).setProjection(Projections.rowCount());
			criteria.add(sqlOperation.getCriterion(attributeName, values));
			addFetchMode(criteria, fetchRelations);
			result = (Long) criteria.uniqueResult();
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			if (session != null) {
				hibernateSession.closeSession(session);
			}
			this.initDefaultJoinTypeAndOrder();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAll(String... fetchRelations) {
		List<T> list = null;
		Session session = null;
		try {
			session = hibernateSession.openSession();
			Criteria criteria = session.createCriteria(clasz);
			//criteria.addOrder(order);
			this.initOrder(criteria);
			addFetchMode(criteria, fetchRelations);
			list = criteria.list();
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			if (session != null) {
				hibernateSession.closeSession(session);
			}
			this.initDefaultJoinTypeAndOrder();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAllPaginable(int pageNumber, int pageSize, String... fetchRelations) {
		List<R> list = null;
		List<T> result = new ArrayList<T>();
		Session session = null;
		try {
			session = hibernateSession.openSession();
			Criteria criteria = session.createCriteria(clasz);
			criteria.setFirstResult((pageNumber - 1) * pageSize).setMaxResults(pageSize);

			if ((fetchRelations != null) && (fetchRelations.length != 0)) {
				//criteria.setProjection(Projections.id()).addOrder(order);
				criteria.setProjection(Projections.id());
				this.initOrder(criteria);
				list = criteria.list();
				if (!list.isEmpty()) {
					Criteria criteriaRequest = addFetchModeForPagination(session, list, fetchRelations);
					//criteriaRequest.addOrder(order);
					this.initOrder(criteriaRequest);
					result = criteriaRequest.list();
				}
			} else {
				//criteria.addOrder(order);
				this.initOrder(criteria);
				result = criteria.list();
			}
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			if (session != null) {
				hibernateSession.closeSession(session);
			}
			this.initDefaultJoinTypeAndOrder();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(R id, String... fetchRelations) {
		T object = null;
		Session session = null;
		try {
			session = hibernateSession.openSession();
			Criteria criteria = session.createCriteria(clasz);
			criteria.add(Restrictions.eq(getIdName(), id));
			addFetchMode(criteria, fetchRelations);
			object = (T) criteria.uniqueResult();
		} finally {
			if (session != null) {
				hibernateSession.closeSession(session);
			}
			this.initDefaultJoinTypeAndOrder();
		}
		return object;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> requestWithCriterias(List<Criterion> restrictions, String... fetchRelations) {
		List<T> list = null;
		Session session = null;
		try {
			session = hibernateSession.openSession();
			Criteria criteria = session.createCriteria(clasz);
			//criteria.addOrder(order);
			this.initOrder(criteria);
			for (Criterion rst : restrictions) {
				criteria.add(rst);
			}
			addFetchMode(criteria, fetchRelations);
			list = criteria.list();
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			if (session != null) {
				hibernateSession.closeSession(session);
			}
			this.initDefaultJoinTypeAndOrder();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> requestWithCriteriasPaginable(List<Criterion> restrictions, int pageNumber, int pageSize, String... fetchRelations) {
		List<R> list = null;
		List<T> result = new ArrayList<T>();
		Session session = null;
		try {
			session = hibernateSession.openSession();
			Criteria criteria = session.createCriteria(clasz);
			for (Criterion rst : restrictions) {
				criteria.add(rst);
			}
			criteria.setFirstResult((pageNumber - 1) * pageSize).setMaxResults(pageSize);

			if ((fetchRelations != null) && (fetchRelations.length != 0)) {
				// criteria.setProjection(Projections.id()).addOrder(Order.asc(getIdName()));
				//criteria.setProjection(Projections.id()).addOrder(order);
				criteria.setProjection(Projections.id());
				this.initOrder(criteria);
				list = criteria.list();
				if (!list.isEmpty()) {
					Criteria criteriaRequest = addFetchModeForPagination(session, list, fetchRelations);
					//criteriaRequest.addOrder(order);
					this.initOrder(criteriaRequest);
					result = criteriaRequest.list();
				}
			} else {
				//criteria.addOrder(order);
				this.initOrder(criteria);
				result = criteria.list();
			}
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			if (session != null) {
				hibernateSession.closeSession(session);
			}
			this.initDefaultJoinTypeAndOrder();
		}
		return result;
	}

	@Override
	public List<?> requestWithHQL(String HQLRequest, Map<String, Object> parameters) {
		List<?> list = null;
		Session session = null;
		try {
			session = hibernateSession.openSession();
			Query query = session.createQuery(HQLRequest);
			if (parameters != null) {
				for (String key : parameters.keySet()) {
					query.setParameter(key, parameters.get(key));
				}
			}
			list = query.list();
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			if (session != null) {
				hibernateSession.closeSession(session);
			}
			this.initDefaultJoinTypeAndOrder();
		}
		return list;
	}

	@Override
	public List<?> requestWithHQLPaginable(String HQLRequest, Map<String, Object> parameters, int pageNumber, int pageSize) {
		List<?> list = null;
		Session session = null;
		try {
			session = hibernateSession.openSession();
			Query query = session.createQuery(HQLRequest);
			query.setFirstResult((pageNumber - 1) * pageSize).setMaxResults(pageSize);
			if (parameters != null) {
				for (String key : parameters.keySet()) {
					query.setParameter(key, parameters.get(key));
				}
			}
			list = query.list();
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			if (session != null) {
				hibernateSession.closeSession(session);
			}
			this.initDefaultJoinTypeAndOrder();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> simpleRequest(String attributeName, SqlOperation operation, Object[] values, String... fetchRelations) {
		List<T> list = null;
		Session session = null;
		try {
			session = hibernateSession.openSession();
			Criteria criteria = session.createCriteria(clasz);
			//criteria.add(operation.getCriterion(attributeName, values)).addOrder(order);
			criteria.add(operation.getCriterion(attributeName, values));
			this.initOrder(criteria);
			addFetchMode(criteria, fetchRelations);
			list = criteria.list();
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			if (session != null) {
				hibernateSession.closeSession(session);
			}
			this.initDefaultJoinTypeAndOrder();
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> simpleRequestPaginable(String attributeName, SqlOperation operation, Object[] values, int pageNumber, int pageSize, String... fetchRelations) {
		List<R> list = null;
		List<T> result = new ArrayList<T>();
		Session session = null;
		try {
			session = hibernateSession.openSession();
			Criteria criteria = session.createCriteria(clasz);
			criteria.add(operation.getCriterion(attributeName, values)).setFirstResult((pageNumber - 1) * pageSize).setMaxResults(pageSize);
			if ((fetchRelations != null) && (fetchRelations.length != 0)) {
				// criteria.addOrder(order);
				// criteria.setProjection(Projections.id()).addOrder(Order.asc(getIdName()));
				//criteria.setProjection(Projections.id()).addOrder(order);
				criteria.setProjection(Projections.id());
				this.initOrder(criteria);
				list = criteria.list();
				if (!list.isEmpty()) {
					Criteria criteriaRequest = addFetchModeForPagination(session, list, fetchRelations);
					//criteriaRequest.addOrder(order);
					this.initOrder(criteriaRequest);
					result = criteriaRequest.list();
				}
			} else {
				//criteria.addOrder(order);
				this.initOrder(criteria);
				result = criteria.list();
			}
		} catch (RuntimeException ex) {
			throw ex;
		} finally {
			if (session != null) {
				hibernateSession.closeSession(session);
			}
			this.initDefaultJoinTypeAndOrder();
		}
		return result;
	}

	private void addFetchMode(Criteria criteria, String... fetchRelations) {
		if ((fetchRelations != null) && (fetchRelations.length > 0)) {
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			for (String relation : fetchRelations) {
				String[] alias = relation.split("\\.");
				String a = relation;
				if (alias.length > 0) {
					a = alias[alias.length - 1];
				}
				criteria.createAlias(relation, a, this.getJoinType());
			}
		}
	}

	private Criteria addFetchModeForPagination(Session session, List<R> ids, String... fetchRelations) {
		Criteria criteria = session.createCriteria(clasz);
		criteria.add(Restrictions.in(getIdName(), ids));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		if (fetchRelations != null) {
			for (String relation : fetchRelations) {
				String[] alias = relation.split("\\.");
				String a = relation;
				if (alias.length > 0) {
					a = alias[alias.length - 1];
				}
				criteria.createAlias(relation, a, this.getJoinType());
			}
		}
		return criteria;
	}

	private JoinType getJoinType(){
		if(joinType != null){
			return joinType;
		}
		return defaultJointure;
	}
	
	public DAO<T,R> setJoinType(JoinType joinType){
		this.joinType = joinType;
		return this;
	}
	
	private void initDefaultJoinTypeAndOrder(){
		joinType = null;
		orders = null;
	}
	
	public DAO<T,R> setOrders(Order...orders){
		this.orders = orders;
		return this;
	}
	
	private void initOrder(Criteria criteria){
		Order[] orders = getOrders();
		for(Order o : orders){
			criteria.addOrder(o);
		}
	}
	
	private Order[] getOrders(){
		if(orders != null){
			return orders;
		}
		return new Order[] { defaultOrder };
	}
}
