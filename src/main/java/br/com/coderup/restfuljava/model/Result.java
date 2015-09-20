package br.com.coderup.restfuljava.model;

import java.util.HashMap;
import java.util.Map;

public class Result {

	private Map<String, Object> meta;
	private Object content;

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public Map<String, Object> getMeta() {
		return meta;
	}

	public void setMeta(Map<String, Object> meta) {
		this.meta = meta;
	}

	public void addMeta(String key, Object value) {
		if (meta == null) {
			meta = new HashMap<>();
		}
		meta.put(key, value);
	}

}
