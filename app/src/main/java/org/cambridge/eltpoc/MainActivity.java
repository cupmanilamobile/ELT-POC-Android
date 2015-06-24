package org.cambridge.eltpoc;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.cambridge.eltpoc.adapters.NavigationDrawerAdapter;
import org.cambridge.eltpoc.javascript.CLMSJavaScriptInterface;
import org.cambridge.eltpoc.model.CLMSModel;
import org.cambridge.eltpoc.model.CLMSWebModel;
import org.cambridge.eltpoc.observers.Observer;
import org.cambridge.eltpoc.util.Misc;

public class MainActivity extends AppCompatActivity implements Observer<CLMSModel> {
    private WebView webView;
    private static final String HOME_URL = "file:///android_asset/www/index.html";
    private static final String LEARNING_URL = "file:///android_asset/www/index.html#learning";
    private static final String TEACHING_URL = "file:///android_asset/www/index.html#teaching";
    private static final String ALL_CONTENT_URL = "file:///android_asset/www/index.html#all-content-a";
    private static final String DOWNLOADED_CONTENT_URL = "file:///android_asset/www/index.html#downloaded-a";
    private static final String LESSON_ALL_CONTENT_URL = "file:///android_asset/www/index.html#lesson-vocabulary";
    private static final String LESSON_DOWNLOADED_CONTENT_URL = "file:///android_asset/www/index.html#lesson-spelling";
    private static final String VIDEO_URL = "file:///android_asset/www/index.html#video";

    private static final int HOME_LEVEL = 0;
    private static final int CLASS_LEVEL = 1;
    private static final int LESSON_LEVEL = 2;
    private static final int VIDEO_LEVEL = 3;

    private int webLevel = HOME_LEVEL;
    private int prevWebLevel = webLevel;

    private CLMSModel webModel = new CLMSModel();
    private CLMSWebModel internetModel = ELTApplication.getInstance().getWebModel();

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
    private ImageView wifiIcon;
    private RotateAnimation rotate;

    private TextView toolbarTitle;
    private ImageView backArrow;
    private CLMSJavaScriptInterface javaScriptInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        javaScriptInterface = new CLMSJavaScriptInterface(MainActivity.this, webModel);

        initToolbar();
        initInternetConnectionStatus();
        initDrawer();
        initializeWebView();
        initTabs();

