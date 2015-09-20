package br.com.coderup.restfuljava.app.initializer;

import javax.servlet.Filter;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import br.com.coderup.restfuljava.app.configuration.HibernateConfiguration;
import br.com.coderup.restfuljava.app.configuration.RootConfiguration;
import br.com.coderup.restfuljava.app.filter.GZIPFilter;
import br.com.coderup.restfuljava.util.UtilProject;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { RootConfiguration.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { HibernateConfiguration.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { UtilProject.getMainPath() + "*" };
	}

	@Override
	protected Filter[] getServletFilters() {
		return new Filter[] { new GZIPFilter() };
	}

}