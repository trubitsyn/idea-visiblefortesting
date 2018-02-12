import com.google.common.annotations.VisibleForTesting

object Foo {

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