        addWebViewListener();
    }

    private void addWebViewListener() {
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if (url.equalsIgnoreCase(LEARNING_URL) || url.equalsIgnoreCase(TEACHING_URL))
                    updateArrowIcon(false);
                else
                    updateArrowIcon(true);
                updateWebLevel(url);
                updateTabTitle();
                updateTabs();
                prevWebLevel = webLevel;
                if (rotate.hasStarted())
                    rotate.cancel();
                if(webLevel == VIDEO_LEVEL)
                    javaScriptInterface.showVideo();
                else if(findViewById(R.id.video_player).getVisibility() == View.VISIBLE)
                    javaScriptInterface.hideVideo();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                rotateIcon();
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        backArrow = (ImageView) toolbar.findViewById(R.id.back_arrow);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void updateWebLevel(String url) {
        switch (url) {
            case HOME_URL:
            case TEACHING_URL:
            case LEARNING_URL:
                webLevel = HOME_LEVEL;
                break;
            case ALL_CONTENT_URL:
            case DOWNLOADED_CONTENT_URL:
                webLevel = CLASS_LEVEL;
                break;
            case LESSON_ALL_CONTENT_URL:
            case LESSON_DOWNLOADED_CONTENT_URL:
                webLevel = LESSON_LEVEL;
                break;
            case VIDEO_URL:
                webLevel = VIDEO_LEVEL;
                break;
            default:
                webLevel = HOME_LEVEL;
        }
    }

    private void updateTabTitle() {
        int title = 0;
        switch (webLevel) {
            case HOME_LEVEL:
                if (webView.getUrl().equalsIgnoreCase(LEARNING_URL))
                    title = R.string.learning;
                else if (webView.getUrl().equalsIgnoreCase(TEACHING_URL))
                    title = R.string.teaching;
                break;
            case CLASS_LEVEL:
                break;
            case LESSON_LEVEL:
                title = R.string.lesson_page;
                break;
            case VIDEO_LEVEL:
                title = R.string.video_page;
                break;
        }

        if (title == 0)
            toolbarTitle.setText("CLASS A");
        else
            toolbarTitle.setText(title);
    }

    private void initInternetConnectionStatus() {
        internetModel.registerObserver(this);
    }

    private void initializeWebView() {
        webView = (WebView) findViewById(R.id.webview);
        webModel.registerObserver(this);
        webView.addJavascriptInterface(javaScriptInterface, "JSInterface");
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
                            javaScriptInterface.signOutUser();
                            break;
                    }
                }
                isNavigationPressed = false;
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };

        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        toolbarTitle.setText(R.string.learning);
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
        webModel.removeObserver(this);
        internetModel.removeObserver(this);
    }

    @Override
    public void update(CLMSModel model) {
        if (model instanceof CLMSWebModel) {
            CLMSWebModel webModel = (CLMSWebModel) model;
            updateInternetConnectionIcon(webModel.isHasInternetConnection());
        } else
            webView.loadUrl(LEARNING_URL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        refreshIcon = menu.findItem(R.id.action_sync).getActionView().findViewById(R.id.refresh_icon);
        wifiIcon = (ImageView) menu.findItem(R.id.action_wifi).getActionView()
                .findViewById(R.id.wifi_icon);
        updateInternetConnectionIcon(Misc.checkInternetConnection(this));
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
        if (!learningLayout.isSelected()) {
            learningLayout.setSelected(true);
            learningText.setSelected(true);
            teachingLayout.setSelected(false);
            teachingText.setSelected(false);
            switch (webLevel) {
                case HOME_LEVEL:
//                    webView.loadUrl("javascript:updateLink('"+LEARNING_URL+"')");
                    webView.loadUrl(LEARNING_URL);
                    toolbarTitle.setText(R.string.learning);
                    break;
                case CLASS_LEVEL:
                    webView.loadUrl(DOWNLOADED_CONTENT_URL);
                    break;
                case LESSON_LEVEL:
                    webView.loadUrl(LESSON_DOWNLOADED_CONTENT_URL);
                    break;
            }
        }
    }

    public void teachingPressed(View view) {
        if (!teachingLayout.isSelected()) {
            teachingLayout.setSelected(true);
            teachingText.setSelected(true);
            learningLayout.setSelected(false);
            learningText.setSelected(false);
            switch (webLevel) {
                case HOME_LEVEL:
                    webView.loadUrl(TEACHING_URL);
                    toolbarTitle.setText(R.string.teaching);
                    break;
                case CLASS_LEVEL:
                    webView.loadUrl(ALL_CONTENT_URL);
                    break;
                case LESSON_LEVEL:
                    webView.loadUrl(LESSON_ALL_CONTENT_URL);
                    break;
            }
        }
    }

    private void resetTabs() {
        learningLayout.setSelected(false);
        learningText.setSelected(false);
        teachingLayout.setSelected(false);
        teachingText.setSelected(false);
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

    private void updateInternetConnectionIcon(boolean hasInternet) {
        wifiIcon.setSelected(hasInternet);
    }

    private void updateTabs() {
        findViewById(R.id.tab_layout).setVisibility(View.VISIBLE);
        if (webLevel == CLASS_LEVEL || webLevel == LESSON_LEVEL) {
            learningText.setText(R.string.all_content);
            teachingText.setText(R.string.downloaded_content);
        } else if (webLevel == HOME_LEVEL) {
            learningText.setText(R.string.my_learning);
            teachingText.setText(R.string.my_teaching);
        } else if (webLevel == VIDEO_LEVEL)
            findViewById(R.id.tab_layout).setVisibility(View.GONE);
    }

    private void updateArrowIcon(boolean show) {
        backArrow.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void goBack(View View) {
        webView.goBack();
    }
}