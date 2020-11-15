package org.ukirin.job.core.executor;

import org.ukirin.job.core.job.Job;
import org.ukirin.job.core.sharding.ShardingContext;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 执行器
 * @author smxknife
 * 2020/11/12
 */
public interface JobExecutor {

	/**
	 * 执行
	 * @param context
	 * @param job
	 * @param fireTime
	 * @param triggerTime
	 * @param parameters
	 */
	void execute(ShardingContext context, Job job, LocalDateTime fireTime, LocalDateTime triggerTime, Map<String, Object> parameters);
}
