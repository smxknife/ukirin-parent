package org.ukirin.job.core.sharding;

import lombok.Getter;
import lombok.Setter;
import org.ukirin.job.core.schedule.ExecutionContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author smxknife
 * 2020/11/13
 */
@Getter
public class ShardingContext {

	private String jobName;
	private String jobClass;
	private String cron;
	private int shardingIndex;
	private int shardingTotal;
	@Setter
	private ExecutionContext executionContext;

	private Map<String, Object> parameters = new HashMap<>();

	public ShardingContext(String jobName, String jobClass, String cron, int shardingIndex, int shardingTotal) {
		this.jobName = jobName;
		this.jobClass = jobClass;
		this.cron = cron;
		this.shardingIndex = shardingIndex;
		this.shardingTotal = shardingTotal;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ShardingContext context = (ShardingContext) o;
		return shardingIndex == context.shardingIndex &&
				shardingTotal == context.shardingTotal &&
				Objects.equals(jobName, context.jobName) &&
				Objects.equals(jobClass, context.jobClass) &&
				Objects.equals(cron, context.cron) &&
				Objects.equals(executionContext, context.executionContext);
	}

	@Override
	public int hashCode() {
		return Objects.hash(jobName, jobClass, cron, shardingIndex, shardingTotal, executionContext);
	}
}
