import com.google.common.annotations.VisibleForTesting

class F<caret>oo {

    @VisibleForTesting
    private fun annotatedFoo() {}

    fun publicFoo() {}

    protected fun foo() {}

    internal fun bar() {}

    private fun baz() {}
}