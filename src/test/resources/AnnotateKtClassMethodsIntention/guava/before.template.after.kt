import com.google.common.annotations.VisibleForTesting

class Foo {

    @VisibleForTesting
    fun annotatedFoo() {}

    fun publicFoo() {}

    @VisibleForTesting
    fun foo() {}

    @VisibleForTesting
    fun bar() {}

    @VisibleForTesting
    fun baz() {}
}