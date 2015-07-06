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
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.cambridge.eltpoc.adapters.NavigationDrawerAdapter;
import org.cambridge.eltpoc.javascript.CLMSJavaScriptInterface;
import org.cambridge.eltpoc.model.CLMSModel;
import org.cambridge.eltpoc.model.CLMSUser;
import org.cambridge.eltpoc.model.CLMSWebModel;
import org.cambridge.eltpoc.observers.CLMSClassListObserver;
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
    private RelativeLayout learningLayout, teachingLayout;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        javaScriptInterface = new CLMSJavaScriptInterface(MainActivity.this, webModel);
        initViews();
        addWebViewListener();
        WebContentHelper.updateTabVisibility(0, 0, true, learningLayout, teachingLayout, webView);
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
            toolbarTitle.setText(ELTApplication.getInstance().getLinkModel().getClassName());
        else
            toolbarTitle.setText(title);
    }

    private void addWebViewListener() {
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
                if (rotate.hasStarted())
                    rotate.cancel();
                if (webLevel == Constants.VIDEO_LEVEL)
                    javaScriptInterface.showVideo();
                else if (findViewById(R.id.video_player).getVisibility() == View.VISIBLE)
                    javaScriptInterface.hideVideo();
                if(ELTApplication.getInstance().getWebModel().isCourseRetrieved()) {
                    if(url.equalsIgnoreCase(Constants.TEACHING_URL) ||
                            url.equalsIgnoreCase(Constants.LEARNING_URL)) {
                        ArrayList<Integer> contentCount = new ArrayList<Integer>();
                        if (url.equalsIgnoreCase(Constants.TEACHING_URL))
                            contentCount = WebContentHelper.updateCourseContent(MainActivity.this,
                                    webView, false);
                        else if (url.equalsIgnoreCase(Constants.LEARNING_URL))
                            contentCount = WebContentHelper.updateCourseContent(MainActivity.this,
                                    webView, true);
                        WebContentHelper.updateTabVisibility(contentCount.get(0),
                                contentCount.get(1), false, learningLayout, teachingLayout, webView);
                    }
                }
                if (url.equalsIgnoreCase(Constants.LEARNING_URL) ||
                        url.equalsIgnoreCase(Constants.LESSON_ALL_CONTENT_URL))
                    updateTabPressed(true);
                else
                    updateTabPressed(false);
                loadingLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                webView.setLayoutParams(params);
                rotateIcon();
            }
        });
    }

    private void initObservers() {
        internetModel.registerObserver(this);
        ELTApplication.getInstance().getCourseListObserver().registerObserver(this);
        ELTApplication.getInstance().getClassListObserver().registerObserver(this);
        ELTApplication.getInstance().getLinkModel().registerObserver(this);
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.webview);
        webModel.registerObserver(this);
        webView.addJavascriptInterface(javaScriptInterface, "JSInterface");
        webView.loadUrl(Constants.LEARNING_URL);
        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void initDrawer() {
        navigationDrawerAdapter = new NavigationDrawerAdapter(this, UIUtils.getNavigationArray(this),
                UIUtils.getNavigationDrawables());
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationList = (ListView) findViewById(R.id.right_drawer);
        View header = getLayoutInflater().inflate(R.layout.navigation_header, null);
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
                            if(navigationDrawerAdapter.isRemoved())
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
                invalidateOptionsMenu();
            }
        };

        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        toolbarTitle.setText(R.string.app_name);

        CLMSUser user = SharedPreferencesUtils.getLoggedInUser(this);
        ((TextView) header.findViewById(R.id.profile_name)).setText(user.getUsername());
    }

    private void initTabs() {
        learningLayout = (RelativeLayout) findViewById(R.id.learning_tab);
        learningText = (TextView) findViewById(R.id.learning_text);
        teachingLayout = (RelativeLayout) findViewById(R.id.teaching_tab);
        teachingText = (TextView) findViewById(R.id.teaching_text);

        learningLayout.setSelected(true);
        learningText.setSelected(true);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.RIGHT))
            drawerLayout.closeDrawer(Gravity.RIGHT);
        else {
            if (webView.canGoBack() && !webView.getUrl().equalsIgnoreCase(Constants.LEARNING_URL) &&
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
        webModel.removeObserver(this);
        internetModel.removeObserver(this);
        ELTApplication.getInstance().getCourseListObserver().removeObserver(this);
        ELTApplication.getInstance().getClassListObserver().removeObserver(this);
        ELTApplication.getInstance().getLinkModel().removeObserver(this);
        ELTApplication.getInstance().getWebModel().setIsCourseRetrieved(false);
    }

    @Override
    public void update(final CLMSModel model) {
        if (model instanceof CLMSWebModel) {
            CLMSWebModel webModel = (CLMSWebModel) model;
            updateInternetConnectionIcon(webModel.isHasInternetConnection());
        } else if (model instanceof CLMSClassListObserver)
            WebContentHelper.updateCourseContent(this, webView, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        refreshIcon = menu.findItem(R.id.action_sync).getActionView().findViewById(R.id.refresh_icon);
        wifiIcon = (ImageView) menu.findItem(R.id.action_wifi).getActionView()
                .findViewById(R.id.wifi_icon);
        updateInternetConnectionIcon(Misc.hasInternetConnection(this));
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
            updateTabPressed(true);
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

    public void updateTabPressed(boolean isLearning) {
        if (isLearning) {
            learningLayout.setSelected(true);
            learningText.setSelected(true);
            teachingLayout.setSelected(false);
            teachingText.setSelected(false);
        } else {
            teachingLayout.setSelected(true);
            teachingText.setSelected(true);
            learningLayout.setSelected(false);
            learningText.setSelected(false);
        }
    }

    public void teachingPressed(View view) {
        if (!teachingLayout.isSelected()) {
            updateTabPressed(false);
            switch (webLevel) {
                case Constants.HOME_LEVEL:
                    webView.loadUrl(Constants.TEACHING_URL);
                    toolbarTitle.setText(R.string.app_name);
                    break;
                case Constants.LESSON_LEVEL:
                    webView.loadUrl(Constants.LESSON_DONWLOADED_URL);
                    break;
            }
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
        webView.goBack();
    }

    private void showSynchronizedDialog() {
        String message = ""+ELTApplication.getInstance().getLinkModel().getClassName()+
                " has been updated.";
        if(webLevel == Constants.HOME_LEVEL)
            message = "Courses have been updated.";
        else if(webLevel == Constants.VIDEO_LEVEL)
            message = "Message has been updated.";
        DialogUtils.createDialog(this, "UPDATE", message);
    }
}