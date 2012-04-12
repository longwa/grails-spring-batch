package grails.plugins.springbatch.job;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;
import org.springframework.batch.core.Job;

public class JobArtefactHandler extends ArtefactHandlerAdapter {

    public static final String TYPE = "BatchJob";

    public JobArtefactHandler() {
        super(TYPE, GrailsJobClass.class, DefaultGrailsJobClass.class, null);
    }

    public boolean isArtefactClass(Class clazz) {
        // class shouldn't be null and shoud ends with Job suffix
        if(clazz == null || !clazz.getName().endsWith(TYPE)) return false;
        return true;
    }
}
