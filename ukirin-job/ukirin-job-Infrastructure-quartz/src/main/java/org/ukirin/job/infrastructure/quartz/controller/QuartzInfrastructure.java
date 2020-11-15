package org.ukirin.job.infrastructure.quartz.controller;

import org.ukirin.job.core.controller.Infrastructure;
import org.ukirin.job.core.controller.JobController;

/**
 * @author smxknife
 * 2020/11/13
 */
public class QuartzInfrastructure implements Infrastructure {
	@Override
	public JobController getJobController() {
		return new QuartzJobController();
	}
}
