package org.ukirin.job.core.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author smxknife
 * 2020/11/13
 */
public interface Infrastructure {

	List<Infrastructure> INFRASTRUCTURES = Collections.unmodifiableList(Loader.load());

	/**
	 * 从底层设施获取具体的JobController
	 * @return
	 */
	JobController getJobController();

	class Loader {
		static List<Infrastructure> load() {
			List<Infrastructure> infrastructures = new ArrayList<>();
			ServiceLoader.load(Infrastructure.class, Loader.class.getClassLoader())
					.forEach(infrastructures::add);
			return infrastructures;
		}
	}
}
