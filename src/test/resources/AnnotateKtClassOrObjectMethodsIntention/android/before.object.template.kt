import android.support.annotation.VisibleForTesting

object F<caret>oo {

    @VisibleForTesting
    private fun annotatedFoo() {}

    fun publicFoo() {}

    protected fun foo() {}

    internal fun bar() {}

    private fun baz() {}
}