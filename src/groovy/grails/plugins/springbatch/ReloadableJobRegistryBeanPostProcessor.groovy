package grails.plugins.springbatch

import org.springframework.batch.core.Job
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor
import org.springframework.beans.BeansException

class ReloadableJobRegistryBeanPostProcessor extends JobRegistryBeanPostProcessor {

    protected JobRegistry jobRegistry

    /**
     * Injection setter for {@link org.springframework.batch.core.configuration.JobRegistry}.
     *
     * @param jobRegistry the jobConfigurationRegistry to set
     */
    public void setJobRegistry(JobRegistry jobRegistry) {
        super.setJobRegistry(jobRegistry)
        this.jobRegistry = jobRegistry;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Job) {
            Job job = (Job) bean;
            jobRegistry.unregister(job.name)
            super.postProcessAfterInitialization(bean, beanName)
        }
        return bean;
    }
}
