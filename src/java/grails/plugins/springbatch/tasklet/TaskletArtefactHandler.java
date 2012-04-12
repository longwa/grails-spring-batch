package grails.plugins.springbatch.tasklet;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;
import org.springframework.batch.core.step.tasklet.Tasklet;

public class TaskletArtefactHandler extends ArtefactHandlerAdapter {

    public static final String TYPE = "BatchTasklet";

    public TaskletArtefactHandler() {
        super(TYPE, GrailsTaskletClass.class, DefaultGrailsTaskletClass.class, null);
    }

    public boolean isArtefactClass(Class clazz) {
        // class shouldn't be null and shoud ends with Job suffix
        if(clazz == null || !clazz.getName().endsWith(TYPE)) return false;
        return true;
    }
}
