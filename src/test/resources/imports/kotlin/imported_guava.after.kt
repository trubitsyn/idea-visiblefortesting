import com.google.common.annotations.VisibleForTesting

class Foo {

    @VisibleForTesting
    fun bar() {}

    @android.support.annotation.VisibleForTesting
    fun baz() {}
}
