package org.cambridge.eltpoc;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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
import org.cambridge.eltpoc.model.CLMSUser;
import org.cambridge.eltpoc.model.CLMSWebModel;
import org.cambridge.eltpoc.observers.CLMSClassListObserver;
import org.cambridge.eltpoc.observers.CLMSContentScoreListObserver;
import org.cambridge.eltpoc.observers.Observer;
import org.cambridge.eltpoc.util.DialogUtils;
import org.cambridge.eltpoc.util.Misc;
import org.cambridge.eltpoc.util.SharedPreferencesUtils;
import org.cambridge.eltpoc.util.UIUtils;
import org.cambridge.eltpoc.util.WebContentHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Observer<CLMSModel> {
    private WebView webView;
    private int webLevel = Constants.HOME_LEVEL;

    private CLMSModel webModel = new CLMSModel();
    private CLMSWebModel internetModel = ELTApplication.getInstance().getWebModel();

    private NavigationDrawerAdapter navigationDrawerAdapter;
    private DrawerLayout drawerLayout;
    private ListView navigationList;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private TextView learningText, teachingText;
    private FrameLayout learningLayout, teachingLayout;

    private View refreshIcon;
    private ImageView wifiIcon;
    private RotateAnimation rotate;

    private boolean isNavigationPressed = false;
    private int navigationPositionPressed;

    private TextView toolbarTitle;
    private ImageView backArrow;
    private CLMSJavaScriptInterface javaScriptInterface;

    private ProgressBar progressBar;
    private View loadingLayout;

    private ELTApplication instance = ELTApplication.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ELTApplication.getInstance().getContentScoreListObserver().clearRetrieval();
        javaScriptInterface = new CLMSJavaScriptInterface(MainActivity.this, webModel);
        initViews();
        addWebViewListener();
        findViewById(R.id.tab_layout).setVisibility(View.GONE);
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
        instance.getContentScoreListObserver().registerObserver(this);
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.webview);
        webModel.registerObserver(this);
        webView.addJavascriptInterface(javaScriptInterface, "JSInterface");
        webView.loadUrl(Constants.LEARNING_URL);
        webView.getSettings().setJavaScriptEnabled(true);
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
                                javaScriptInterface.signOutUser();
                            else
                                teachingPressed(null);
                            break;
                        case Constants.SIGN_OUT:
                            javaScriptInterface.signOutUser();
                            break;
                    }
                }
                isNavigationPressed = false;
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                CLMSUser user = ELTApplication.getInstance().getCurrentUser();
                if (user == null)
                    user = SharedPreferencesUtils.getLoggedInUser(MainActivity.this);
