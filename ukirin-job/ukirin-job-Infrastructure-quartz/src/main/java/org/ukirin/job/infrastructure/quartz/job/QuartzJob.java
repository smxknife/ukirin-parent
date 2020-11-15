package org.ukirin.job.infrastructure.quartz.job;

import lombok.Setter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.ukirin.job.core.executor.JobExecutor;
import org.ukirin.job.core.sharding.ShardingContext;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * @author smxknife
 * 2020/11/13
 */
@Setter
public final class QuartzJob implements Job {

	private JobExecutor jobExecutor;
	private org.ukirin.job.core.job.Job job;
	private ShardingContext shardingContext;
	private LocalDateTime triggerTime;
	private Map<String, Object> parameters;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LocalDateTime fireTime = LocalDateTime.ofInstant(context.getFireTime().toInstant(), ZoneId.systemDefault());
		jobExecutor.execute(shardingContext, job, fireTime, triggerTime, parameters);
	}
}
