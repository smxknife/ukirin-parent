package org.ukirin.job.infrastructure.quartz.controller;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.ukirin.job.core.listener.JobListener;

/**
 * @author smxknife
 * 2020/11/13
 */
public class QuartzJobListenerAdapter implements org.quartz.JobListener{

	private JobListener jobListener;

	public QuartzJobListenerAdapter(JobListener jobListener) {
		this.jobListener = jobListener;
	}

	@Override
	public String getName() {
		return jobListener.getName();
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		jobListener.beforeExecute(context.getJobDetail().getKey().getName());
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		jobListener.afterExecute(context.getJobDetail().getKey().getName());
	}

	@Override
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		jobListener.afterExecute(context.getJobDetail().getKey().getName());
	}
}
