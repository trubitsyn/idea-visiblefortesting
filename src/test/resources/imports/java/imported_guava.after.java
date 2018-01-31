import com.google.common.annotations.VisibleForTesting;

class Foo {

    @VisibleForTesting
    public void bar() {}

    @android.support.annotation.VisibleForTesting
    public void baz() {}
}
