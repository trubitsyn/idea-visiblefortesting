import com.google.common.annotations.VisibleForTesting;

class Foo {

    @VisibleForTesting
    private void annotatedFoo() {}

    public void publicFoo() {}

    @VisibleForTesting
    public void foo() {}

    @VisibleForTesting
    public void bar() {}

    @VisibleForTesting
    public void baz() {}
}