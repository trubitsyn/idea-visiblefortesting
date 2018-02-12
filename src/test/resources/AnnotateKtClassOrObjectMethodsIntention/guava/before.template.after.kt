import com.google.common.annotations.VisibleForTesting

class Foo {

    @VisibleForTesting
    private fun annotatedFoo() {}

    fun publicFoo() {}

    @VisibleForTesting
    fun foo() {}

    @VisibleForTesting
    fun bar() {}

    @VisibleForTesting
    fun baz() {}
}