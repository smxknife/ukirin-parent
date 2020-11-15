package org.ukirin.job.core.instance;

import lombok.Getter;
import org.ukirin.job.core.trigger.JobTrigger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author smxknife
 * 2020/11/13
 */
public class InstanceStatus {

	@Getter
	private Instance instance;
	//private List<JobTrigger> triggers;
	private AtomicInteger triggersCount;

	public InstanceStatus(Instance instance) {
		this.instance = instance;
		this.triggersCount = new AtomicInteger(0);
		//this.triggers = new CopyOnWriteArrayList<>();
	}

	public int addTrigger(JobTrigger trigger) {
		//triggers.add(trigger);
		return this.triggersCount.incrementAndGet();
	}

	public int getTriggersCount() {
		return this.triggersCount.get();
	}


}
