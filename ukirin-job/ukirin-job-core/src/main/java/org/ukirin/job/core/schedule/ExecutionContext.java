package org.ukirin.job.core.schedule;

import java.util.Map;

/**
 * @author smxknife
 * 2020/11/12
 */

public class ExecutionContext {
	private Map<String, Object> parameters;

	public Object get(String key) {
		return parameters.get(key);
	}
}
