package grails.plugins.springbatch.tasklet;

import org.codehaus.groovy.grails.commons.InjectableGrailsClass;
import org.springframework.batch.core.step.tasklet.Tasklet;

public interface GrailsTaskletClass extends InjectableGrailsClass, Tasklet {
}