package grails.plugins.springbatch.job;

import grails.plugins.springbatch.incrementor.IncrementorArtefactHandler;
import grails.plugins.springbatch.step.StepArtefactHandler;
import grails.plugins.springbatch.validator.ValidatorArtefactHandler;
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
    private Class validator;
    private Class incrementor;

    public DefaultGrailsJobClass(Class clazz) {
        super(clazz, JobArtefactHandler.TYPE);
        loadSteps();
        loadValidator();
        loadIncrementor();
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
        return validator;
    }

    public List<Class> getSteps() {
        return steps;
    }

    public List<Class> getListeners() {
        return listeners;
    }

    private void loadValidator() {
        Class validator = (Class) GrailsClassUtils.getPropertyValueOfNewInstance(getClazz(), GrailsJobClassProperty.VALIDATOR);
        if(validator == null) {
            String clazz = GrailsNameUtils.getClassName(getName(), ValidatorArtefactHandler.TYPE);
            String validatorName = getPackageName() + "." + clazz;
            try {
                Class validatorClass = getClass().getClassLoader().loadClass(validatorName);
                if(validatorClass != null) {
                    validator = validatorClass;
                } else {
                    validator = null;
                }
            } catch (ClassNotFoundException e) {
                validator = null;
            }
        }
        this.validator = validator;
    }

    private void loadIncrementor() {
        Class incrementor = (Class) GrailsClassUtils.getPropertyValueOfNewInstance(getClazz(), GrailsJobClassProperty.INCREMENTOR);
        if(incrementor == null) {
            String clazz = GrailsNameUtils.getClassName(getName(), IncrementorArtefactHandler.TYPE);
            String incrementorName = getPackageName() + "." + clazz;
            try {
                Class incrementorClass = getClass().getClassLoader().loadClass(incrementorName);
                if(incrementorClass != null) {
                    incrementor = incrementorClass;
                } else {
                    incrementor = null;
                }
            } catch (ClassNotFoundException e) {
                incrementor = null;
            }
        }
        this.incrementor = incrementor;
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
