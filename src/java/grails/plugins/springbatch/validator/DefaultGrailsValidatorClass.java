package grails.plugins.springbatch.validator;

import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;

public class DefaultGrailsValidatorClass extends AbstractInjectableGrailsClass implements GrailsValidatorClass {

    public DefaultGrailsValidatorClass(Class clazz) {
        super(clazz, ValidatorArtefactHandler.TYPE);
    }
}