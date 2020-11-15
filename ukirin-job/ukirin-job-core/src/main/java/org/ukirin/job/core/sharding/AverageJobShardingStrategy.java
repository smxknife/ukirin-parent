package org.ukirin.job.core.sharding;

import lombok.extern.slf4j.Slf4j;
import org.ukirin.job.core.instance.Instance;
import org.ukirin.job.core.job.JobDefinition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author smxknife
 * 2020/11/13
 */
@Slf4j
public class AverageJobShardingStrategy implements JobShardingStrategy {

	@Override
	public Map<Instance, ShardingContext> sharding(List<Instance> instances, JobDefinition jobDefinition) {
		int shardingCount = jobDefinition.getShardingCount();
		int shardingTotal = Math.min(shardingCount, instances.size());
		return Stream.iterate(0, idx -> idx + 1)
				.limit(shardingTotal)
				.collect(Collectors.toMap(idx -> instances.get(idx), idx ->
						new ShardingContext(jobDefinition.getJobName(), jobDefinition.getClassName(),
								jobDefinition.getCron(), idx, shardingTotal)));
	}
}
