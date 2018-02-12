import android.support.annotation.VisibleForTesting

object Foo {

    @VisibleForTesting
    private fun annotatedFoo() {}

    fun publicFoo() {}

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun foo() {}

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    fun bar() {}

    @VisibleForTesting
    fun baz() {}
}