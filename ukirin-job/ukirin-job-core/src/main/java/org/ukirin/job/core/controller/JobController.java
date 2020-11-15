package org.ukirin.job.core.controller;

import org.ukirin.job.core.job.Job;
import org.ukirin.job.core.listener.JobListener;
import org.ukirin.job.core.listener.TriggerListener;
import org.ukirin.job.core.sharding.ShardingContext;
import org.ukirin.job.core.trigger.JobTrigger;

import java.util.List;

/**
 * 控制器
 * @author smxknife
 * 2020/11/12
 */
public interface JobController {

	/**
	 * 通过触发器来启动job
	 * @param context
	 * @param trigger
	 */
	void trigger(ShardingContext context, JobTrigger trigger);

	/**
	 * 初始化
	 */
	void init();

	/**
	 * 启动
	 */
	void start();

	/**
	 * 暂停一个job
	 * @param job
	 */
	void pause(Job job);

	/**
	 * 获取JobListeners监听
	 * @return
	 */
	List<JobListener> getJobListeners();

	/**
	 * 获取TriggerListener监听
	 * @return
	 */
	List<TriggerListener> getTriggerListeners();
}
