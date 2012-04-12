package grails.plugins.springbatch.job;

import grails.plugins.springbatch.step.StepArtefactHandler;
import grails.util.GrailsNameUtils;
import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.JobParametersValidator;

import java.util.ArrayList;
import java.util.List;

public class DefaultGrailsJobClass extends AbstractInjectableGrailsClass implements GrailsJobClass {

    private List<Class> steps;
    private List<Class> listeners;

    public DefaultGrailsJobClass(Class clazz) {
        super(clazz, JobArtefactHandler.TYPE);
        loadSteps();
        loadListeners();
    }

    public boolean isRestartable() {
        Boolean restartable = (Boolean) (GrailsClassUtils.getPropertyValueOfNewInstance(getClazz(), GrailsJobClassProperty.RESTARTABLE));
        if(restartable == null) {
            //if not set defined, then set to true since that's the Spring Batch default.
            restartable = true;
        }
        return restartable;
    }

    public Class getIncrementor() {
        return (Class) (GrailsClassUtils.getPropertyValueOfNewInstance(getClazz(), GrailsJobClassProperty.INCREMENTOR));
    }

    public Class getValidator() {
        return (Class) (GrailsClassUtils.getPropertyValueOfNewInstance(getClazz(), GrailsJobClassProperty.VALIDATOR));
    }

    public List<Class> getSteps() {
        return steps;
    }

    public List<Class> getListeners() {
        return listeners;
    }

    @SuppressWarnings("unchecked")
    private void loadSteps() {
        List stepOrder = (List) GrailsClassUtils.getPropertyValueOfNewInstance(getClazz(), GrailsJobClassProperty.STEPS);
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
                String clazz = GrailsNameUtils.getClassName((String) o, StepArtefactHandler.TYPE);
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

    private void loadListeners() {
        this.listeners = new ArrayList<Class>();
    }
}
