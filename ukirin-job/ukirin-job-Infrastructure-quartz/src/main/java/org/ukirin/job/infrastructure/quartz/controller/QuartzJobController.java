package org.ukirin.job.infrastructure.quartz.controller;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.ukirin.job.core.controller.AbstractJobController;
import org.ukirin.job.core.executor.JobExecutor;
import org.ukirin.job.core.job.Job;
import org.ukirin.job.core.listener.JobListener;
import org.ukirin.job.core.listener.TriggerListener;
import org.ukirin.job.core.sharding.ShardingContext;
import org.ukirin.job.core.trigger.JobTrigger;
import org.ukirin.job.infrastructure.quartz.job.QuartzJob;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * @author smxknife
 * 2020/11/13
 */
public class QuartzJobController extends AbstractJobController {

	private Scheduler scheduler;

	public QuartzJobController() {}

	public QuartzJobController(JobExecutor jobExecutor) {
		super(jobExecutor);
	}

	@Override
	public void doInit() {
		try {
			StdSchedulerFactory factory = new StdSchedulerFactory();
			//factory.initialize(getBaseQuartzProperties());
			scheduler = factory.getScheduler();

			for (TriggerListener listener : getTriggerListeners()) {
				scheduler.getListenerManager().addTriggerListener(new QuartzTriggerListenerAdapter(listener));
			}

			for (JobListener listener : getJobListeners()) {
				scheduler.getListenerManager().addJobListener(new QuartzJobListenerAdapter(listener));
			}

		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doStart() {
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doTrigger(ShardingContext context, JobTrigger jobTrigger) throws Exception {
		LocalDateTime triggerTime = jobTrigger.getTriggerTime();

		JobDetail jobDetail = null;
		Trigger trigger = null;
		String jobIdentity = createJobIdentity(context.getJobName(), context.getShardingIndex());

		if (Objects.isNull(triggerTime)) {
			jobDetail = createJobDetail(jobIdentity, context.getJobClass(), context,
					triggerTime, jobTrigger.getParameters());
			trigger = createCronTrigger(jobIdentity, context.getCron());
			trigger.getTriggerBuilder().usingJobData(jobDetail.getJobDataMap());
		} else {
			jobDetail = createJobDetail(jobIdentity, context.getJobClass(), context,
					triggerTime, jobTrigger.getParameters());
			trigger = createOneOffTrigger(jobIdentity);
			trigger.getTriggerBuilder().usingJobData(jobDetail.getJobDataMap());

		}

		if (scheduler.checkExists(JobKey.jobKey(jobIdentity))) {
			scheduler.triggerJob(JobKey.jobKey(jobIdentity), jobDetail.getJobDataMap());
		} else {
			scheduler.scheduleJob(jobDetail, trigger);
		}


	}

	@Override
	public void pause(Job job) {
		// TODO
	}

//	private Properties getBaseQuartzProperties() {
//		Properties result = new Properties();
//		result.put("org.quartz.threadPool.class", org.quartz.simpl.SimpleThreadPool.class.getName());
//		result.put("org.quartz.threadPool.threadCount", "15");
//		//result.put("org.quartz.scheduler.instanceName", liteJobConfig.getJobName());
//		result.put("org.quartz.jobStore.misfireThreshold", "1");
//		//result.put("org.quartz.plugin.shutdownhook.class", JobShutdownHookPlugin.class.getName());
//		//result.put("org.quartz.plugin.shutdownhook.cleanShutdown", Boolean.TRUE.toString());
//		return result;
//	}

	private String createJobIdentity(String jobName, int shardingIndex) {
		return String.format("%s@%s", jobName, shardingIndex);
	}

	private JobDetail createJobDetail(final String jobName, final String jobClass, ShardingContext shardingContext, LocalDateTime triggerTime, Map<String, Object> parameters) {
		JobDetail result = JobBuilder.newJob(QuartzJob.class).withIdentity(jobName).build();
		JobDataMap jobDataMap = result.getJobDataMap();
		jobDataMap.put("jobExecutor", jobExecutor);
		jobDataMap.put("shardingContext", shardingContext);
		jobDataMap.put("triggerTime", triggerTime);
		jobDataMap.put("parameters", parameters);
		try {
			Class<?> jobClazz = Class.forName(jobClass);
			if (!Job.class.isAssignableFrom(jobClazz)) {
				throw new IllegalArgumentException(String.format("Job: Job class '%s' is not subclass of '%s'", jobClass, Job.class.getName()));
			}
			jobDataMap.put("job", jobClazz.newInstance());
		} catch (final ReflectiveOperationException ex) {
			throw new IllegalArgumentException(String.format("Job: Job class '%s' can not initialize.", jobClass));
		}
		return result;
	}

	private CronTrigger createCronTrigger(final String jobName, final String cron) {
		return TriggerBuilder.newTrigger().withIdentity(jobName).withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing()).build();
	}


	private Trigger createOneOffTrigger(final String jobName) {
		return TriggerBuilder.newTrigger().withIdentity(jobName).withSchedule(SimpleScheduleBuilder.simpleSchedule()).build();
	}
}
