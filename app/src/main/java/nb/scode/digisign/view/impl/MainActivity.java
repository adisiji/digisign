package nb.scode.digisign.view.impl;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerMainViewComponent;
import nb.scode.digisign.injection.MainViewModule;
import nb.scode.digisign.presenter.MainPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.MainView;

public final class MainActivity extends BaseActivity<MainPresenter, MainView>
    implements MainView, NavigationView.OnNavigationItemSelectedListener {

  @Inject PresenterFactory<MainPresenter> mPresenterFactory;
  @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
  @BindView(R.id.toolbar) Toolbar toolbar;
  @BindView(R.id.nvView) NavigationView nvDrawer;
  private ActionBarDrawerToggle drawerToggle;

  // Your presenter is available using the mPresenter variable

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    // Do not call mPresenter from here, it will be null! Wait for onStart or onPostCreate.
    setSupportActionBar(toolbar);
    nvDrawer.setNavigationItemSelectedListener(this);
    //set first item checked an showed
    if (savedInstanceState == null) {
      onNavigationItemSelected(nvDrawer.getMenu().getItem(0));
    }

    drawerToggle = setupDrawerToggle();
    // Tie DrawerLayout events to the ActionBarToggle
    drawerLayout.addDrawerListener(drawerToggle);
  }

  @Override protected void setupComponent(@NonNull AppComponent parentComponent) {
    DaggerMainViewComponent.builder()
        .appComponent(parentComponent)
        .mainViewModule(new MainViewModule())
        .build()
        .inject(this);
  }

  @NonNull @Override protected PresenterFactory<MainPresenter> getPresenterFactory() {
    return mPresenterFactory;
  }

  @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    selectDrawerItem(item);
    return true;
  }

  public void selectDrawerItem(MenuItem menuItem) {
    // Create a new fragment and specify the fragment to show based on nav item clicked
    Fragment fragment = null;
    String[] fragmentTag = new String[] { "home_tag", "alldoc_tag", "sentfiles_tag" };
    int pos;
    Class fragmentClass;
    switch (menuItem.getItemId()) {
      case R.id.nav_first_fragment:
        fragmentClass = HomeFragment.class;
        pos = 0;
        break;
      case R.id.nav_second_fragment:
        fragmentClass = AllDocFragment.class;
        pos = 1;
        break;
      case R.id.nav_third_fragment:
        fragmentClass = SentFilesFragment.class;
        pos = 2;
        break;
      default:
        fragmentClass = HomeFragment.class;
        pos = 0;
        break;
    }

    try {
      fragment = (Fragment) fragmentClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }

    // Insert the fragment by replacing any existing fragment
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction().replace(R.id.flContent, fragment, fragmentTag[pos]).commit();

    // Highlight the selected item has been done by NavigationView
    menuItem.setChecked(true);
    // Set action bar title
    setTitle(menuItem.getTitle());
    // Close the navigation drawer
    drawerLayout.closeDrawers();
  }

  private ActionBarDrawerToggle setupDrawerToggle() {
    // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
    // and will not render the hamburger icon without it.
    return new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open,
        R.string.drawer_close);
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    drawerToggle.syncState();
  }

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    // Pass any configuration change to the drawer toggles
    drawerToggle.onConfigurationChanged(newConfig);
  }
}
