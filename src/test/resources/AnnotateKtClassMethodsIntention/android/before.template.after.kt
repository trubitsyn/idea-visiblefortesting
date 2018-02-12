import android.support.annotation.VisibleForTesting

class Foo {

    @VisibleForTesting
    fun annotatedFoo() {}

    fun publicFoo() {}

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    fun foo() {}

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    fun bar() {}

    @VisibleForTesting
    fun baz() {}
}