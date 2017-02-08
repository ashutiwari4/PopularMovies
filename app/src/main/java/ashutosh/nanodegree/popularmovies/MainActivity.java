package ashutosh.nanodegree.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import com.github.clans.fab.FloatingActionMenu;

import ashutosh.nanodegree.Application.BaseActivity;
import ashutosh.nanodegree.adapter.RecyclerViewAdapter;
import ashutosh.nanodegree.beans.Movies;
import ashutosh.nanodegree.beans.MoviesContent;
import ashutosh.nanodegree.database.AppDatabase;
import ashutosh.nanodegree.network.NetworkLoader;
import ashutosh.nanodegree.network.ServerResponse;
import ashutosh.nanodegree.network.WebUtils;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MainActivity extends BaseActivity implements
        View.OnClickListener, LoaderManager.LoaderCallbacks<ServerResponse>, RecyclerViewAdapter.OnItemClickListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerViewAdapter myRecyclerViewAdapter;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    private GridLayoutManager gridLayoutManager;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private int pageId = 1;

    private String curFilter = WebUtils.POPULAR;
    private FloatingActionMenu floatingActionMenu;
    private FrameLayout detailsFrameLayout;
    private Handler handler;
    private ProgressDialog progressDialog;
    private boolean isFavouriteFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.menu);
        findViewById(R.id.fab_most_popular).setOnClickListener(this);
        findViewById(R.id.fab_highest_rated).setOnClickListener(this);

        findViewById(R.id.fab_show_fav).setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.item_list);

        gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        myRecyclerViewAdapter = new RecyclerViewAdapter(this, MoviesContent.getInstance().getMoviesList(), R.layout.item_list_content);
        recyclerView.setAdapter(myRecyclerViewAdapter);

        detailsFrameLayout = (FrameLayout) findViewById(R.id.item_detail_container);
        if (detailsFrameLayout != null) {
            handler = new Handler();
            mTwoPane = true;
        }

        myRecyclerViewAdapter.setOnItemClickListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isFavouriteFilter) {
                    visibleItemCount = recyclerView.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if (totalItemCount > previousTotal) {
                            loading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                        // End has been reached
                        makeRequest(++pageId, curFilter);
                        loading = true;
                    }
                }

            }
        });


        checkInternet();
    }

    @Override
    public void networkFound() {
        super.networkFound();
        makeRequest(pageId, curFilter);
    }

    private void makeRequest(int pageId, String curFilter) {
        if (getSupportLoaderManager() != null) {
            Bundle bundle = new Bundle();
            bundle.putInt(WebUtils.PAGE_ID, pageId);
            bundle.putString("currentFilter", curFilter);
            getSupportLoaderManager().restartLoader(pageId, bundle, MainActivity.this);
        }
    }

    @Override
    public Loader<ServerResponse> onCreateLoader(int id, Bundle args) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        return new NetworkLoader(this, WebUtils.baseUrl + args.getString("currentFilter") +WebUtils.API_KEY_END_POINT+ args.getInt(WebUtils.PAGE_ID), NetworkLoader.GET);
    }

    @Override
    public void onLoadFinished(Loader<ServerResponse> loader, ServerResponse data) {
        if (progressDialog.isShowing()) progressDialog.cancel();
        if (data.getResponseCode() == 200) {
            MoviesContent.getInstance(MainActivity.this, data.getServerResponse());
            myRecyclerViewAdapter.notifyDataSetChanged();
            if (handler != null)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (detailsFrameLayout != null) {
                            Bundle arguments = new Bundle();
                            arguments.putParcelable("itemDetails", MoviesContent.getInstance().getMoviesList().get(0));
                            DetailFragment fragment = new DetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, fragment)
                                    .commit();
                        }
                    }
                });
        } else if (data.getServerResponse() == null || data.getServerResponse().equals("")) {
            getSnackBar(Snackbar.LENGTH_SHORT, getString(R.string.no_data_available), null).show();
        } else {
            getSnackBar(Snackbar.LENGTH_SHORT, data.getException().getMessage(), null).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<ServerResponse> loader) {

    }

    @Override
    public void onClick(View v) {
        reset();
        floatingActionMenu.close(true);
        switch (v.getId()) {
            case R.id.fab_most_popular:
                isFavouriteFilter = false;
                curFilter = WebUtils.POPULAR;
                makeRequest(pageId, curFilter);
                getSnackBar(Snackbar.LENGTH_LONG, "applying " + getFilterMsg() + " filter...", null).show();
                break;
            case R.id.fab_highest_rated:
                isFavouriteFilter = false;
                curFilter = WebUtils.TOP_RATED;
                makeRequest(pageId, curFilter);
                getSnackBar(Snackbar.LENGTH_LONG, "applying " + getFilterMsg() + " filter...", null).show();
                break;
            case R.id.fab_show_fav:
                isFavouriteFilter = true;
                showFav();
                break;
        }
    }

    private void reset() {
        pageId = 1;
        visibleItemCount = 0;
        totalItemCount = 0;
        firstVisibleItem = 0;
        previousTotal = 0;
        loading = true;
        MoviesContent.getInstance().getMoviesList().clear();
        myRecyclerViewAdapter.notifyDataSetChanged();
    }


    private String getFilterMsg() {
        String temp = "";
        if (curFilter == WebUtils.POPULAR) {
            temp = "popular";
        } else if (curFilter == WebUtils.TOP_RATED) {
            temp = "highest rated";
        }

        return temp;
    }

    @Override
    public void onItemClick(RecyclerViewAdapter.ItemHolder item, Movies movie, int position) {
        if (!mTwoPane)
            startActivity(new Intent(this, DetailActivity.class).putExtra("itemDetails", movie));
        else {
            Bundle arguments = new Bundle();
            arguments.putParcelable("itemDetails", movie);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        }
    }


    private void showFav() {
        reset();
        AppDatabase.getInstance(this).getAllFavMovies(MoviesContent.getInstance().getMoviesList());
        myRecyclerViewAdapter.notifyDataSetChanged();
    }
}
