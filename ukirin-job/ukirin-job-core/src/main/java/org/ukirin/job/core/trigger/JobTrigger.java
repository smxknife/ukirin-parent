package org.ukirin.job.core.trigger;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 触发器
 * @author smxknife
 * 2020/11/12
 */
@Getter
public class JobTrigger {

	private String jobName;
	private LocalDateTime triggerTime;
	private Map<String, Object> parameters = new HashMap<>();

	public JobTrigger(String jobName) {
		this.jobName = jobName;
	}
	public JobTrigger(String jobName, LocalDateTime triggerTime) {
		this.jobName = jobName;
		this.triggerTime = triggerTime;
	}

	public Object addParameter(String key, Object value) {
		return this.parameters.put(key, value);
	}

	public Object removeParameter(String key) {
		return this.parameters.remove(key);
	}

	public void clearParameters() {
		this.parameters.clear();
	}
}
