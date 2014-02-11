package groovy.transformation

import groovy.annotation.EntityEqualsAndHashCode

@EntityEqualsAndHashCode(idFields = ['id1', 'id2'], uniqueFields = ['un1','un2'])
class ClassWithEntityEqualsAndHashCode {

    Integer id1
    String id2

    Integer un1
    String un2

    void test() {
        println "teste"
    }

}
