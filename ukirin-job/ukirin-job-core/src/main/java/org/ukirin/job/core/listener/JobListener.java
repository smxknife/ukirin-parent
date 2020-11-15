package org.ukirin.job.core.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author smxknife
 * 2020/11/13
 */
public interface JobListener {
	/**
	 * 监听器名称
	 * @return
	 */
	String getName();

	/**
	 * 执行前调用
	 * @param jobName
	 */
	void beforeExecute(String jobName);

	/**
	 * 执行后调用
	 * @param jobName
	 */
	void afterExecute(String jobName);

	class Loader {
		public static List<JobListener> load(ClassLoader classLoader) {
			List<JobListener> listeners = new ArrayList<>();
			ServiceLoader.load(JobListener.class, classLoader).forEach(listeners::add);
			return listeners;
		}
	}


}
