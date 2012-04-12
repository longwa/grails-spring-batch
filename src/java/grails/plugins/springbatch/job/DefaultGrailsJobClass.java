package grails.plugins.springbatch.job;

import grails.util.GrailsNameUtils;
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

    @SuppressWarnings("unchecked")
    private void loadSteps() {
        List stepOrder = (List) GrailsClassUtils.getStaticPropertyValue(getClazz(), "steps");
        //If not defined, add 1 step with the same same as the Job
        if(stepOrder == null) {
            stepOrder = new ArrayList();
        }
        if(stepOrder.isEmpty()) {
            stepOrder.add(getName());
        }
        List<Class> steps = new ArrayList<Class>();
        for(Object o : stepOrder) {
            if(!((o instanceof Class) || (o instanceof String))) {
                throw new RuntimeException("Must provide class or string for steps.");
            }
            if(o instanceof Class) {
                Class clazz = (Class) o;
                steps.add(clazz);
            } else {
                String clazz = GrailsNameUtils.getClassName((String) o, "Step");
                String jobName = getName();
                String stepName = getPackageName() + "." + jobName + clazz;
                try {
                    Class stepClass = getClass().getClassLoader().loadClass(stepName);
                    steps.add(stepClass);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        this.steps = steps;
    }
}
