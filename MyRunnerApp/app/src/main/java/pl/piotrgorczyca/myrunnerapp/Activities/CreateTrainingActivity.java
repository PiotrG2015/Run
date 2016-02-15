package pl.piotrgorczyca.myrunnerapp.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.piotrgorczyca.myrunnerapp.AppConfig;
import pl.piotrgorczyca.myrunnerapp.MySingleton;
import pl.piotrgorczyca.myrunnerapp.R;
import pl.piotrgorczyca.myrunnerapp.helper.SQLiteHandler;

public class CreateTrainingActivity extends Activity implements View.OnClickListener {

    @Bind(R.id.create_training_distance) protected EditText mDistance;
    @Bind(R.id.create_training_place) protected EditText mPlace;
    @Bind(R.id.create_training_time) protected EditText mTime;
    @Bind(R.id.activity_create_trainig_go_back_iv) protected ImageView mGoBackIv;
    @Bind(R.id.create_training_description) protected EditText mDescription;
    @Bind(R.id.create_training_btn) protected TextView mCreateTrainingTv;

    private ProgressDialog pDialog;
    private String description;
    private String time;
    private String place;
    private String distance;
    private String user_id;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_training);
        ButterKnife.bind(this);
        pDialog = new ProgressDialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        user_id = user.get("id");

        mGoBackIv.setOnClickListener(this);
        mCreateTrainingTv.setOnClickListener(this);
    }


    private void createTraining(final String description,
                                final String place, final String time, final String distance, final String user_id) {
        // Tag used to cancel the request
        String tag_string_req = "req_create_training";

        showDialog();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("time_of_training", time);
        params.put("description", description);
        params.put("place", place);
        params.put("distance", distance);
        JSONObject jsonParams = new JSONObject(params);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, AppConfig.URL_CREATE_TRAINING, jsonParams, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("CreateTrainingActivity", "Register Response: " + response.toString());
                        hideDialog();

                        try {
                            //JSONObject jObj = new JSONObject(response);
                            boolean error = response.getBoolean("error");
                            if (!error) {
                                // Inserting row in users table
                                String message = response.getString("status");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                setResult(Activity.RESULT_OK);
                                finish();
                            } else {

                                // Error occurred in registration. Get the error
                                // message
                                String errorMsg = response.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CreateTrainingActivity", "Registration Error: " + error.getMessage());
                        hideDialog();
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest, tag_string_req);

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.setMessage("Creating new training ...");
        pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {

            case R.id.activity_create_trainig_go_back_iv:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;

            case R.id.create_training_btn:

            description = mDescription.getText().toString();
            place = mPlace.getText().toString();
            time = mTime.getText().toString();
            distance = mDistance.getText().toString();
            if (!description.isEmpty() && !place.isEmpty() && !time.isEmpty() && !distance.isEmpty() && !user_id.isEmpty()) {
                createTraining(description, place, time, distance, user_id);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Some data is missing!", Toast.LENGTH_LONG)
                        .show();}
                break;
        }
    }
}