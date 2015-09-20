package br.com.coderup.restfuljava.controller;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import br.com.coderup.restfuljava.model.AbstractEntity;
import br.com.coderup.restfuljava.model.Result;
import br.com.coderup.restfuljava.repository.GenericRepository;
import br.com.coderup.restfuljava.service.GenericService;

public abstract class GenericController<S extends GenericService<R, E, K>, R extends GenericRepository<E, K>, E extends AbstractEntity, K extends Serializable> {

	public abstract S getService();

	@RequestMapping(method = { RequestMethod.GET })
	public Result findAll(String sort, String fields, HttpServletRequest request) {
		String sortValues = request.getParameter("sort");
		String fieldValues = request.getParameter("fields");
		return getService().findAll(sortValues, fieldValues);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public Result findById(@PathVariable("id") K id, String fields, HttpServletRequest request) {
		String fieldValues = request.getParameter("fields");
		return getService().findById(id, fieldValues);
	}

	@RequestMapping(method = { RequestMethod.PUT })
	public Result updateAll(@RequestBody List<E> entities) {
		return getService().updateAll(entities);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public Result update(@PathVariable("id") K id, @RequestBody E entity) {
		return getService().update(id, entity);
	}

	@RequestMapping(method = RequestMethod.POST)
	public Result save(@RequestBody E entity) {
		return getService().save(entity);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/{id}")
	public Result save(@PathVariable("id") K id) {
		return getService().save(id);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public Result deleteById(@PathVariable("id") K id) {
		return getService().deleteById(id);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public Result deleteAll() {
		return getService().deleteAll();
	}

}
