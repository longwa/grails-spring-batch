package grails.plugins.springbatch.step;

import grails.plugins.springbatch.tasklet.TaskletArtefactHandler;
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
        Class taskletClass = (Class) GrailsClassUtils.getStaticPropertyValue(getClazz(), "tasklet");
        if(false) {
            //TODO can't do this yet cause grailsApplication is null
//        if(taskletClass == null || !grailsApplication.isArtefactOfType(TaskletArtefactHandler.TYPE, taskletClass)) {
//            throw new RuntimeException("Must specify tasklet for step class: " + getClazz().getName());
        } else {
            this.taskletClass = taskletClass;
        }
    }
}
