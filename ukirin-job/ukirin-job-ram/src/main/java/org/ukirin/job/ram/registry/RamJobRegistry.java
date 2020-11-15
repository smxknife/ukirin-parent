package org.ukirin.job.ram.registry;

import com.google.common.base.Preconditions;
import org.ukirin.job.core.instance.Instance;
import org.ukirin.job.core.instance.InstanceStatus;
import org.ukirin.job.core.job.JobDefinition;
import org.ukirin.job.core.registry.JobRegistry;
import org.ukirin.job.core.registry.Watcher;
import org.ukirin.job.core.sharding.ShardingContext;
import org.ukirin.job.core.trigger.JobTrigger;
import org.ukirin.job.core.util.Tuple2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author smxknife
 * 2020/11/12
 */
public class RamJobRegistry implements JobRegistry {

	private List<JobDefinition> jobs;
	private Map<String, Set<Tuple2<Instance, ShardingContext>>> jobInstanceShardingContext;
	private Map<Instance, InstanceStatus> instanceStatusMap;
	private Instance leader;
	private Object leaderLock = new Object();
	private List<Watcher<JobDefinition>> jobWatchers;
	private Map<Instance, Watcher<Tuple2<ShardingContext, JobTrigger>>> instanceTriggerWatcher;

	private RamJobRegistry() {
		jobs = new CopyOnWriteArrayList<>();
		jobWatchers = new CopyOnWriteArrayList<>();
		jobInstanceShardingContext = new ConcurrentHashMap<>();
		instanceStatusMap = new ConcurrentHashMap<>();
		instanceTriggerWatcher = new ConcurrentHashMap<>();
	}

	private static class RamJobRegistryHolder {
		private static final RamJobRegistry REGISTRY = new RamJobRegistry();
	}

	public static RamJobRegistry getRegistry() {
		return RamJobRegistryHolder.REGISTRY;
	}

	@Override
	public void addJobWatcher(Watcher watcher) {
		jobWatchers.add(watcher);
	}

	@Override
	public void addJobTriggerWatcher(Instance instance, Watcher<Tuple2<ShardingContext, JobTrigger>> watcher) {
		instanceTriggerWatcher.put(instance, watcher);
	}

	@Override
	public boolean registerInstance(Instance instance) {
		Preconditions.checkNotNull(instance);
		instanceStatusMap.putIfAbsent(instance, new InstanceStatus(instance));
		boolean isLeader = false;
		synchronized (this.leaderLock) {
			if (Objects.isNull(this.leader)) {
				isLeader = true;
				this.leader = instance;
			}
		}
		return isLeader;
	}

	@Override
	public boolean registerJob(JobDefinition definition) {
		Preconditions.checkNotNull(definition);
		if (jobs.contains(definition) && !definition.isEnableCover()) {
			return true;
		}
		this.jobs.add(definition);
		jobWatchers.stream().forEach(watcher -> watcher.watch(definition));
		return true;
	}

	@Override
	public Set<InstanceStatus> getInstances() {
		return Collections.unmodifiableSet(instanceStatusMap.values().stream().collect(Collectors.toSet()));
	}

	@Override
	public void assignSharding(Map<Instance, ShardingContext> shardingContextMap) {
		shardingContextMap.entrySet().forEach(entry -> {
			Instance instance = entry.getKey();
			ShardingContext context = entry.getValue();
			Set<Tuple2<Instance, ShardingContext>> set = new HashSet<>();
			set.add(new Tuple2<>(instance, context));
			jobInstanceShardingContext.merge(context.getJobName(), set, (l1, l2) -> {
				l1.addAll(l2);
				return l1;
			});
			JobTrigger trigger = new JobTrigger(context.getJobName());
			// TODO: 这里需要重新设计，这里添加trigger并没有后续移除处理，所以如果有大量的触发，这里会导致内存异常
			instanceStatusMap.get(instance).addTrigger(trigger);
			instanceTriggerWatcher.get(instance).watch(new Tuple2<>(context, trigger));
		});

	}

	@Override
	public void trigger(JobTrigger trigger) {
		Tuple2<Instance, ShardingContext> tuple2 = jobInstanceShardingContext.get(trigger.getJobName()).stream()
				.findAny().orElseThrow(() ->
						new IllegalStateException(String.format("the jobName = %s is not exist\r\n",
								trigger.getJobName())));
		Instance instance = tuple2.getE1();
		ShardingContext context = tuple2.getE2();
		instanceTriggerWatcher.get(instance).watch(new Tuple2<>(context, trigger));
	}
}
