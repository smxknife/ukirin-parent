package org.ukirin.job.core.schedule;

import org.ukirin.job.core.controller.Infrastructure;
import org.ukirin.job.core.controller.JobController;
import org.ukirin.job.core.instance.Instance;
import org.ukirin.job.core.instance.InstanceSelectedStrategy;
import org.ukirin.job.core.instance.MinimumLoadInstanceSelectedStrategy;
import org.ukirin.job.core.job.JobDefinition;
import org.ukirin.job.core.registry.JobRegistry;
import org.ukirin.job.core.registry.Watcher;
import org.ukirin.job.core.sharding.AverageJobShardingStrategy;
import org.ukirin.job.core.sharding.JobShardingStrategy;
import org.ukirin.job.core.sharding.ShardingContext;
import org.ukirin.job.core.trigger.JobTrigger;
import org.ukirin.job.core.util.Tuple2;

import java.util.Map;
import java.util.Objects;

/**
 * @author smxknife
 * 2020/11/12
 */
public abstract class AbstractJobScheduler implements JobScheduler {

	private volatile boolean isInitialed = false;
	protected JobRegistry jobRegistry;
	protected JobController jobController;
	protected JobShardingStrategy jobShardingStrategy;
	protected InstanceSelectedStrategy instanceSelectedStrategy;
	protected ExecutionContext context;
	private String instanceId;

	public AbstractJobScheduler(String instanceId, JobRegistry jobRegistry) {
		this(instanceId, jobRegistry, firstJobController());

	}

	public AbstractJobScheduler(String instanceId, JobRegistry jobRegistry, JobController jobController) {
		this(instanceId, jobRegistry, jobController, new MinimumLoadInstanceSelectedStrategy(), new AverageJobShardingStrategy());
	}

	public AbstractJobScheduler(String instanceId, JobRegistry jobRegistry, JobController jobController, InstanceSelectedStrategy instanceSelectedStrategy, JobShardingStrategy jobShardingStrategy) {
		this.instanceId = instanceId;
		this.jobRegistry = jobRegistry;
		this.jobController = jobController;
		this.jobShardingStrategy = jobShardingStrategy;
		this.instanceSelectedStrategy = instanceSelectedStrategy;
	}

	private static JobController firstJobController() {
		if (Infrastructure.INFRASTRUCTURES.size()  == 0) {
			throw new IllegalArgumentException("JobController is not exist");
		}
		return Infrastructure.INFRASTRUCTURES.get(0).getJobController();
	}

	@Override
	public final JobScheduler init() {
		boolean isLeader = this.jobRegistry.registerInstance(this);
		if (isLeader) {
			this.jobRegistry.addJobWatcher(this.jobWatcher());
		}
		this.jobRegistry.addJobTriggerWatcher(this, jobTriggerWatcher());
		this.jobController.init();
		this.isInitialed = true;
		return this;
	}

	@Override
	public final void start() {
		if (!isInitialed) {
			throw new UnsupportedOperationException("JobScheduler does not init");
		}
		this.jobController.start();
	}

	@Override
	public Watcher<Tuple2<ShardingContext, JobTrigger>> jobTriggerWatcher() {
		return (Tuple2<ShardingContext, JobTrigger> tuple2) -> {
			ShardingContext shardingContext = tuple2.getE1();
			shardingContext.setExecutionContext(context);
			jobController.trigger(shardingContext, tuple2.getE2());
		};
	};

	@Override
	public Watcher<JobDefinition> jobWatcher() {
		return (JobDefinition jobDefinition) -> {
			Map<Instance, ShardingContext> instanceShardingMap = jobShardingStrategy
					.sharding(instanceSelectedStrategy.select(jobRegistry.getInstances()), jobDefinition);
			jobRegistry.assignSharding(instanceShardingMap);
		};
	}

	@Override
	public String instanceId() {
		return this.instanceId;
	}

	@Override
	public final int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public final boolean equals(Object obj) {
		return Objects.nonNull(obj) && this == obj;
	}
}
