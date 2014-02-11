package groovy.annotation

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass(["groovy.annotation.transformation.EntityEqualsAndHashCodeASTTransformation"])
@interface EntityEqualsAndHashCode {
    String[] idFields();
    String[] uniqueFields();
}
