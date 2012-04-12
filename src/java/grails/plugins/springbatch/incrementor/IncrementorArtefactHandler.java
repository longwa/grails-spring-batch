package grails.plugins.springbatch.incrementor;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;

public class IncrementorArtefactHandler extends ArtefactHandlerAdapter {

    public static final String TYPE = "BatchIncrementor";

    public IncrementorArtefactHandler() {
        super(TYPE, GrailsIncrementorClass.class, DefaultGrailsIncrementorClass.class, null);
    }

    public boolean isArtefactClass(Class clazz) {
        // class shouldn't be null and shoud ends with Job suffix
        if(clazz == null || !clazz.getName().endsWith(TYPE)) return false;
        return true;
    }
}