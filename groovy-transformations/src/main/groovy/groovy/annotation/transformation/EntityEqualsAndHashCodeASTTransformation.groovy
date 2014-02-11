package groovy.annotation.transformation

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static org.codehaus.groovy.control.CompilePhase.CANONICALIZATION
import static org.codehaus.groovy.control.CompilePhase.FINALIZATION
import static org.codehaus.groovy.control.CompilePhase.INSTRUCTION_SELECTION

@GroovyASTTransformation(phase=CANONICALIZATION)
class EntityEqualsAndHashCodeASTTransformation extends AbstractASTTransformation {

    List<String> idFields = []
    List<String> uniqueFields = []

    public void visit(ASTNode[] nodes, SourceUnit sourceUnit) {
        init(nodes, sourceUnit);
        // get methods from the annotated object
        AnnotatedNode targetClass = (AnnotatedNode) nodes[1]
        AnnotationNode anno = (AnnotationNode) nodes[0]
        idFields = getMemberList(anno, "idFields")
        uniqueFields = getMemberList(anno, "uniqueFields")
        if (targetClass instanceof ClassNode) {
            ClassNode cNode = (ClassNode) targetClass;
            MethodNode hashCodeMethod = makeHashCodeMethod(cNode)
            MethodNode equalsMethod = makeEqualsMethod(cNode)
            targetClass.addMethod(hashCodeMethod)
            targetClass.addMethod(equalsMethod)
        }
    }

    MethodNode makeHashCodeMethod(ClassNode source) {
        def className = source.name

        StringBuilder stringBuilder = new StringBuilder()
        idFields.each {
            stringBuilder.append(
                """
                result = 31 * result + ($it != null ? ${it}.hashCode() : 0);
                """
            )
        }

        def ast = new AstBuilder().buildFromString(INSTRUCTION_SELECTION, false, """
            package $source.packageName

            class $source.nameWithoutPackage {
                public int hashCode() {
                    int result = 0
                """
                +
                stringBuilder.toString()
                +
                """
                    return result
                }
            }
            """)

        ast[1].methods.find { it.name == 'hashCode' }
    }
    MethodNode makeEqualsMethod(ClassNode source) {
        def className = source.name

        StringBuilder stringBuilder = new StringBuilder("if (")
        idFields.each {
            stringBuilder.append(
                """
                ($it == null || that.$it == null) ||
                """
            )
        }
        stringBuilder.append("(0 == 1)) { if (")
        uniqueFields.each {
            stringBuilder.append(
                """
                ($it == null || that.$it == null) ||
                """)
        }
        stringBuilder.append("(0 == 1)) return false;")
        stringBuilder.append("if (")
        uniqueFields.each {
            stringBuilder.append(
                    """
                ($it != that.$it) ||
                """)
        }
        idFields.each {
            stringBuilder.append(
                    """
                ($it != that.$it) ||
                """)
        }
        stringBuilder.append("(0 == 1)) return false;")
        stringBuilder.append("} else if (")
        idFields.each {
            stringBuilder.append(
                """
                ($it != that.$it) ||
                """
            )
        }
        stringBuilder.append("(0 == 1)) return false;")

        def ast = new AstBuilder().buildFromString(INSTRUCTION_SELECTION, false, """
            package $source.packageName

            class $source.nameWithoutPackage {
                public boolean equals(o) {
                    if (this.is(o)) return true
                    if (getClass() != o.class) return false

                    $source.nameWithoutPackage that = ($source.nameWithoutPackage) o
                """
                +
                stringBuilder.toString()
                +
                """
                    return true
                }
            }
            """)

        ast[1].methods.find { it.name == 'equals' }
    }

}
