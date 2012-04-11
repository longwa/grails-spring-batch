package grails.plugins.springbatch.job;

import grails.plugins.springbatch.step.StepArtefactHandler;
import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.JobParametersValidator;

import java.util.ArrayList;
import java.util.List;

public class DefaultGrailsJobClass extends AbstractInjectableGrailsClass implements GrailsJobClass {

    public static final String JOB = "Job";

    private List<Class> steps;

    public DefaultGrailsJobClass(Class clazz) {
        super(clazz, JOB);
        loadSteps();
    }

    public boolean isRestartable() {
        return (Boolean) (getMetaClass().invokeMethod(getReferenceInstance(), GrailsJobClassProperty.RESTARTABLE, new Object[]{}));
    }

    public void execute(JobExecution jobExecution) {
        getMetaClass().invokeMethod(getReferenceInstance(), GrailsJobClassProperty.EXECUTE, new Object[]{jobExecution});
    }

    public JobParametersIncrementer getJobParametersIncrementer() {
        return (JobParametersIncrementer) (getMetaClass().invokeMethod(getReferenceInstance(), GrailsJobClassProperty.INCREMENTOR, new Object[]{}));
    }

    public JobParametersValidator getJobParametersValidator() {
        return (JobParametersValidator) (getMetaClass().invokeMethod(getReferenceInstance(), GrailsJobClassProperty.VALIDATOR, new Object[]{}));
    }

    public List<Class> getSteps() {
        return steps;
    }

    private void loadSteps() {
        List stepClasses = (List) GrailsClassUtils.getStaticPropertyValue(getClazz(), "steps");
        if(stepClasses == null || stepClasses.isEmpty()) {
            throw new RuntimeException("Must specify steps for job class: " + getClazz().getName());
        }
        List<Class> steps = new ArrayList<Class>();
        for(Object o : stepClasses) {
            if(!(o instanceof Class))
                throw new RuntimeException("Must provide classes for steps.");
            Class clazz = (Class) o;
            //TODO can't do this yet cause grailsApplication is null
//            if(!grailsApplication.isArtefactOfType(StepArtefactHandler.TYPE, clazz))
//                throw new RuntimeException("Steps must be of type: " + StepArtefactHandler.TYPE);
            steps.add(clazz);
        }
        this.steps = steps;
    }
}
