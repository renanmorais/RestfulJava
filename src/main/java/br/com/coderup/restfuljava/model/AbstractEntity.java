package br.com.coderup.restfuljava.model;

import java.io.Serializable;

public abstract class AbstractEntity {

	public abstract Serializable getId();

	public abstract void setId(Serializable id);

}
