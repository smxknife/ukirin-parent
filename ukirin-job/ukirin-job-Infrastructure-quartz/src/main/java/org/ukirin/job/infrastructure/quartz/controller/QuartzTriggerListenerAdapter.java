package org.ukirin.job.infrastructure.quartz.controller;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.ukirin.job.core.listener.TriggerListener;
import org.ukirin.job.core.sharding.ShardingContext;

/**
 * @author smxknife
 * 2020/11/13
 */
public class QuartzTriggerListenerAdapter extends TriggerListenerSupport {

	private TriggerListener triggerListener;

	public QuartzTriggerListenerAdapter(TriggerListener triggerListener) {
		this.triggerListener = triggerListener;
	}

	@Override
	public String getName() {
		return triggerListener.getName();
	}

	@Override
	public void triggerMisfired(final Trigger trigger) {
		if (null != trigger.getPreviousFireTime()) {
			ShardingContext context = (ShardingContext) trigger.getJobDataMap().get("shardingContext");
//			context.getParameters().put("status", "misfired");
//			context.getParameters().put("previousFireTime", trigger.getPreviousFireTime());
			triggerListener.triggerMisfired(trigger.getJobKey().getName(), context);
		}
	}

	@Override
	public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
		ShardingContext shardingContext = (ShardingContext) context.getJobDetail().getJobDataMap().get("shardingContext");
		triggerListener.triggerComplete(trigger.getJobKey().getName(), shardingContext);
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		ShardingContext shardingContext = (ShardingContext) context.getJobDetail().getJobDataMap().get("shardingContext");
		triggerListener.triggerFired(trigger.getJobKey().getName(), shardingContext);
	}
}
