import com.google.common.annotations.VisibleForTesting;

class Foo {

    @VisibleForTesting
    public void bar() {}

    private void b<caret>az() {}
}
