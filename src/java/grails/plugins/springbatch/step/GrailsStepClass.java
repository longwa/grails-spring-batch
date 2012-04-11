package grails.plugins.springbatch.step;

import org.codehaus.groovy.grails.commons.InjectableGrailsClass;
import org.springframework.batch.core.Step;

public interface GrailsStepClass extends InjectableGrailsClass, Step {

    Class getTaskletClass();
}