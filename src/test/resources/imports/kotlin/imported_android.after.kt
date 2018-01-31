import android.support.annotation.VisibleForTesting

class Foo {

    @VisibleForTesting
    fun bar() {}

    @com.google.common.annotations.VisibleForTesting
    fun baz() {}
}
