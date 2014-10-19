package hibernateAbstractDao.genericDAO;

import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;

/**
 * 
 * @author d.charpentier
 * 
 *         This DAO its a simple interface which declare basic CRUD interface and some helpers for request databases
 * 
 *         Its optimize if you use lazy mode and load all your data in request
 * 
 *         The Hibernate session was close after each request
 * 
 * @param <T>
 *            Type of entity
 * @param <R>
 *            Type of primary key
 */
public interface DAO<T, R> {

	/**
	 * 
	 * @return the attribute name of primary key
	 */
	public abstract String getIdName();

	/**
	 * Insert a new entity in database with a transaction
	 * 
	 * @param obj
	 *            the object to insert
	 */
	T insert(T obj);

	/**
	 * Update the value of entity with a transaction
	 * 
	 * @param obj
	 *            the object to update
	 */
	T update(T obj);

	/**
	 * Delete the object
	 * 
	 * @param obj
	 *            the object to delete
	 */
	void delete(T obj);
	
	/**
	 * 
	 * @param id
	 *            Value of id
	 * @param fetchRelations
	 *            Table of dependencies to load
	 * @return return a single value with the good id value
	 */
	T get(R id, String... fetchRelations);
	
	/**
	 * Count the elements in table with criterion filter and dependencies, restrictions and fetchRelation can be null and the result is all the table size
	 * 
	 * @param restrictions
	 *            list of hibernate criterion for filter the count
	 * @param fetchRelations
	 *            relation to load for the criterion
	 * @return long value represent the number of row with filter
	 */
	Long count(List<Criterion> restrictions, String... fetchRelations);

	/**
	 * Count the element in table with a simple request (its a where on one column)
	 * 
	 * @param attributeName
	 *            the attribute name for filter
	 * @param sqlOperation
	 *            the operation for filter
	 * @param values
	 *            the values in an object array, if the number of parameters are not good the method throw an runtime exception
	 * @param fetchRelations
	 *            relation to load for attributeName
	 * @return long value represent the number of row with simple request filter
	 */
	Long count(String attributeName, SqlOperation sqlOperation, Object[] values, String... fetchRelations);
	
	/**
	 * List of all element of table
	 * 
	 * @param fetchRelations
	 *            load the relations of table, relations of relation etc.. ex: getAll("movie.subCategory", movie.subCategory.Category");
	 * 
	 * @return All table in list of T elements
	 */
	List<T> getAll(String... fetchRelations);
	
	/**
	 * 
	 * @param restrictions
	 *            list of hibernate criteria
	 * @param fetchRelations
	 *            relations we need
	 * @return list of T elements with criteria
	 */
	List<T> requestWithCriterias(List<Criterion> restrictions, String... fetchRelations);
	
	/**
	 * 
	 * @param HQLRequest
	 *            HQL query
	 * @param parameters
	 *            parameters to make a prepared request
	 * @return a list of joker, you can select just one field or all table in object.
	 */
	List<?> requestWithHQL(String HQLRequest, Map<String, Object> parameters);
	
	/**
	 * request : From Table where 'attribueName' 'operation' 'value'
	 * 
	 * @param attributeName
	 *            attribute who need filter
	 * @param operation
	 *            Enum SqlOperation (EQUALS, GREATER_THAN, ...)
	 * @param values
	 *            the values of attribute
	 * @param fetchRelations
	 *            the relation to load
	 * @return List of T element ex request : From Movie where title ='iron man' => simpleRequest("title", SqlOperation.EQUALS, new Object[]{'iron man'}) From Movie where view is true =>
	 *         simpleRequest("view", SqlOperation.EQUALS, new Object[]{'true}) From Movie m left outer join m.category category where category.name = 'action' simpleRequest("category.name",
	 *         SqlOperation.EQUALS, new Object[]{'action)
	 * 
	 */
	List<T> simpleRequest(String attributeName, SqlOperation operation, Object[] values, String... fetchRelations);

	/**
	 * list of all element of table with pagination
	 * 
	 * @param pageNumber
	 *            number of page (the first page is 1)
	 * @param pageSize
	 *            number of element in page
	 * @param fetchRelations
	 *            the relation to load
	 * @return List of all element of table with pageSize element at the poisition pageNumber
	 */
	List<T> getAllPaginable(int pageNumber, int pageSize, String... fetchRelations);
	
	/**
	 * 
	 * @param restrictions
	 *            restrictions list of hibernate criteria
	 * @param pageNumber
	 *            page number (first is 1)
	 * @param pageSize
	 *            number of element per page
	 * @param fetchRelations
	 *            fetchRelations relations we need
	 * @return list of T elements with pagination
	 */
	List<T> requestWithCriteriasPaginable(List<Criterion> restrictions, int pageNumber, int pageSize, String... fetchRelations);
	
	/**
	 * 
	 * @param HQLRequest
	 *            HQL query with pagination
	 * @param parameters
	 *            parameters to make a prepared request
	 * @return a list of joker, you can select just one field or all table in object.
	 */
	List<?> requestWithHQLPaginable(String HQLRequest, Map<String, Object> parameters, int pageNumber, int pageSize);
	
	/**
	 * 
	 * @param attributeName
	 *            attribue who need filter
	 * @param operation
	 *            Enum SqlOperation (EQUALS, GREATER_THAN, ...)
	 * @param values
	 *            the values of attribute
	 * @param pageNumber
	 *            The number of page (the first is 1)
	 * @param pageSize
	 *            The number of element per page
	 * @param fetchRelations
	 *            the relation to load
	 * @return List of T element with pagination
	 */
	List<T> simpleRequestPaginable(String attributeName, SqlOperation operation, Object[] values, int pageNumber, int pageSize, String... fetchRelations);


}