//                ((TextView) header.findViewById(R.id.profile_name)).setText(user.getDisplayName());
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
        instance.getContentScoreListObserver().removeObserver(this);
    }

    public void learningPressed(View view) {
        if (!learningLayout.isSelected()) {
            updateTabSelection(true);
            switch (webLevel) {
                case Constants.HOME_LEVEL:
                    webView.loadUrl(Constants.LEARNING_URL);
                    toolbarTitle.setText(R.string.app_name);
                    break;
                case Constants.LESSON_LEVEL:
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
                case Constants.LESSON_LEVEL:
                    webView.loadUrl(Constants.LESSON_DOWNLOADED_URL);
                    break;
            }
        }
    }

    public void updateTabSelection(boolean isLearning) {
        if (isLearning) {
            learningLayout.setSelected(true);
            learningText.setSelected(true);
            teachingLayout.setSelected(false);
            teachingText.setSelected(false);

            ViewGroup.LayoutParams layoutParams =
                    findViewById(R.id.learning_inner).getLayoutParams();
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.tab_pressed_height);
            findViewById(R.id.learning_inner).setLayoutParams(layoutParams);

            ViewGroup.LayoutParams layoutParams2 =
                    findViewById(R.id.teaching_inner).getLayoutParams();
            layoutParams2.height = getResources().getDimensionPixelSize(R.dimen.tab_height);
            findViewById(R.id.teaching_inner).setLayoutParams(layoutParams2);
        } else {
            teachingLayout.setSelected(true);
            teachingText.setSelected(true);
            learningLayout.setSelected(false);
            learningText.setSelected(false);

            ViewGroup.LayoutParams layoutParams =
                    findViewById(R.id.teaching_inner).getLayoutParams();
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.tab_pressed_height);
            findViewById(R.id.teaching_inner).setLayoutParams(layoutParams);

            ViewGroup.LayoutParams layoutParams2 =
                    findViewById(R.id.learning_inner).getLayoutParams();
            layoutParams2.height = getResources().getDimensionPixelSize(R.dimen.tab_height);
            findViewById(R.id.learning_inner).setLayoutParams(layoutParams2);
        }
    }

    private void refreshWebView() {
        webView.reload();
        showSynchronizedDialog();
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
        if (wifiIcon != null)
            wifiIcon.setSelected(hasInternet);
    }

    private void updateTabs() {
        findViewById(R.id.tab_layout).setVisibility(View.VISIBLE);
        if (webLevel == Constants.LESSON_LEVEL) {
            learningText.setText(R.string.all_content);
            teachingText.setText(R.string.downloaded_content);
        } else if (webLevel == Constants.HOME_LEVEL) {
            learningText.setText(R.string.my_learning);
            teachingText.setText(R.string.my_teaching);
        } else if (webLevel == Constants.VIDEO_LEVEL)
            findViewById(R.id.tab_layout).setVisibility(View.GONE);
    }

    private void updateArrowIcon(boolean show) {
        backArrow.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void goBack(View View) {
        if (webLevel == Constants.VIDEO_LEVEL)
            addWebViewListener();
        webLevel--;
        switch (webLevel) {
            case Constants.HOME_LEVEL:
                webView.loadUrl(Constants.LEARNING_URL);
                break;
            case Constants.LESSON_LEVEL:
                webView.loadUrl(Constants.LESSON_ALL_CONTENT_URL);
                break;
        }
    }

    private void showSynchronizedDialog() {
        String message = "" + ELTApplication.getInstance().getLinkModel().getClassName() +
                " has been updated.";
        if (webLevel == Constants.HOME_LEVEL)
            message = "Courses have been updated.";
        else if (webLevel == Constants.VIDEO_LEVEL)
            message = "Message has been updated.";
        DialogUtils.createDialog(this, "UPDATE", message);
    }

    private void updateTabTitle() {
        int title = 0;
        switch (webLevel) {
            case Constants.HOME_LEVEL:
                title = R.string.app_name;
                break;
            case Constants.VIDEO_LEVEL:
                title = R.string.video_page;
                break;
        }
        if (title == 0)
            toolbarTitle.setText(instance.getLinkModel().getClassName());
        else
            toolbarTitle.setText(title);
    }

    private void addWebViewListener() {
        findViewById(R.id.tab_layout).setVisibility(View.VISIBLE);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if (url.equalsIgnoreCase(Constants.LEARNING_URL) ||
                        url.equalsIgnoreCase(Constants.TEACHING_URL))
                    updateArrowIcon(false);
                else
                    updateArrowIcon(true);
                webLevel = WebContentHelper.updateWebLevel(url);
                updateTabTitle();
                updateTabs();
                loadTeachingLearningURL(url);
                if (url.equalsIgnoreCase(Constants.LEARNING_URL) ||
                        url.equalsIgnoreCase(Constants.LESSON_ALL_CONTENT_URL))
                    updateTabSelection(true);
                else
                    updateTabSelection(false);
                loadLessonURL(url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                webView.setLayoutParams(params);
            }
        });
    }

    private void removeWebViewListener() {
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                showLoadingScreen(false);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                webView.setLayoutParams(params);
                showLoadingScreen(true);
            }
        });
    }

    private void loadTeachingLearningURL(String url) {
        if (url.equalsIgnoreCase(Constants.TEACHING_URL) ||
                url.equalsIgnoreCase(Constants.LEARNING_URL)) {
            ArrayList<Integer> contentCount = new ArrayList<Integer>();
            if (url.equalsIgnoreCase(Constants.TEACHING_URL))
                contentCount = WebContentHelper.updateCourseContent(MainActivity.this,
                        webView, false, Misc.hasInternetConnection(MainActivity.this));
            else if (url.equalsIgnoreCase(Constants.LEARNING_URL))
                contentCount = WebContentHelper.updateCourseContent(MainActivity.this,
                        webView, true, Misc.hasInternetConnection(MainActivity.this));
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
                    ELTApplication.getInstance().getContentScoreListObserver().getCourseId(),
                    ELTApplication.getInstance().getContentScoreListObserver().getClassId(),
                    url.equalsIgnoreCase(Constants.LESSON_DOWNLOADED_URL));
        }
    }

    private void showLoadingScreen(boolean isLoading) {
        loadingLayout.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
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
            if (webLevel == Constants.LESSON_LEVEL) {
                WebContentHelper.updateTabVisibility(webModel.isHasInternetConnection(),
                        learningLayout, teachingLayout);
                webView.reload();
            } else if (webLevel == Constants.HOME_LEVEL)
                webView.reload();
            if (webModel.isSynced()) {
                if (rotate != null)
                    rotate.cancel();
                DialogUtils.createDialog(this, "SYNC", webModel.getSyncMessage());
                this.internetModel.setIsSynced(false);
            }
        } else if (model instanceof CLMSClassListObserver) {
            CLMSClassListObserver classListObserver = (CLMSClassListObserver) model;
            if (classListObserver.isClassesRetrieved() && classListObserver.isCoursesRetrieved()) {
                ArrayList<Integer> contentCount = new ArrayList<Integer>();
                contentCount = WebContentHelper.updateCourseContent(this, webView, true,
                        Misc.hasInternetConnection(this));
                WebContentHelper.updateTabVisibility(contentCount.get(0),
                        contentCount.get(1), false, learningLayout, teachingLayout, webView,
                        navigationDrawerAdapter);
            }
        } else if (model instanceof CLMSContentScoreListObserver) {
            ELTApplication.getInstance().getContentScoreListObserver().clearRetrieval();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadLessonURL(webView.getUrl());
                }
            });
        } else if (model instanceof CLMSLinkModel) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    removeWebViewListener();
                    findViewById(R.id.tab_layout).setVisibility(View.GONE);
                    CLMSLinkModel linkModel = (CLMSLinkModel) model;
                    toolbarTitle.setText(linkModel.getClassName());
                    webLevel = Constants.VIDEO_LEVEL;
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
                            WebContentHelper.refreshContents(webView, model.getContentScore());
                            break;
                        case REFRESHED:
                            webView.reload();
                            break;
                        case LOADING:
                            showLoadingScreen(true);
                            break;
                        case NONE:
                            findViewById(R.id.tab_layout).setVisibility(View.VISIBLE);
                            showLoadingScreen(false);
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
        refreshIcon = menu.findItem(R.id.action_sync).getActionView().findViewById(R.id.refresh_icon);
        wifiIcon = (ImageView) menu.findItem(R.id.action_wifi).getActionView()
                .findViewById(R.id.wifi_icon);
        updateInternetConnectionIcon(Misc.hasInternetConnection(this));
        initRotation();
        menu.findItem(R.id.action_sync).setVisible(Misc.hasInternetConnection(this));
        refreshIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateIcon();
                javaScriptInterface.updateContentScores();
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
}