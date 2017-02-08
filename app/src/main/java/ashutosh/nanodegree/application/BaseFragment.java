package ashutosh.nanodegree.application;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;

import ashutosh.nanodegree.network.NetworkLoader;
import ashutosh.nanodegree.network.ServerResponse;
import ashutosh.nanodegree.popularmovies.R;

/**
 * Created by Jhon Snowee on 5/7/2016.
 */
public class BaseFragment extends Fragment implements LoaderManager.LoaderCallbacks<ServerResponse> {


    public void networkFound(View v, Bundle bundle) {
        if (v.findViewById(R.id.ll_error_msg) != null)
            ((ViewGroup) v.findViewById(R.id.ll_error_msg).getParent()).removeView(v.findViewById(R.id.ll_error_msg));
    }

    @Override
    public LoaderManager getLoaderManager() {
        return super.getLoaderManager();
    }

    @Override
    public Loader<ServerResponse> onCreateLoader(int id, Bundle args) {
        return new NetworkLoader(getActivity(), args.getString(NetworkLoader.URL_PARAM), NetworkLoader.GET);

    }

    @Override
    public void onLoaderReset(Loader<ServerResponse> loader) {

    }

    @Override
    public void onLoadFinished(Loader<ServerResponse> loader, ServerResponse data) {

    }

}