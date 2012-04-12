package grails.plugins.springbatch.step;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;
import org.springframework.batch.core.Step;

public class StepArtefactHandler extends ArtefactHandlerAdapter {

    public static final String TYPE = "BatchStep";

    public StepArtefactHandler() {
        super(TYPE, GrailsStepClass.class, DefaultGrailsStepClass.class, null);
    }

    public boolean isArtefactClass(Class clazz) {
        // class shouldn't be null and shoud ends with Job suffix
        if(clazz == null || !clazz.getName().endsWith(TYPE)) return false;
        return true;
    }
}