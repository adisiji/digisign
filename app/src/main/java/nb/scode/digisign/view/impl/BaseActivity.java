package nb.scode.digisign.view.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import java.util.concurrent.atomic.AtomicBoolean;
import nb.scode.digisign.App;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.presenter.BasePresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.presenter.loader.PresenterLoader;
import timber.log.Timber;

public abstract class BaseActivity<P extends BasePresenter<V>, V> extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<P> {
  /**
   * Do we need to call {@link #doStart()} from the {@link #onLoadFinished(Loader, BasePresenter)}
   * method.
   * Will be true if presenter wasn't loaded when {@link #onStart()} is reached
   */
  private final AtomicBoolean mNeedToCallStart = new AtomicBoolean(false);
  /**
   * The presenter for this view
   */
  @Nullable protected P mPresenter;
  /**
   * Is this the first start of the activity (after onCreate)
   */
  private boolean mFirstStart;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mFirstStart = true;

    injectDependencies();

    getSupportLoaderManager().initLoader(0, null, this).startLoading();
  }

  private void injectDependencies() {
    setupComponent(((App) getApplication()).getAppComponent());
  }

  @Override protected void onStart() {
    super.onStart();

    if (mPresenter == null) {
      mNeedToCallStart.set(true);
    } else {
      doStart();
    }
  }

  /**
   * Call the presenter callbacks for onStart
   */
  @SuppressWarnings("unchecked") private void doStart() {
    assert mPresenter != null;

    mPresenter.onViewAttached((V) this);

    mPresenter.onStart(mFirstStart);

    mFirstStart = false;
  }

  @Override protected void onStop() {
    if (mPresenter != null) {
      mPresenter.onStop();

      mPresenter.onViewDetached();
    }

    super.onStop();
  }

  @NonNull @Override public final Loader<P> onCreateLoader(int id, Bundle args) {
    return new PresenterLoader<>(this, getPresenterFactory());
  }

  @Override public final void onLoadFinished(Loader<P> loader, P presenter) {
    mPresenter = presenter;

    if (mNeedToCallStart.compareAndSet(true, false)) {
      doStart();
    }
  }

  @Override public final void onLoaderReset(Loader<P> loader) {
    mPresenter = null;
  }

  /**
   * Get the presenter factory implementation for this view
   *
   * @return the presenter factory
   */
  @NonNull protected abstract PresenterFactory<P> getPresenterFactory();

  /**
   * Setup the injection component for this view
   *
   * @param appComponent the app component
   */
  protected abstract void setupComponent(@NonNull AppComponent appComponent);

  /**
   * Change title and home up icon
   *
   * @param title the page title
   */
  protected void setupToolbar(String title) {
    setTitle(title);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_chevron_left_white_24dp);
    } else {
      Timber.e("setupToolbar(): Null Toolbar ! Can't change icon");
    }
  }
}
