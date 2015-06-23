package org.cambridge.eltpoc;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.cambridge.eltpoc.adapters.NavigationDrawerAdapter;
import org.cambridge.eltpoc.javascript.CLMSJavaScriptInterface;
import org.cambridge.eltpoc.model.CLMSModel;
import org.cambridge.eltpoc.observers.Observer;

public class MainActivity extends Activity implements Observer<CLMSModel> {
    private WebView webView;
    private static final String HOME_URL = "file:///android_asset/www/index.html";
    private static final String LEARNING_URL = "file:///android_asset/www/index.html#learning";
    private static final String TEACHING_URL = "file:///android_asset/www/index.html#teaching";

    private CLMSModel webModel = new CLMSModel();
    private String[] navigationArray = new String[4];
    private int[] navigationDrawables = new int[4];
    private NavigationDrawerAdapter navigationDrawerAdapter;
    private DrawerLayout drawerLayout;
    private ListView navigationList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView learningText, teachingText;
    private RelativeLayout learningLayout, teachingLayout;

    private static final int HOME = 1;
    private static final int LEARNING = 2;
    private static final int TEACHING = 3;
    private static final int SIGN_OUT = 4;

    private boolean isNavigationPressed = false;
    private int navigationPositionPressed;

    private View refreshIcon;
    private RotateAnimation rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDrawer();
        initializeWebView();
        initTabs();
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if (rotate.hasStarted())
                    rotate.cancel();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                rotateIcon();
            }
        });
    }

    /* Private methods */
    private void initializeWebView() {
        webView = (WebView) findViewById(R.id.webview);
        webModel.registerObserver(this);
        webView.addJavascriptInterface(new CLMSJavaScriptInterface(this, webModel), "JSInterface");
        webView.loadUrl(LEARNING_URL);
        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void initDrawer() {
        initNavigationArray();
        initNavigationDrawables();
        navigationDrawerAdapter = new NavigationDrawerAdapter(this, navigationArray, navigationDrawables);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationList = (ListView) findViewById(R.id.right_drawer);
        navigationList.setAdapter(navigationDrawerAdapter);
        navigationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
                isNavigationPressed = true;
                navigationPositionPressed = position;
            }
        });

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.home, R.string.home) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                if (isNavigationPressed) {
                    switch (navigationPositionPressed) {
                        case HOME:
                            break;
                        case LEARNING:
                            learningPressed(null);
                            break;
                        case TEACHING:
                            teachingPressed(null);
                            break;
                        case SIGN_OUT:
                            new CLMSJavaScriptInterface(MainActivity.this, webModel).signOutUser();
                            break;
                    }
                }
                isNavigationPressed = false;
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        getActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1d1d1d")));
        getActionBar().setTitle(R.string.learning_classes);

        View header = getLayoutInflater().inflate(R.layout.navigation_header, null);
        navigationList.addHeaderView(header);
    }

    private void initTabs() {
        learningLayout = (RelativeLayout) findViewById(R.id.learning_tab);
        learningText = (TextView) findViewById(R.id.learning_text);
        teachingLayout = (RelativeLayout) findViewById(R.id.teaching_tab);
        teachingText = (TextView) findViewById(R.id.teaching_text);

        learningLayout.setSelected(true);
        learningText.setSelected(true);
    }

    private void initNavigationArray() {
        navigationArray[0] = getResources().getString(R.string.home);
        navigationArray[1] = getResources().getString(R.string.learning);
        navigationArray[2] = getResources().getString(R.string.teaching);
        navigationArray[3] = getResources().getString(R.string.sign_out);
    }

    private void initNavigationDrawables() {
        navigationDrawables[0] = R.drawable.ic_home_white_36dp;
        navigationDrawables[1] = R.drawable.ic_school_white_36dp;
        navigationDrawables[2] = R.drawable.ic_account_balance_white_36dp;
        navigationDrawables[3] = R.drawable.ic_input_white_36dp;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack() && !webView.getUrl().equalsIgnoreCase(LEARNING_URL) &&
                            !webView.getUrl().equalsIgnoreCase(HOME_URL)) {
                        webView.goBack();
                        if (findViewById(R.id.video_player).getVisibility() == View.VISIBLE)
                            new CLMSJavaScriptInterface(this, webModel).hideVideo();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void update(CLMSModel model) {
        webView.loadUrl(LEARNING_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        refreshIcon = menu.findItem(R.id.action_sync).getActionView();
        initRotation();
        refreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshWebView();
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_menu:
                if (!drawerLayout.isDrawerOpen(Gravity.RIGHT))
                    drawerLayout.openDrawer(Gravity.RIGHT);
                else
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void learningPressed(View view) {
        learningLayout.setSelected(true);
        learningText.setSelected(true);
        teachingLayout.setSelected(false);
        teachingText.setSelected(false);
        webView.loadUrl(LEARNING_URL);
        getActionBar().setTitle(R.string.learning_classes);
    }

    public void teachingPressed(View view) {
        teachingLayout.setSelected(true);
        teachingText.setSelected(true);
        learningLayout.setSelected(false);
        learningText.setSelected(false);
        webView.loadUrl(TEACHING_URL);
        getActionBar().setTitle(R.string.teaching_classes);
    }

    private void refreshWebView() {
        webView.reload();
    }

    private void initRotation() {
        rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(2000);
        rotate.setRepeatCount(-1);
    }

    private void rotateIcon() {
        if (refreshIcon != null) {
            refreshIcon.findViewById(R.id.refresh_icon).setAnimation(rotate);
            refreshIcon.startAnimation(rotate);
        }
    }
}