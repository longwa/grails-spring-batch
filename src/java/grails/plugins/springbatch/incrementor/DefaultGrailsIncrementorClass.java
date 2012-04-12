package grails.plugins.springbatch.incrementor;

import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;

public class DefaultGrailsIncrementorClass extends AbstractInjectableGrailsClass implements GrailsIncrementorClass {

    public DefaultGrailsIncrementorClass(Class clazz) {
        super(clazz, IncrementorArtefactHandler.TYPE);
    }
}