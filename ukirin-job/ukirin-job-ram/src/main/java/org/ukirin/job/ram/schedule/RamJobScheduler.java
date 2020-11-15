package org.ukirin.job.ram.schedule;

import org.ukirin.job.core.controller.JobController;
import org.ukirin.job.core.instance.InstanceSelectedStrategy;
import org.ukirin.job.core.schedule.AbstractJobScheduler;
import org.ukirin.job.core.sharding.JobShardingStrategy;
import org.ukirin.job.ram.registry.RamJobRegistry;

/**
 * @author smxknife
 * 2020/11/13
 */
public class RamJobScheduler extends AbstractJobScheduler {

	public RamJobScheduler(String instanceId, RamJobRegistry jobRegistry) {
		super(instanceId, jobRegistry);
	}

	public RamJobScheduler(String instanceId, RamJobRegistry jobRegistry, JobController jobController) {
		super(instanceId, jobRegistry, jobController);
	}

	public RamJobScheduler(String instanceId, RamJobRegistry jobRegistry, JobController jobController, InstanceSelectedStrategy instanceSelectedStrategy, JobShardingStrategy jobShardingStrategy) {
		super(instanceId, jobRegistry, jobController, instanceSelectedStrategy, jobShardingStrategy);
	}
}
