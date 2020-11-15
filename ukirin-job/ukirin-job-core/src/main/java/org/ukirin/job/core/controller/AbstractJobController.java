package org.ukirin.job.core.controller;

import org.ukirin.job.core.executor.JobExecutor;
import org.ukirin.job.core.executor.SimpleJobExecutor;
import org.ukirin.job.core.listener.JobListener;
import org.ukirin.job.core.listener.TriggerListener;
import org.ukirin.job.core.sharding.ShardingContext;
import org.ukirin.job.core.trigger.JobTrigger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author smxknife
 * 2020/11/13
 */
public abstract class AbstractJobController implements JobController {

	private volatile boolean isStarted = false;
	private List<JobListener> jobListeners;
	private List<TriggerListener> triggerListeners;
	protected JobExecutor jobExecutor;

	public AbstractJobController() {
		this(new SimpleJobExecutor());
	}

	public AbstractJobController(JobExecutor jobExecutor) {
		this.jobExecutor = jobExecutor;
		this.jobListeners = new CopyOnWriteArrayList<>();
		this.triggerListeners = new CopyOnWriteArrayList<>();
	}

	@Override
	public final void init() {
		ClassLoader classLoader = this.getClass().getClassLoader();
		this.jobListeners.addAll(JobListener.Loader.load(classLoader));
		this.triggerListeners.addAll(TriggerListener.Loader.load(classLoader));
		doInit();
	}

	@Override
	public final void start() {
		doStart();
		this.isStarted = true;
	}

	protected void doInit() {}

	protected void doStart() {}

	@Override
	public final void trigger(ShardingContext context, JobTrigger trigger) {
		if (!this.isStarted) {
			throw new UnsupportedOperationException("JobController does not started");
		}
		try {
			doTrigger(context, trigger);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 如何根据触发器生成任务交由底层设备来操作
	 * @param context
	 * @param trigger
	 * @throws Exception
	 */
	protected abstract void doTrigger(ShardingContext context, JobTrigger trigger) throws Exception;

	@Override
	public List<JobListener> getJobListeners() {
		return Collections.unmodifiableList(this.jobListeners);
	}

	@Override
	public List<TriggerListener> getTriggerListeners() {
		return Collections.unmodifiableList(this.triggerListeners);
	}
}
