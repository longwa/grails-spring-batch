package grails.plugins.springbatch.listener;

import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;

public class DefaultGrailsJobListenerClass extends AbstractInjectableGrailsClass implements GrailsJobListenerClass {

    public DefaultGrailsJobListenerClass(Class clazz) {
        super(clazz, JobListenerArtefactHandler.TYPE);
    }
}