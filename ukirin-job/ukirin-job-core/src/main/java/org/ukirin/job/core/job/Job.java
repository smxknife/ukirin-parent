package org.ukirin.job.core.job;

import org.ukirin.job.core.sharding.ShardingContext;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务
 * @author smxknife
 * 2020/11/12
 */
public interface Job {

	/**
	 * 执行
	 * @param shardingContext
	 * @param fireTime
	 * @param triggerTime
	 * @param parameters
	 */
	void execute(ShardingContext shardingContext, LocalDateTime fireTime, LocalDateTime triggerTime, Map<String, Object> parameters);
}
