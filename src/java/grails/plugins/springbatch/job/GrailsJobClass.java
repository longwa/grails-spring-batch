package grails.plugins.springbatch.job;

import org.codehaus.groovy.grails.commons.InjectableGrailsClass;
import org.springframework.batch.core.Job;

import java.util.List;

public interface GrailsJobClass extends InjectableGrailsClass {

    public List<Class> getSteps();

    public List<Class> getListeners();

    public Class getValidator();

    public Class getIncrementor();

    public boolean isRestartable();
}