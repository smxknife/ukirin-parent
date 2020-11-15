package org.ukirin.job.core.schedule;

import org.ukirin.job.core.instance.Instance;
import org.ukirin.job.core.job.JobDefinition;
import org.ukirin.job.core.registry.Watcher;
import org.ukirin.job.core.sharding.ShardingContext;
import org.ukirin.job.core.trigger.JobTrigger;
import org.ukirin.job.core.util.Tuple2;

/**
 * 调度器
 * @author smxknife
 * 2020/11/12
 */
public interface JobScheduler extends Instance {

	/**
	 * 初始化
	 * @return
	 */
	JobScheduler init();

	/**
	 * 启动
	 */
	void start();

	/**
	 * trigger观察器
	 * @return
	 */
	Watcher<Tuple2<ShardingContext, JobTrigger>> jobTriggerWatcher();

	/**
	 * JobDefinition观察器
	 * @return
	 */
	Watcher<JobDefinition> jobWatcher();

}
