import android.support.annotation.VisibleForTesting;

class Foo {

    @VisibleForTesting
    public void bar() {}

    private void b<caret>az() {}
}
