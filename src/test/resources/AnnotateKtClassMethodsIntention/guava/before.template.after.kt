import com.google.common.annotations.VisibleForTesting

class Foo {

    fun publicFoo() {}

    @VisibleForTesting
    fun foo() {}

    @VisibleForTesting
    fun bar() {}

    @VisibleForTesting
    fun baz() {}
}