package com.example.xyzreader.ui;

import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.xyzreader.R;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity {

    private int mSelectedItemId;
    private BookSwipeListener mBookSwipeListener;
    private static final String ID_KEY = "position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_article_detail);

        if (savedInstanceState == null) {
            if (getIntent().hasExtra(ID_KEY)) {
                mSelectedItemId = getIntent().getIntExtra(ID_KEY, 0);
                openNewFragment(3);
            }
        } else {
            mSelectedItemId = savedInstanceState.getInt(ID_KEY);
        }

        setUpBar();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.share_fab);
        fab.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(ArticleDetailActivity.this)
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.action_share)));
            }
        });
        listenForSwipes();
    }

    private void openNewFragment(int transactionType) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().disallowAddToBackStack();
        ArticleDetailFragment frag = ArticleDetailFragment.newInstance(mSelectedItemId);

        switch (transactionType) {
            case 1:
                //frag.setEnterTransition(new Slide(Gravity.RIGHT));
                //frag.setExitTransition(new Slide(Gravity.LEFT));
                ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            case 2:
                //frag.setEnterTransition(new Slide(Gravity.LEFT));
                //frag.setExitTransition(new Slide(Gravity.RIGHT));
                ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case 3:
                //frag.setEnterTransition(new Slide(Gravity.RIGHT));
                //frag.setExitTransition(new Slide(Gravity.LEFT));
                ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                break;
            default:
                break;
        }
        ft.replace(R.id.fragment_container, frag);
        ft.commit();
    }

    private void setUpBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        actionbar.setDisplayShowTitleEnabled(false);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.fragment_app_bar);
        if (Build.VERSION.SDK_INT >= 21) {
            appBarLayout.setStateListAnimator(AnimatorInflater.loadStateListAnimator(ArticleDetailActivity.this, R.anim.appbar_elevation));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onNavigateUp();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void listenForSwipes() {
        FrameLayout container = (FrameLayout) findViewById(R.id.fragment_container);
        final int count = ArticleListActivity.bookIDs.length;

        mBookSwipeListener = new BookSwipeListener(this) {
            @Override
            public void onSwipeLeft() {
                mSelectedItemId = (mSelectedItemId < (count - 1)) ? mSelectedItemId + 1 : 0;
                openNewFragment(1);
            }

            @Override
            public void onSwipeRight() {
                mSelectedItemId = (mSelectedItemId > 0) ? mSelectedItemId - 1 : count - 1;
                openNewFragment(2);
            }
        };
        container.setOnTouchListener(mBookSwipeListener);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mBookSwipeListener.getGestureDetector().onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ID_KEY, mSelectedItemId);
        super.onSaveInstanceState(outState);
    }
}
