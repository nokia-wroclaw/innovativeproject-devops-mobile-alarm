package pwr.android_app.network.notifications;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    /* ========================================= METHODS ======================================== */

    @Override
    public void onTokenRefresh() {

        // Get updated InstanceID token.
        String FireBaseToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("TOKEN", "Refreshed token: " + FireBaseToken);

        // ToDo: W przypadku odświeżenia tokenu należy wysłać go do serwera!!!
    }

    /* ========================================================================================== */
}
