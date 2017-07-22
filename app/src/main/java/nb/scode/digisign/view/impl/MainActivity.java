package nb.scode.digisign.view.impl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import javax.inject.Inject;
import nb.scode.digisign.R;
import nb.scode.digisign.data.remote.BusModel.SignOutEvent;
import nb.scode.digisign.data.remote.BusModel.UserBusPost;
import nb.scode.digisign.data.service.TokenPost;
import nb.scode.digisign.injection.AppComponent;
import nb.scode.digisign.injection.DaggerMainViewComponent;
import nb.scode.digisign.injection.MainViewModule;
import nb.scode.digisign.presenter.MainPresenter;
import nb.scode.digisign.presenter.loader.PresenterFactory;
import nb.scode.digisign.view.MainView;
import nb.scode.digisign.view.busmodel.SpinnerMenu;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import timber.log.Timber;

import static nb.scode.digisign.view.impl.Constants.MENU_ALL;
import static nb.scode.digisign.view.impl.Constants.MENU_RECEIVE;
import static nb.scode.digisign.view.impl.Constants.MENU_SENT;

public final class MainActivity extends BaseActivity<MainPresenter, MainView>
    implements MainView, NavigationView.OnNavigationItemSelectedListener {

  @Inject PresenterFactory<MainPresenter> mPresenterFactory;
  @Nullable @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
  @Nullable @BindView(R.id.toolbar) Toolbar toolbar;
  @Nullable @BindView(R.id.nvView) NavigationView nvDrawer;
  @Nullable private ActionBarDrawerToggle drawerToggle;
  private ProgressDialog progressDialog;
  @NonNull private Boolean isFromAllDoc = false;
  private View view;

  // Your presenter is available using the mPresenter variable

  @Subscribe(threadMode = ThreadMode.MAIN) public void onUserBusEvent(
      @NonNull UserBusPost userBusPost) {
    setHeaderProfile(userBusPost);
  }

  @Subscribe(threadMode = ThreadMode.MAIN) public void onLogoutEvent(SignOutEvent event) {
    gotoLogin();
  }

  @Subscribe(threadMode = ThreadMode.BACKGROUND) public void onGetToken(@NonNull TokenPost post) {
    mPresenter.sendTokenToServer(post.getToken());
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
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
    setupProgress();
    setupSpinner();
  }

  private void setupSpinner() {
    SpinnerAdapter spinnerAdapter =
        ArrayAdapter.createFromResource(getApplicationContext(), R.array.category,
            R.layout.spinner_dropdown_item);
    LayoutInflater inflater = LayoutInflater.from(this);
    view = inflater.inflate(R.layout.toolbar_all_doc, null);
    Spinner navigationSpinner = (Spinner) view.findViewById(R.id.spinner);
    navigationSpinner.setAdapter(spinnerAdapter);
    navigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Timber.d("onItemSelected(): i => " + i);
        switch (i) {
          case 0: { // All Doc
            EventBus.getDefault().post(new SpinnerMenu(MENU_ALL));
            break;
          }
          case 1: {
            EventBus.getDefault().post(new SpinnerMenu(MENU_SENT));
            break;
          }
          case 2: {
            EventBus.getDefault().post(new SpinnerMenu(MENU_RECEIVE));
            break;
          }
        }
      }

      @Override public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
  }

  private void setupProgress() {
    progressDialog = new ProgressDialog(this);
    progressDialog.setIndeterminate(true);
    progressDialog.setCancelable(false);
  }

  @Override protected void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
    mPresenter.getPhotoUri();
  }

  @Override protected void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
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

  public void selectDrawerItem(@NonNull MenuItem menuItem) {
    // Create a new fragment and specify the fragment to show based on nav item clicked
    Fragment fragment = null;
    String[] fragmentTag = new String[] { "home_tag", "alldoc_tag", "sentfiles_tag" };
    int pos;
    Class fragmentClass;
    switch (menuItem.getItemId()) {
      case R.id.nav_first_fragment:
        fragmentClass = HomeFragment.class;
        if (isFromAllDoc) {
          toolbar.removeView(view);
        }
        pos = 0;
        break;
      case R.id.nav_second_fragment:
        fragmentClass = AllDocFragment.class;
        toolbar.addView(view);
        isFromAllDoc = true;
        pos = 1;
        break;
      case R.id.nav_third_fragment:
        fragmentClass = SettingsFragment.class;
        if (isFromAllDoc) {
          toolbar.removeView(view);
        }
        pos = 2;
        break;
      default:
        fragmentClass = HomeFragment.class;
        pos = 0;
        break;
    }

    try {
      fragment = (Fragment) fragmentClass.newInstance();
    } catch (@NonNull InstantiationException | IllegalAccessException e) {
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

  @Nullable private ActionBarDrawerToggle setupDrawerToggle() {
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

  private void setHeaderProfile(@NonNull UserBusPost busPost) {
    View view = nvDrawer.getHeaderView(0);
    CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.civ_profile_header);
    Uri uri = busPost.getUri();
    if (uri != null) {
      Glide.with(this).load(uri).asBitmap().into(circleImageView);
    } else {
      circleImageView.setImageDrawable(
          ContextCompat.getDrawable(this, R.drawable.ic_account_circle_lime_700_36dp));
    }

    TextView tvEmail = (TextView) view.findViewById(R.id.tv_email_header);
    TextView tvName = (TextView) view.findViewById(R.id.tv_display_name);

    if (busPost.getDisplayName() != null) {
      tvEmail.setText(busPost.getEmail());
      tvName.setText(busPost.getDisplayName());
    } else {
      tvName.setText(busPost.getEmail());
    }
  }

  @OnClick(R.id.btn_logout) void logout() {
    mPresenter.logout();
  }

  @Override public void gotoLogin() {
    Intent i = new Intent(this, LoginActivity.class);
    startActivity(i);
    finish();
  }

  @Override public void showProgressDialog(String message) {
    if (progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
    Timber.d("showProgressDialog(): " + message);
    progressDialog.setMessage(message);
    progressDialog.show();
  }

  @Override public void hideProgressDialog() {
    progressDialog.dismiss();
  }

  @Override public void showToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
}
