package org.ukirin.job.core.instance;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author smxknife
 * 2020/11/13
 */
public class MinimumLoadInstanceSelectedStrategy implements InstanceSelectedStrategy {
	@Override
	public List<Instance> select(Collection<InstanceStatus> instanceStatuses) {
		return instanceStatuses.stream()
				.sorted((pre, next) -> Integer.compare(pre.getTriggersCount(), next.getTriggersCount()))
				.map(InstanceStatus::getInstance)
				.collect(Collectors.toList());
	}
}
