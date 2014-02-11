package groovy.annotation.transformation

import groovy.transformation.ClassWithEntityEqualsAndHashCode


class EntityEqualsAndHashCodeTest extends GroovyTestCase {

    void testClass() {
        ClassWithEntityEqualsAndHashCode obj1 = new ClassWithEntityEqualsAndHashCode(id1: 1, id2: "id")
        ClassWithEntityEqualsAndHashCode obj2 = new ClassWithEntityEqualsAndHashCode(id1: 1, id2: "id")
        ClassWithEntityEqualsAndHashCode obj3 = new ClassWithEntityEqualsAndHashCode(id1: 1, id2: "id", un1: 2, un2: "id")
        ClassWithEntityEqualsAndHashCode obj4 = new ClassWithEntityEqualsAndHashCode(un1: 2, un2: "id")
        ClassWithEntityEqualsAndHashCode obj5 = new ClassWithEntityEqualsAndHashCode(un1: 2, un2: "id")
        assert obj1.equals(obj2)
        assert obj1.equals(obj3)
        assert !obj3.equals(obj4)
        assert obj4.equals(obj5)

        Set set = []
        set += obj1
        set += obj2
        assert set.size() == 1
        set += obj3
        assert set.size() == 1
        set += obj4
        assert set.size() == 2
        set += obj5
        assert set.size() == 2
    }

}
