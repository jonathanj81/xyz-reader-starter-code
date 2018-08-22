package com.example.xyzreader.ui;

import android.animation.AnimatorInflater;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private long mStartId;

    private int mSelectedItemId;
    private int mSelectedItemUpButtonFloor = Integer.MAX_VALUE;
    private int mTopInset;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("ACT-ONCREATE", ": activity oncreate called");
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_article_detail);

        getLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.viewPager_container);
        mPager.setAdapter(mPagerAdapter);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                    Log.i("ACT-PAGESELECT-CURPOS", ": " + position);
                }
                mSelectedItemId = position;
                Log.i("ACT-PAGESELECT-MSELID", ": " + mSelectedItemId);
            }
        });

        if (getIntent().hasExtra("position")){
            mSelectedItemId = getIntent().getIntExtra("position", 0);
            Log.i("ACT-GETINTENT", ": " + mSelectedItemId);
        }

        setUpBar();

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.share_fab);
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
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Log.i("ACT-CREATELOADER", ": activity createloader called");
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.i("ACT-LOADFINISHED", ": activity loadfinished called");
        mCursor = cursor;
        mCursor.moveToPosition(mSelectedItemId);
        Log.i("ACT-LOADFIN-MSELID", ": " + mSelectedItemId);
        mPagerAdapter.notifyDataSetChanged();
        mPager.setCurrentItem(mSelectedItemId);

        /*mPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPager.setCurrentItem(mSelectedItemId);
            }
        }, 250);*/


        // Select the start ID
        /*if (mStartId > 0) {
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    break;
                }
                mCursor.moveToNext();
            }
            mStartId = 0;
        } else {
            mPager.setCurrentItem(0);
        }*/
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.i("ACT-LOADERRESET", ": activity loaderreset called");
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            Log.i("ACT-PRIMARYITEM", ": activity primaryitem called");
            Log.i("ACT-ADAPTER-PRIM", ": " + position);
            ArticleDetailFragment fragment = (ArticleDetailFragment) object;
            super.setPrimaryItem(container, mSelectedItemId, fragment);
        }

        @Override
        public Fragment getItem(int position) {
            Log.i("ACT-GETITEM", ": activity getitem called");
            mCursor.moveToPosition(mSelectedItemId);
            Log.i("ACT-ADAPTER-GETITEM", ": " + position);
            Log.i("ACT-ADAPTER-DBID", ": " + mCursor.getLong(ArticleLoader.Query._ID));
            return ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }
    }

    private void setUpBar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        actionbar.setDisplayShowTitleEnabled(false);

        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.fragment_app_bar);
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
}
