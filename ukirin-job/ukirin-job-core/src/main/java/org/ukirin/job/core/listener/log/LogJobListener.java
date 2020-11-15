package org.ukirin.job.core.listener.log;

import lombok.extern.slf4j.Slf4j;
import org.ukirin.job.core.listener.JobListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author smxknife
 * 2020/11/13
 */
@Slf4j
public class LogJobListener implements JobListener {

	private static final String FLAG = "LOG";
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	@Override
	public String getName() {
		return FLAG;
	}

	@Override
	public void beforeExecute(String jobName) {
		log.info("::: {} ::: [jobName = {}] begin execute at {}", FLAG, jobName, LocalDateTime.now().format(FORMATTER));
	}

	@Override
	public void afterExecute(String jobName) {
		log.info("::: {} ::: [jobName = {}] finish execute at {}", FLAG, jobName, LocalDateTime.now().format(FORMATTER));
	}
}
