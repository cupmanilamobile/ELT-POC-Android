package org.cambridge.eltpoc;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.cambridge.eltpoc.adapters.NavigationDrawerAdapter;
import org.cambridge.eltpoc.javascript.CLMSJavaScriptInterface;
import org.cambridge.eltpoc.model.CLMSLinkModel;
import org.cambridge.eltpoc.model.CLMSModel;
import org.cambridge.eltpoc.model.CLMSWebModel;
import org.cambridge.eltpoc.observers.CLMSClassListObserver;
import org.cambridge.eltpoc.observers.CLMSContentScoreListObserver;
import org.cambridge.eltpoc.observers.Observer;
import org.cambridge.eltpoc.util.DialogUtils;
import org.cambridge.eltpoc.util.Misc;
import org.cambridge.eltpoc.util.UIUtils;
import org.cambridge.eltpoc.util.WebContentHelper;
import org.cambridge.eltpoc.util.WebServiceHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Observer<CLMSModel> {
    private WebView webView;
    private int webLevel = Constants.HOME_LEVEL;
    private CLMSModel webModel = new CLMSModel();

    private NavigationDrawerAdapter navigationDrawerAdapter;
    private DrawerLayout drawerLayout;
    private ListView navigationList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView learningText, teachingText;
    private FrameLayout learningLayout, teachingLayout;
    private View syncButton;
    private ImageView wifiIcon;
    private ProgressBar progressBar;
    private View loadingLayout;
    private TextView toolbarTitle;
    private ImageView backArrow;

    private boolean isNavigationPressed = false;
    private int navigationPositionPressed;
    private boolean isTouchEnabled = true;
    private boolean isTabPressed = false;
    private boolean isWebLoaded = false;
    private boolean isContentLoaded = false;

    private CLMSJavaScriptInterface javaScriptInterface;
    private ELTApplication instance = ELTApplication.getInstance();
    private CLMSWebModel internetModel = instance.getWebModel();
    private CLMSContentScoreListObserver contentScoreListObserver = instance
            .getContentScoreListObserver();
    private String customContentUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentScoreListObserver.clearRetrieval();
        javaScriptInterface = new CLMSJavaScriptInterface(MainActivity.this, webModel);
        initViews();
        addWebViewListener();
        findViewById(R.id.tab_layout).setVisibility(View.GONE);
        showLoadingScreen(true);
    }

    private void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.progress);
        loadingLayout = findViewById(R.id.loading_layout);
        initToolbar();
        initObservers();
        initDrawer();
        initWebView();
        initTabs();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        backArrow = (ImageView) toolbar.findViewById(R.id.back_arrow);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initObservers() {
        internetModel.registerObserver(this);
        instance.getClassListObserver().registerObserver(this);
        instance.getClassListObserver().setIsClassesRetrieved(false);
        instance.getClassListObserver().setIsCoursesRetrieved(false);
        instance.getLinkModel().registerObserver(this);
        contentScoreListObserver.registerObserver(this);
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.webview);
        webModel.registerObserver(this);
        webView.addJavascriptInterface(javaScriptInterface, Constants.JS_INTERFACE);
        webView.loadUrl(Constants.LEARNING_URL);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    private void initDrawer() {
        navigationDrawerAdapter = new NavigationDrawerAdapter(this, UIUtils.getNavigationArray(this),
                UIUtils.getNavigationDrawables());
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationList = (ListView) findViewById(R.id.right_drawer);
        final View header = getLayoutInflater().inflate(R.layout.navigation_header, null);
        navigationList.addHeaderView(header);
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
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
                if (isNavigationPressed) {
                    switch (navigationPositionPressed) {
                        case Constants.LEARNING:
                            webLevel = Constants.HOME_LEVEL;
                            learningPressed(null);
                            break;
                        case Constants.TEACHING:
                            webLevel = Constants.HOME_LEVEL;
                            if (navigationDrawerAdapter.isRemoved())
                                WebServiceHelper.signOutUser(MainActivity.this);
                            else
                                teachingPressed(null);
                            break;
                        case Constants.SIGN_OUT:
                            WebServiceHelper.signOutUser(MainActivity.this);
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
        toolbarTitle.setText(R.string.app_name);
    }

    private void initTabs() {
        learningLayout = (FrameLayout) findViewById(R.id.learning_tab);
        learningText = (TextView) findViewById(R.id.learning_text);
        teachingLayout = (FrameLayout) findViewById(R.id.teaching_tab);
        teachingText = (TextView) findViewById(R.id.teaching_text);
        learningLayout.setSelected(true);
        learningText.setSelected(true);
    }

    private void removeObservers() {
        webModel.removeObserver(this);
        internetModel.removeObserver(this);
        instance.getClassListObserver().removeObserver(this);
        instance.getLinkModel().removeObserver(this);
        contentScoreListObserver.removeObserver(this);
    }

    public void learningPressed(View view) {
        if (!learningLayout.isSelected() || (webLevel == Constants.HOME_LEVEL &&
                (webView.getUrl().equalsIgnoreCase(Constants.LESSON_ALL_CONTENT_URL) ||
                        webView.getUrl().equalsIgnoreCase(Constants.LESSON_DOWNLOADED_URL)))) {
            updateTabSelection(true);
            switch (webLevel) {
                case Constants.HOME_LEVEL:
                    webView.loadUrl(Constants.LEARNING_URL);
                    toolbarTitle.setText(R.string.app_name);
                    break;
                case Constants.UNIT_LEVEL:
                    isTabPressed = true;
                    webView.loadUrl(Constants.LESSON_ALL_CONTENT_URL);
                    break;
            }
        }
    }

    public void teachingPressed(View view) {
        if (!teachingLayout.isSelected()) {
            updateTabSelection(false);
            switch (webLevel) {
                case Constants.HOME_LEVEL:
                    webView.loadUrl(Constants.TEACHING_URL);
                    toolbarTitle.setText(R.string.app_name);
                    break;
                case Constants.UNIT_LEVEL:
                    isTabPressed = true;
                    webView.loadUrl(Constants.LESSON_DOWNLOADED_URL);
                    break;
            }
        }
    }

    public void updateTabSelection(boolean isLearning) {
        learningLayout.setSelected(isLearning);
        learningText.setSelected(isLearning);
        teachingLayout.setSelected(!isLearning);
        teachingText.setSelected(!isLearning);
        UIUtils.updateViewHeight(isLearning ? findViewById(R.id.learning_inner) :
                        findViewById(R.id.teaching_inner),
                getResources().getDimensionPixelSize(R.dimen.tab_pressed_height));
        UIUtils.updateViewHeight(isLearning ? findViewById(R.id.teaching_inner) :
                        findViewById(R.id.learning_inner),
                getResources().getDimensionPixelSize(R.dimen.tab_height));
    }

    private void updateInternetConnectionIcon(boolean hasInternet) {
        if (wifiIcon != null)
            wifiIcon.setSelected(hasInternet);
    }

    private void updateTabs() {
        findViewById(R.id.tab_layout).setVisibility(View.VISIBLE);
        if (webLevel == Constants.UNIT_LEVEL) {
            learningText.setText(R.string.all_content);
            teachingText.setText(R.string.downloaded_content);
        } else if (webLevel == Constants.HOME_LEVEL) {
            learningText.setText(R.string.my_learning);
            teachingText.setText(R.string.my_teaching);
        } else if (webLevel == Constants.CONTENT_LEVEL)
            findViewById(R.id.tab_layout).setVisibility(View.GONE);
    }

    private void updateBackArrowIcon(boolean show) {
        findViewById(R.id.toolbar_back).setEnabled(show);
        backArrow.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void goBack(View view) {
        if (webLevel >= Constants.CONTENT_LEVEL) {
            if(view != null || webLevel == Constants.CONTENT_LEVEL) {
                addWebViewListener();
                webLevel = Constants.CONTENT_LEVEL;
            }
            else
                webView.goBack();
            webLevel--;
        }
        else
            webLevel--;
        switch (webLevel) {
            case Constants.HOME_LEVEL:
                webView.loadUrl(Constants.LEARNING_URL);
                break;
            case Constants.UNIT_LEVEL:
                isTabPressed = true;
                webView.loadUrl(Constants.LESSON_ALL_CONTENT_URL);
                break;
        }
    }

    private void updateTabTitle() {
        String title;
        switch (webLevel) {
            case Constants.UNIT_LEVEL:
                title = instance.getLinkModel().getClassName();
                break;
            case Constants.CONTENT_LEVEL:
                title = instance.getLinkModel().getContentName();
                break;
            case Constants.HOME_LEVEL:
            default:
                title = getString(R.string.app_name);
                break;
        }
        toolbarTitle.setText(title);
    }

    private void addWebViewListener() {
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if (url.equalsIgnoreCase(Constants.LEARNING_URL) ||
                        url.equalsIgnoreCase(Constants.TEACHING_URL))
                    updateBackArrowIcon(false);
                else
                    updateBackArrowIcon(true);
                webLevel = WebContentHelper.updateWebLevel(url);
                updateTabTitle();
                updateTabs();
                loadTeachingLearningURL(url);
                if (url.equalsIgnoreCase(Constants.LEARNING_URL) ||
                        url.equalsIgnoreCase(Constants.LESSON_ALL_CONTENT_URL))
                    updateTabSelection(true);
                else
                    updateTabSelection(false);
                if (isTabPressed) {
                    isTabPressed = false;
                    loadLessonURL(url);
                }
                if (url.equalsIgnoreCase(Constants.LESSON_ALL_CONTENT_URL) ||
                        url.equalsIgnoreCase(Constants.LESSON_DOWNLOADED_URL))
                    isWebLoaded = true;
                else
                    isWebLoaded = false;
                if (isContentLoaded) {
                    loadLessonURL(url);
                    isContentLoaded = false;
                    isWebLoaded = false;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                resizeWebView(false);
            }
        });
    }

    private void addContentWebViewListener() {
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        customContentUrl = "";
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                showLoadingScreen(false);
                if(customContentUrl.equalsIgnoreCase(""))
                    customContentUrl = url;
                if(!customContentUrl.equalsIgnoreCase(url))
                    ++webLevel;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                resizeWebView(true);
                showLoadingScreen(true);
            }
        });
    }

    private void resizeWebView(boolean isContent) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, isContent ?
                getResources().getDisplayMetrics().heightPixels - 100: RelativeLayout.LayoutParams.WRAP_CONTENT);
        webView.setLayoutParams(params);
    }

    private void loadTeachingLearningURL(String url) {
        if (url.equalsIgnoreCase(Constants.TEACHING_URL) ||
                url.equalsIgnoreCase(Constants.LEARNING_URL)) {
            ArrayList<Integer> contentCount = WebContentHelper.updateCourseContent(this, webView,
                    url.equalsIgnoreCase(Constants.LEARNING_URL), Misc.hasInternetConnection(this));
            WebContentHelper.updateTabVisibility(contentCount.get(0),
                    contentCount.get(1), false, learningLayout, teachingLayout, webView,
                    navigationDrawerAdapter);
        }
    }

    private void loadLessonURL(String url) {
        if (url.equalsIgnoreCase(Constants.LESSON_ALL_CONTENT_URL) ||
                url.equalsIgnoreCase(Constants.LESSON_DOWNLOADED_URL)) {
            WebContentHelper.updateTabVisibility(Misc.hasInternetConnection(MainActivity.this),
                    learningLayout, teachingLayout);
            WebContentHelper.updateUnitContent(MainActivity.this, webView,
                    contentScoreListObserver.getCourseId(), contentScoreListObserver.getClassId(),
                    url.equalsIgnoreCase(Constants.LESSON_DOWNLOADED_URL));
        }
    }

    private void showLoadingScreen(boolean isLoading) {
        findViewById(R.id.learning_inner).setEnabled(!isLoading);
        findViewById(R.id.teaching_inner).setEnabled(!isLoading);
        isTouchEnabled = !isLoading;
        loadingLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return !isTouchEnabled;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT))
            drawerLayout.closeDrawer(Gravity.RIGHT);
        else {
            if (!webView.getUrl().equalsIgnoreCase(Constants.LEARNING_URL) &&
                    !webView.getUrl().equalsIgnoreCase(Constants.TEACHING_URL))
                goBack(null);
            else
                super.onBackPressed();
        }
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
        removeObservers();
    }

    @Override
    public void update(final CLMSModel model) {
        if (model instanceof CLMSWebModel) {
            CLMSWebModel webModel = (CLMSWebModel) model;
            updateInternetConnectionIcon(webModel.isHasInternetConnection());
            invalidateOptionsMenu();
            if (webLevel == Constants.UNIT_LEVEL) {
                isTabPressed = true;
                if (Misc.hasInternetConnection(this))
                    webView.loadUrl(Constants.LESSON_ALL_CONTENT_URL);
                else
                    webView.loadUrl(Constants.LESSON_DOWNLOADED_URL);
            } else if (webLevel == Constants.HOME_LEVEL)
                webView.reload();
            if (webModel.isSynced()) {
                if (syncButton.findViewById(R.id.refresh_icon).getAnimation() != null)
                    syncButton.findViewById(R.id.refresh_icon).getAnimation().cancel();
                DialogUtils.createDialog(this, "SYNC", webModel.getSyncMessage());
                this.internetModel.setIsSynced(false);
            }
        } else if (model instanceof CLMSClassListObserver) {
            CLMSClassListObserver classListObserver = (CLMSClassListObserver) model;
            if (classListObserver.isClassesRetrieved() && classListObserver.isCoursesRetrieved()) {
                ArrayList<Integer> contentCount = WebContentHelper.updateCourseContent(this,
                        webView, true, Misc.hasInternetConnection(this));
                WebContentHelper.updateTabVisibility(contentCount.get(0),
                        contentCount.get(1), false, learningLayout, teachingLayout, webView,
                        navigationDrawerAdapter);
            }
        } else if (model instanceof CLMSContentScoreListObserver) {
            contentScoreListObserver.clearRetrieval();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isContentLoaded = true;
                    if (isWebLoaded) {
                        loadLessonURL(((CLMSContentScoreListObserver) model).getUrl());
                        isContentLoaded = false;
                        isWebLoaded = false;
                    }
                }
            });
        } else if (model instanceof CLMSLinkModel) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addContentWebViewListener();
                    findViewById(R.id.tab_layout).setVisibility(View.GONE);
                    CLMSLinkModel linkModel = (CLMSLinkModel) model;
                    toolbarTitle.setText(linkModel.getContentName());
                    webLevel = Constants.CONTENT_LEVEL;
                    webView.loadUrl(linkModel.getWebLink());
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (model.getWebOperation()) {
                        case DOWNLOADED:
                        case DELETED:
                            WebContentHelper.refreshContents(MainActivity.this, webView,
                                    model.getContentScore(), model.getCourseId());
                            break;
                        case FAILED:
                            DialogUtils.createDialog(MainActivity.this, "ERROR", "Download has " +
                                    "failed.\nPlease Check Internet Connection");
                            break;
                        case REFRESHED:
                            webView.reload();
                            break;
                        case LOADING:
                            showLoadingScreen(true);
                            break;
                        case NONE:
                            findViewById(R.id.tab_layout).setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showLoadingScreen(false);
                                }
                            }, 1000);
                            break;
                    }
                    webModel.setWebOperation(CLMSModel.WEB_OPERATION.NONE);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        syncButton = menu.findItem(R.id.action_sync).getActionView().findViewById(R.id.refresh_icon);
        wifiIcon = (ImageView) menu.findItem(R.id.action_wifi).getActionView()
                .findViewById(R.id.wifi_icon);
        updateInternetConnectionIcon(Misc.hasInternetConnection(this));
        menu.findItem(R.id.action_sync).setVisible(Misc.hasInternetConnection(this));
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTouchEnabled) {
                    UIUtils.rotateView(syncButton.findViewById(R.id.refresh_icon));
                    WebServiceHelper.updateContentScores(MainActivity.this, javaScriptInterface);
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        switch (item.getItemId()) {
            case R.id.action_menu:
                if (isTouchEnabled) {
                    if (!drawerLayout.isDrawerOpen(Gravity.RIGHT))
                        drawerLayout.openDrawer(Gravity.RIGHT);
                    else
                        drawerLayout.closeDrawer(Gravity.RIGHT);
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}