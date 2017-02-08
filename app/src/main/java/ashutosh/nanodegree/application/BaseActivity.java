package ashutosh.nanodegree.application;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ashutosh.nanodegree.popularmovies.R;

/**
 * Created by ashutosh on 1/3/16.
 */
public class BaseActivity extends AppCompatActivity {
    private Snackbar snackbar;

    protected void checkInternet() {
        if (!isOnLine()) {
            if (findViewById(R.id.ll_error_msg) == null && findViewById(R.id.frameLayout) != null)
                LayoutInflater.from(this).inflate(R.layout.error_msg_layout, (ViewGroup) findViewById(R.id.frameLayout), true);
            if (snackbar != null && snackbar.isShown())
                snackbar.dismiss();
            try {
                snackbar = getSnackBar(Snackbar.LENGTH_INDEFINITE, getString(R.string.no_internet_available), getString(R.string.retry));
                snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isOnLine()) {
                            networkFound();
                        } else {
                            checkInternet();
                        }
                    }
                });
                snackbar.show();
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        } else {
            networkFound();
            //getSnackBar(Snackbar.LENGTH_LONG, "There is some problem, write to developer", null).show();
        }

    }

    public boolean isOnLine() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                }
                return true;
            } else {
                // not connected to the internet
                return false;
            }
        }
        return false;
    }


    public void networkFound() {
        if (findViewById(R.id.ll_error_msg) != null)
            ((ViewGroup) findViewById(R.id.ll_error_msg).getParent()).removeView(findViewById(R.id.ll_error_msg));
    }

    public Snackbar getSnackBar(int length, String msg, String actionMsg) {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.coordinator), msg, length);
        return snackbar;
    }

}
