package hibernateAbstractDao.genericDAO;

import org.hibernate.criterion.Order;

/**
 * 
 * @author d.charpentier
 * 
 *         Represente the Order operation
 * 
 */
public enum OrderOperation {
	ASC {
		@Override
		public Order getOrder(String attributeName) {
			return Order.asc(attributeName);
		}
	},
	DESC {
		@Override
		public Order getOrder(String attributeName) {
			return Order.desc(attributeName);
		}
	};

	/**
	 * 
	 * @param attributeName
	 * @return
	 * 
	 */
	public abstract Order getOrder(String attributeName);
}
