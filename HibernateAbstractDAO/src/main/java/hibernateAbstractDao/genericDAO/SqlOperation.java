package hibernateAbstractDao.genericDAO;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import util.EscapedExpression;

/**
 * 
 * @author d.charpentier
 * 
 *         Represente the simple sql Operation
 * 
 */
public enum SqlOperation {

	/**
	 * its >
	 */
	GREATER_THAN {
		@Override
		public Criterion getCriterion(String attributeName, Object... values) {
			if ((values == null) || (values.length != 1)) {
				throw new IllegalArgumentException();
			}
			return Restrictions.gt(attributeName, values[0]);
		}
	},
	/**
	 * its >=
	 * 
	 */
	GREATER_THAN_OR_EQUALS {
		@Override
		public Criterion getCriterion(String attributeName, Object... values) {
			if ((values == null) || (values.length != 1)) {
				throw new IllegalArgumentException();
			}
			return Restrictions.ge(attributeName, values[0]);
		}
	},

	/**
	 * its <
	 */
	LOWER_THAN {
		@Override
		public Criterion getCriterion(String attributeName, Object... values) {
			if ((values == null) || (values.length != 1)) {
				throw new IllegalArgumentException();
			}
			return Restrictions.lt(attributeName, values[0]);
		}
	},

	/**
	 * its =
	 */
	EQUALS {
		@Override
		public Criterion getCriterion(String attributeName, Object... values) {
			if ((values == null) || (values.length != 1)) {
				throw new IllegalArgumentException();
			}
			return Restrictions.eq(attributeName, values[0]);
		}
	},

	/**
	 * its !=
	 */
	DIFFERENT {
		@Override
		public Criterion getCriterion(String attributeName, Object... values) {
			if ((values == null) || (values.length != 1)) {
				throw new IllegalArgumentException();
			}
			return Restrictions.ne(attributeName, values[0]);
		}
	},

	/**
	 * its like %''%
	 */
	LIKE {
		@Override
		public Criterion getCriterion(String attributeName, Object... values) {
			if ((values == null) || (values.length != 1)) {
				throw new IllegalArgumentException();
			}
			if (values[0] instanceof String) {
				return Restrictions.like(attributeName, EscapedExpression.escape((String) values[0]));
			} else {
				return Restrictions.like(attributeName, values[0]);
			}
		}
	},

	/**
	 * its a between val1 and val2
	 */
	BETWEEN {
		@Override
		public Criterion getCriterion(String attributeName, Object... values) {
			if ((values == null) || (values.length != 2)) {
				throw new IllegalArgumentException();
			}
			return Restrictions.between(attributeName, values[0], values[1]);
		}
	},

	/**
	 * its In(value1, value2,....)
	 */
	IN {
		@Override
		public Criterion getCriterion(String attributeName, Object... values) {
			return Restrictions.in(attributeName, values);
		}
	};

	public abstract Criterion getCriterion(String attributeName, Object... values);

}
