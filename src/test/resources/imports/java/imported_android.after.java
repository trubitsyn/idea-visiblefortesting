import android.support.annotation.VisibleForTesting;

class Foo {

    @VisibleForTesting
    public void bar() {}

    @com.google.common.annotations.VisibleForTesting
    public void baz() {}
}
