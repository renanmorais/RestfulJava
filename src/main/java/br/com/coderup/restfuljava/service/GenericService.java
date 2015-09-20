package br.com.coderup.restfuljava.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.PropertyProjection;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;

import br.com.coderup.restfuljava.exception.FieldNotFoundException;
import br.com.coderup.restfuljava.model.AbstractEntity;
import br.com.coderup.restfuljava.model.Result;
import br.com.coderup.restfuljava.repository.GenericRepository;
import br.com.coderup.restfuljava.util.UtilProject;

public abstract class GenericService<T extends GenericRepository<E, K>, E extends AbstractEntity, K extends Serializable> {

	public abstract T getRepository();

	public Result findById(K key, String fieldValues) {
		HttpStatus httpStatus;
		Object content;
		try {
			content = getRepository().findById(key, getFields(fieldValues));
			httpStatus = content == null ? HttpStatus.NOT_FOUND : HttpStatus.OK;
		} catch (FieldNotFoundException fnfe) {
			httpStatus = HttpStatus.BAD_REQUEST;
			content = fnfe.getMessage();
			fnfe.printStackTrace();
		} catch (QueryException qe) {
			httpStatus = HttpStatus.BAD_REQUEST;
			content = qe.getMessage();
			qe.printStackTrace();
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			content = getRepository().getEntityClass();
			e.printStackTrace();
		}
		return createResult(httpStatus, content);
	}

	public Result findAll(String sortValues, String fieldValues) {

		HttpStatus httpStatus;
		Object content;
		long count = 0;

		try {
			content = getRepository().findAll(getSortParams(sortValues), getFields(fieldValues));
			count = getRepository().count().longValue();
			httpStatus = HttpStatus.OK;
		} catch (FieldNotFoundException fnfe) {
			httpStatus = HttpStatus.BAD_REQUEST;
			content = fnfe.getMessage();
			fnfe.printStackTrace();
		} catch (QueryException qe) {
			httpStatus = HttpStatus.BAD_REQUEST;
			content = qe.getMessage();
			qe.printStackTrace();
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			content = e.getMessage();
			e.printStackTrace();
		}

		Result result = createResult(httpStatus, content);
		result.addMeta("count", count);

		return result;
	}

	public Result updateAll(List<E> entities) {
		HttpStatus httpStatus;
		Object content;
		try {
			getRepository().updateAll(entities);
			httpStatus = HttpStatus.OK;
			content = HttpStatus.OK.getReasonPhrase();
		} catch (Exception e) {
			httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
			content = e.getMessage();
			e.printStackTrace();
		}
		return createResult(httpStatus, content);
	}

	public Result update(K id, E entity) {
		HttpStatus httpStatus;
		Object content;
		try {
			getRepository().update(id, entity);
			httpStatus = HttpStatus.OK;
			content = HttpStatus.OK.getReasonPhrase();
		} catch (Exception e) {
			httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
			content = e.getMessage();
			e.printStackTrace();
		}
		return createResult(httpStatus, content);
	}

	public Result save(E entity) {
		HttpStatus httpStatus;
		Object content;
		try {
			httpStatus = HttpStatus.CREATED;
			String resourcePath = UtilProject.getMainPath();
			resourcePath += getRepository().getEntityClass().getSimpleName() + "s";
			resourcePath += "/" + getRepository().save(entity);
			content = resourcePath.toLowerCase();
		} catch (Exception e) {
			httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
			content = processException(e);
			e.printStackTrace();
		}
		return createResult(httpStatus, content);
	}

	public Result save(K id) {
		return createResult(HttpStatus.METHOD_NOT_ALLOWED, null);
	}

	public Result deleteById(K key) {
		HttpStatus httpStatus;
		Object content;
		try {
			getRepository().delete(key);
			httpStatus = HttpStatus.NO_CONTENT;
			content = HttpStatus.NO_CONTENT.getReasonPhrase();
		} catch (Exception e) {
			httpStatus = HttpStatus.UNPROCESSABLE_ENTITY;
			content = e.getMessage();
			e.printStackTrace();
		}
		return createResult(httpStatus, content);
	}

	public Result deleteAll() {
		HttpStatus httpStatus;
		Object content;
		try {
			getRepository().deleteAll();
			httpStatus = HttpStatus.NO_CONTENT;
			content = HttpStatus.NO_CONTENT.getReasonPhrase();
		} catch (Exception e) {
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			content = e.getMessage();
			e.printStackTrace();
		}
		return createResult(httpStatus, content);
	}

	public Result createResult(HttpStatus httpStatus, Object content) {
		Result result = new Result();
		result.setContent(content);
		result.addMeta("resultcode", httpStatus.value());
		result.addMeta("resultmessage", httpStatus.getReasonPhrase());
		return result;
	}

	public Order[] getSortParams(String sortParams) throws FieldNotFoundException {
		if (sortParams == null) {
			Order[] orders = new Order[1];
			orders[0] = Order.asc("id");
			return orders;
		} else {
			String[] fields = sortParams.split(",");
			Order[] orders = new Order[fields.length];
			for (int i = 0, size = fields.length; i < size; i++) {
				if (fields[i].startsWith("-")) {
					orders[i] = Order.desc(fields[i].replaceFirst("-", ""));
					evaluateField(fields[i].replaceFirst("-", ""));
				} else {
					orders[i] = Order.asc(fields[i]);
					evaluateField(fields[i]);
				}
			}
			return orders;
		}
	}

	public PropertyProjection[] getFields(String fieldValues) throws FieldNotFoundException {
		if (fieldValues == null) {
			return null;
		} else {
			List<String> fields = Arrays.asList(fieldValues.split(","));
			boolean hasId = fields.contains("id");
			int sizePropertyProjection = fields.size();
			if (!hasId) {
				sizePropertyProjection++;
			}
			PropertyProjection[] propertyProjections = new PropertyProjection[sizePropertyProjection];
			for (int i = 0, size = fields.size(); i < size; i++) {
				evaluateField(fields.get(i));
				propertyProjections[i] = Projections.property(fields.get(i));
			}
			if (!hasId) {
				propertyProjections[propertyProjections.length - 1] = Projections.property("id");
			}
			return propertyProjections;
		}
	}

	public void evaluateField(String stringField) throws FieldNotFoundException {
		boolean throwException = true;
		Class<E> clazz = getRepository().getEntityClass();
		for (Field field : clazz.getDeclaredFields()) {
			if (field.getName().equals(stringField)) {
				throwException = false;
				break;
			}
		}
		if (throwException) {
			throw new FieldNotFoundException(
					"Field '" + stringField + "' don't exist in " + clazz.getSimpleName() + "!");
		}
	}
	
	public Object processException(Exception e) {
		Throwable throwable = e.getCause();
		while(throwable != null) {
			if(throwable instanceof PSQLException) {
				String field = throwable.getMessage();
				field = field.substring(field.indexOf("\"") + 1, field.lastIndexOf("\""));
				return "Invalid or inexistent value in '" + field + "'";
			} else {
				throwable = throwable.getCause();
			}
		}
		return e.getLocalizedMessage();
	}

}
