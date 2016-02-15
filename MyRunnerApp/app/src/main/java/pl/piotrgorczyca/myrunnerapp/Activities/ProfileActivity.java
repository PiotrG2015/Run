package pl.piotrgorczyca.myrunnerapp.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.piotrgorczyca.myrunnerapp.AppConfig;
import pl.piotrgorczyca.myrunnerapp.MySingleton;
import pl.piotrgorczyca.myrunnerapp.R;
import pl.piotrgorczyca.myrunnerapp.adapters.ChatListAdapter;
import pl.piotrgorczyca.myrunnerapp.adapters.CommentsListAdapter;
import pl.piotrgorczyca.myrunnerapp.adapters.MailboxListItem;
import pl.piotrgorczyca.myrunnerapp.helper.CalendarHelper;
import pl.piotrgorczyca.myrunnerapp.helper.SQLiteHandler;

/**
 * Created by Piotr on 2015-12-29. Enjoy!
 */
public class ProfileActivity extends Activity implements View.OnClickListener{

    private static final int ADD_COMMENT_REQUEST = 2;

    private ProgressDialog pDialog;
    private static final String TAG = "profile_activity";
    private String user_id;

    @Bind(R.id.profile_lv) protected ListView mList;
    @Bind(R.id.profile_name) protected TextView mName;
    @Bind(R.id.profile_mileage) protected TextView mMileage;
    @Bind(R.id.profile_gender) protected TextView mGender;
    @Bind(R.id.profile_age) protected TextView mAge;
    @Bind(R.id.activity_profile_go_back_iv) protected ImageView mGoBackIv;
    @Bind(R.id.activity_profile_add_comment) protected ImageView mAddCommentIv;
    @Bind(R.id.activity_profile_message_iv) protected ImageView mMessageIv;

    private CommentsListAdapter adapter;
    private Intent intent;
    private SQLiteHandler db;
    private String uid;
    private String user_pid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        pDialog = new ProgressDialog(this);
        user_id = getIntent().getStringExtra("user_id");
        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        uid = user.get("uid");
        user_pid = user.get("id");

        mMessageIv.setOnClickListener(this);
        mAddCommentIv.setOnClickListener(this);
        mGoBackIv.setOnClickListener(this);

        getProfileData();
        getCommentsList();
        adapter = new CommentsListAdapter(this);
        mList.setAdapter(adapter);
        hideDialog();
    }

    private void getProfileData() {
        showDialog();
        Map<String, String> params = new HashMap<>();
        params.put("user_id" , user_id);
        JSONObject objParams = new JSONObject(params);

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.POST, AppConfig.URL_GET_USERS_PROFILE, objParams, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject statusJsonObj = response.getJSONObject(0);
                             boolean error = statusJsonObj.getBoolean("error");
                            if (!error) {
                                String message = statusJsonObj.getString("status");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                mName.setText(statusJsonObj.getString("name"));
                                mGender.setText("Gender: " + statusJsonObj.getString("gender"));
                                mAge.setText("Age: " + CalendarHelper.countAge(statusJsonObj.getString("date_of_birth")));
                                mMileage.setText("Total distance: " + statusJsonObj.getString("mileage") + " km");
                            } else {

                                // Error occurred during downloading. Get the error message
                                String errorMsg = statusJsonObj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ProfileActivity", "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest, TAG);

    }

    private void getCommentsList() {
        showDialog();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        JSONObject objParams = new JSONObject(params);
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.POST, AppConfig.URL_GET_USERS_COMMENT, objParams, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject statusJsonObj = response.getJSONObject(0);
                            boolean error = statusJsonObj.getBoolean("error");
                            if (!error) {
                                String message = statusJsonObj.getString("status");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                for (int i = 1; i < response.length(); i++) {
                                    adapter.add(response.getJSONObject(i).getString("name"), response.getJSONObject(i).getString("content"), CalendarHelper.countPassedTime(response.getJSONObject(i).getString("created_at")));
                                }

                            } else {
                                // Error occurred during downloading. Get the error message
                                String errorMsg = statusJsonObj.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ProfileActivity", "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest, TAG);

    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.setMessage("Downloading ...");
        pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.activity_profile_go_back_iv:
                finish();
            break;

            case R.id.activity_profile_add_comment:
                intent = new Intent(ProfileActivity.this, AddCommentActivity.class);
                intent.putExtra("user_id", user_id);
                startActivityForResult(intent, ADD_COMMENT_REQUEST);
            break;

            case R.id.activity_profile_message_iv:
                intent = new Intent(this, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                bundle.putString("name", (String) mName.getText());
                bundle.putInt("sender_id", Integer.parseInt(user_id));
                bundle.putInt("user_pid", Integer.parseInt(user_pid));
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ADD_COMMENT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // The user created a training.
                // refreshing the trainings list
                adapter.clear();
                getCommentsList();
            }
        }
    }
}
