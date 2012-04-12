package grails.plugins.springbatch.step;

import grails.util.GrailsNameUtils;
import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;
import org.codehaus.groovy.grails.commons.GrailsClassUtils;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.StepExecution;

public class DefaultGrailsStepClass extends AbstractInjectableGrailsClass implements GrailsStepClass {

    public static final String STEP = "Step";
    private Class taskletClass;

    public DefaultGrailsStepClass(Class clazz) {
        super(clazz, STEP);
        loadTasklet();
    }

    public boolean isAllowStartIfComplete() {
        return (Boolean) (getMetaClass().invokeMethod(getReferenceInstance(), GrailsStepClassProperty.ALLOW_START, new Object[]{}));
    }

    public int getStartLimit() {
        return (Integer) (getMetaClass().invokeMethod(getReferenceInstance(), GrailsStepClassProperty.START_LIMIT, new Object[]{}));
    }

    public void execute(StepExecution stepExecution) throws JobInterruptedException {
        getMetaClass().invokeMethod(getReferenceInstance(), GrailsStepClassProperty.EXECUTE, new Object[]{stepExecution});
    }

    public Class getTaskletClass() {
        return taskletClass;
    }

    private void loadTasklet() {
        String stepName = getName();
        Object tasklet = GrailsClassUtils.getStaticPropertyValue(getClazz(), "tasklet");
        //If no tasklet defined, then try to find a Tasklet with the same name as this step.
        if(tasklet == null) {
            tasklet = stepName;
        }
        if(!((tasklet instanceof Class) || (tasklet instanceof String))) {
            throw new RuntimeException("Must specify class or string for tasklet");
        }
        if(tasklet instanceof Class) {
            this.taskletClass = (Class) tasklet;
        } else {
            String clazz = GrailsNameUtils.getClassName((String) tasklet, "Tasklet");
            String fullClassName = getPackageName() + "." + clazz;
            try {
                Class taskletClass = getClass().getClassLoader().loadClass(fullClassName);
                this.taskletClass = taskletClass;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
