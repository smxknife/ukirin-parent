package org.ukirin.job.core.sharding;

import org.ukirin.job.core.instance.Instance;
import org.ukirin.job.core.job.JobDefinition;

import java.util.List;
import java.util.Map;

/**
 * @author smxknife
 * 2020/11/13
 */
public interface JobShardingStrategy {

	/**
	 * 生成分片上下文
	 * @param instances
	 * @param jobDefinition
	 * @return
	 */
	Map<Instance, ShardingContext> sharding(List<Instance> instances, JobDefinition jobDefinition);


}
