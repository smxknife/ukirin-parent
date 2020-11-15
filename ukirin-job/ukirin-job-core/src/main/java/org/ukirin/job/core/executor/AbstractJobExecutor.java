package org.ukirin.job.core.executor;

import org.ukirin.job.core.job.Job;
import org.ukirin.job.core.sharding.ShardingContext;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author smxknife
 * 2020/11/13
 */
public abstract class AbstractJobExecutor implements JobExecutor {
	@Override
	public final void execute(ShardingContext context, Job job, LocalDateTime fireTime, LocalDateTime triggerTime, Map<String, Object> parameters) {
		job.execute(context, fireTime, triggerTime, parameters);
	}
}
