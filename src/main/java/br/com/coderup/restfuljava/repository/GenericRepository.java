package br.com.coderup.restfuljava.repository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.PropertyProjection;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.coderup.restfuljava.model.AbstractEntity;

@Repository
@Transactional
public abstract class GenericRepository<E extends AbstractEntity, K extends Serializable> {

	@Autowired
	private SessionFactory sessionFactory;

	private Class<E> entityClass;
	private String tableName;

	@SuppressWarnings("unchecked")
	public GenericRepository() {
		this.entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
		this.tableName = entityClass.getSimpleName().toLowerCase();
	}

	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	public E findById(K key, PropertyProjection[] propertyProjections) {
		Criteria criteria = getSession().createCriteria(entityClass);
		criteria.add(Restrictions.eq("id", key));
		buildPropertyProjection(criteria, propertyProjections);
		return (E) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<E> findAll(Order[] orderValues, PropertyProjection[] propertyProjections) {
		Criteria criteria = getSession().createCriteria(entityClass);
		buildPropertyProjection(criteria, propertyProjections);
		if (orderValues != null) {
			for (Order order : orderValues) {
				criteria.addOrder(order);
			}
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public void updateAll(List<E> entities) {
		for (E entity : entities) {
			update((K) entity.getId(), entity);
		}
	}

	public void update(K id, E entity) {
		Session session = getSession();
		entity.setId(id);
		session.update(entity);
	}

	public Serializable save(E entity) {
		Session session = getSession();
		return session.save(entity);
	}

	public void delete(K key) {
		Session session = getSession();
		session.delete(findById(key, null));
	}

	public void deleteAll() {
		Session session = getSession();
		session.createQuery("delete from " + tableName).executeUpdate();
	}

	public BigInteger count() {
		Session session = getSession();
		return (BigInteger) session.createSQLQuery("select count(*) from " + tableName).uniqueResult();
	}

	public void buildPropertyProjection(Criteria criteria, PropertyProjection[] propertyProjections) {
		if (propertyProjections != null) {
			ProjectionList projectionList = Projections.projectionList();
			for (PropertyProjection propertyProjection : propertyProjections) {
				projectionList.add(propertyProjection, propertyProjection.getPropertyName());
			}
			criteria.setProjection(projectionList);
			criteria.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		}
	}

	public Class<E> getEntityClass() {
		return entityClass;
	}

}
