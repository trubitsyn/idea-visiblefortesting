import com.google.common.annotations.VisibleForTesting

class Foo {

    @VisibleForTesting
    fun bar() {}

    private fun b<caret>az() {}
}
