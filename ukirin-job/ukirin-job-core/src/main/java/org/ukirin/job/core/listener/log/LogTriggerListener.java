package org.ukirin.job.core.listener.log;

import lombok.extern.slf4j.Slf4j;
import org.ukirin.job.core.listener.TriggerListener;
import org.ukirin.job.core.sharding.ShardingContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author smxknife
 * 2020/11/13
 */
@Slf4j
public class LogTriggerListener implements TriggerListener {
	private static final String FLAG = "LOG";
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public String getName() {
		return FLAG;
	}

	@Override
	public void triggerFired(String jobName, ShardingContext context) {
		log.info("::: {} ::: [jobName = {}] trigger fired at {}", FLAG, jobName, LocalDateTime.now().format(FORMATTER));
	}

	@Override
	public void triggerComplete(String jobName, ShardingContext shardingContext) {
		log.info("::: {} ::: [jobName = {}] trigger complete at {}", FLAG, jobName, LocalDateTime.now().format(FORMATTER));
	}

	@Override
	public void triggerMisfired(String jobName, ShardingContext context) {
		log.info("::: {} ::: [jobName = {}] trigger misfired at {}", FLAG, jobName, LocalDateTime.now().format(FORMATTER));
	}
}
