package com.rawalinfocom.rcontact.helper.pinterest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rawalinfocom.rcontact.R;
import com.rawalinfocom.rcontact.constants.AppConstants;
import com.rawalinfocom.rcontact.helper.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PinterestTempActivity extends AppCompatActivity {

    // Pinterest
    private PDKClient pdkClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinterest_temp);

        pdkClient = PDKClient.configureInstance(this, AppConstants.appID);

        // Call onConnect() method to make link between App id and Pinterest SDK
        pdkClient.onConnect(this);
        pdkClient.setDebugMode(true);

        if (Utils.getStringPreference(PinterestTempActivity.this, AppConstants
                .KEY_PINTEREST_LOIN_PREFERENCES, "0").equalsIgnoreCase("0")) {
            Utils.setStringPreference(PinterestTempActivity.this, AppConstants
                    .KEY_PINTEREST_LOIN_PREFERENCES, "1");
            pinterestLogin();
        } else {
            PinterestTempActivity.this.finish();//finishing activity
        }
    }

    /**
     * It handle reuslt and switch back to own app when authentication process complete
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pdkClient.onOauthResponse(requestCode, resultCode,
                data);
    }

    private void pinterestLogin() {

        List scopes = new ArrayList<String>();
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PUBLIC);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_RELATIONSHIPS);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_READ_PRIVATE);
        scopes.add(PDKClient.PDKCLIENT_PERMISSION_WRITE_PRIVATE);

        pdkClient.login(this, scopes, new PDKCallback() {

            /**
             * It called, when Authentication success
             * @param response
             */
            @Override
            public void onSuccess(PDKResponse response) {

                Log.e(getClass().getName(), response.getData().toString());

                try {

                    JSONObject jsonObject = new JSONObject(response.getData().toString());

                    Intent intent = new Intent();
                    intent.putExtra("isBack", "0");
                    intent.putExtra("socialId", jsonObject.optString("url"));
                    setResult(RESULT_OK, intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                PinterestTempActivity.this.finish();//finishing activity
            }

            /**
             * It called, when Authentication failed
             * @param exception
             */
            @Override
            public void onFailure(PDKException exception) {
                Log.e(getClass().getName(), exception.getDetailMessage());
                PinterestTempActivity.this.finish();//finishing activity
            }


        });
    }
}
