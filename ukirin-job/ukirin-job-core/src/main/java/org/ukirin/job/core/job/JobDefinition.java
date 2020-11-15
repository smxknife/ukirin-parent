package org.ukirin.job.core.job;

import lombok.Getter;

/**
 * @author smxknife
 * 2020/11/12
 */
@Getter
public class JobDefinition {
	private String jobName;
	private String className;
	private String cron;
	private int shardingCount;
	private boolean enableCover = false;

	public JobDefinition(String jobName, String className, String cron, int shardingCount, boolean enableCover) {
		this.jobName = jobName;
		this.className = className;
		this.cron = cron;
		this.shardingCount = shardingCount;
		this.enableCover = enableCover;
	}

}
