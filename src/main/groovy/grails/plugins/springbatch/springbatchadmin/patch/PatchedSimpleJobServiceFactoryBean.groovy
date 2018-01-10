package grails.plugins.springbatch.springbatchadmin.patch

import org.springframework.batch.admin.service.JobService
import org.springframework.batch.admin.service.SimpleJobService
import org.springframework.batch.admin.service.SimpleJobServiceFactoryBean
import org.springframework.batch.core.configuration.JobLocator
import org.springframework.batch.core.configuration.ListableJobLocator
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.InitializingBean

/**
 * The only purpose of this class is to create a patched version of the SimpleJobService.
 * It should be discarded at the earliest opportunity.
 * 
 *
 */
class PatchedSimpleJobServiceFactoryBean implements FactoryBean<JobService>, InitializingBean {
	
	@Delegate
	SimpleJobServiceFactoryBean simpleJobServiceFactoryBean = new SimpleJobServiceFactoryBean()
	
	/**
	 * Create a {@link SimpleJobService} from the configuration provided.
	 *
	 * @see FactoryBean#getObject()
	 */
	JobService getObject() throws Exception {
		return new PatchedSimpleJobService(
			simpleJobServiceFactoryBean.createJobInstanceDao(), 
			simpleJobServiceFactoryBean.createJobExecutionDao(), 
			simpleJobServiceFactoryBean.createStepExecutionDao(),
			jobRepository, jobLauncher, jobLocator, 
			simpleJobServiceFactoryBean.createExecutionContextDao())
	}

	/**
	 * Tells the containing bean factory what kind of object is the product of
	 * {@link #getObject()}.
	 *
	 * @return SimpleJobService
	 * @see FactoryBean#getObjectType()
	 */
	Class<? extends JobService> getObjectType() {
		return PatchedSimpleJobService
	}

	void afterPropertiesSet() throws Exception {
		simpleJobServiceFactoryBean.afterPropertiesSet()
	}

	boolean isSingleton() {
		return true
	}

	private JobRepository jobRepository
	private JobLauncher jobLauncher
	private JobLocator jobLocator
	
	void setJobRepository(JobRepository jobRepository) {
		this.jobRepository = jobRepository
		simpleJobServiceFactoryBean.jobRepository= jobRepository
	}

	void setJobLauncher(JobLauncher jobLauncher) {
		this.jobLauncher = jobLauncher
		simpleJobServiceFactoryBean.jobLauncher = jobLauncher
	}

	void setJobLocator(ListableJobLocator jobLocator) {
		this.jobLocator = jobLocator
		simpleJobServiceFactoryBean.jobLocator = jobLocator
	}
}
