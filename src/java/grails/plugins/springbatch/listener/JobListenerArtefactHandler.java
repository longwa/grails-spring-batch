package grails.plugins.springbatch.listener;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;

public class JobListenerArtefactHandler extends ArtefactHandlerAdapter {

    public static final String TYPE = "BatchJobListener";

    public JobListenerArtefactHandler() {
        super(TYPE, GrailsJobListenerClass.class, DefaultGrailsJobListenerClass.class, null);
    }

    public boolean isArtefactClass(Class clazz) {
        // class shouldn't be null and shoud ends with Job suffix
        if(clazz == null || !clazz.getName().endsWith(TYPE)) return false;
        return true;
    }
}