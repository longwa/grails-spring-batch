package grails.plugins.springbatch.job;

import org.codehaus.groovy.grails.commons.InjectableGrailsClass;
import org.springframework.batch.core.Job;

import java.util.List;

public interface GrailsJobClass extends InjectableGrailsClass, Job {

    public List<Class> getSteps();
}