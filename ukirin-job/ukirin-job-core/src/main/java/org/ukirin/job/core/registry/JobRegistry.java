package org.ukirin.job.core.registry;

import org.ukirin.job.core.instance.Instance;
import org.ukirin.job.core.instance.InstanceStatus;
import org.ukirin.job.core.job.JobDefinition;
import org.ukirin.job.core.sharding.ShardingContext;
import org.ukirin.job.core.trigger.JobTrigger;
import org.ukirin.job.core.util.Tuple2;

import java.util.Collection;
import java.util.Map;

/**
 * 注册中心
 * @author smxknife
 * 2020/11/12
 */
public interface JobRegistry {
	/**
	 * 添加job监听
	 * @param watcher
	 */
	void addJobWatcher(Watcher<JobDefinition> watcher);

	/**
	 * 添加trigger监听
	 * @param instance
	 * @param watcher
	 */
	void addJobTriggerWatcher(Instance instance, Watcher<Tuple2<ShardingContext, JobTrigger>> watcher);

	/**
	 * 注册job
	 * @param jobDefinition
	 * @return
	 */
	boolean registerJob(JobDefinition jobDefinition);

	/**
	 * 注册实例
	 * @param instance
	 * @return
	 */
	boolean registerInstance(Instance instance);

	/**
	 * 获取实例列表
	 * @return
	 */
	Collection<InstanceStatus> getInstances();

	/**
	 * 指派分片到实例
	 * @param instanceShardingContextMap
	 */
	void assignSharding(Map<Instance, ShardingContext> instanceShardingContextMap);

	/**
	 * 下发trigger，触发job执行
	 * @param trigger
	 */
	void trigger(JobTrigger trigger);
}
