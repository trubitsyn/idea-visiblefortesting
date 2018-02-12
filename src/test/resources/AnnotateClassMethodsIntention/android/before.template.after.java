import android.support.annotation.VisibleForTesting;

class Foo {

    @VisibleForTesting
    private void annotatedFoo() {}

    public void publicFoo() {}

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public void foo() {}

    @VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
    public void bar() {}

    @VisibleForTesting
    public void baz() {}
}