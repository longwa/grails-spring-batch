package grails.plugins.springbatch.tasklet;

import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

public class DefaultGrailsTaskletClass extends AbstractInjectableGrailsClass implements GrailsTaskletClass {

    public DefaultGrailsTaskletClass(Class clazz) {
        super(clazz, TaskletArtefactHandler.TYPE);
    }

    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        return (RepeatStatus) (getMetaClass().invokeMethod(getReferenceInstance(), GrailsTaskletClassProperty.EXECUTE, new Object[]{stepContribution, chunkContext}));
    }
}
