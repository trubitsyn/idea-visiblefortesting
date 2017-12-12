import android.support.annotation.VisibleForTesting

class Foo {

    @VisibleForTesting
    fun bar() {}

    private fun b<caret>az() {}
}
