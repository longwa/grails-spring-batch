package grails.plugins.springbatch.validator;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;
import org.springframework.batch.core.JobParametersValidator;

public class ValidatorArtefactHandler extends ArtefactHandlerAdapter {

    public static final String TYPE = "BatchValidator";

    public ValidatorArtefactHandler() {
        super(TYPE, GrailsValidatorClass.class, DefaultGrailsValidatorClass.class, null);
    }

    public boolean isArtefactClass(Class clazz) {
        // class shouldn't be null and shoud ends with Job suffix
        if(clazz == null || !clazz.getName().endsWith(TYPE) || !JobParametersValidator.class.isAssignableFrom(clazz)) return false;
        return true;
    }
}