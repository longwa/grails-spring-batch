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
        Object tasklet = GrailsClassUtils.getStaticPropertyValue(getClazz(), "tasklet");
        if(!((tasklet instanceof Class) || (tasklet instanceof String))) {
            throw new RuntimeException("Must specify class or string for tasklet");
        }
        if(tasklet instanceof Class) {
            this.taskletClass = (Class) tasklet;
        } else {
//            String clazz = GrailsNameUtils.getClassName((String) tasklet, "Tasklet");
//            String stepName = getName();
//            String taskletName = stepName + clazz;
//            try {
//                Class taskletClass = Thread.currentThread().getContextClassLoader().loadClass(taskletName);
//                this.taskletClass = taskletClass;
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            }
        }
    }
}
