package pl.piotrgorczyca.myrunnerapp.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.piotrgorczyca.myrunnerapp.AppConfig;
import pl.piotrgorczyca.myrunnerapp.MySingleton;
import pl.piotrgorczyca.myrunnerapp.R;
import pl.piotrgorczyca.myrunnerapp.adapters.AttendingListAdapter;
import pl.piotrgorczyca.myrunnerapp.adapters.AttendingListItem;
import pl.piotrgorczyca.myrunnerapp.adapters.ChatListAdapter;
import pl.piotrgorczyca.myrunnerapp.helper.SQLiteHandler;

/**
 * Created by Piotr on 2015-12-30. Enjoy!
 */
public class TrainingPageActivity extends Activity implements View.OnClickListener {

    private ProgressDialog pDialog;
    private static final String TAG = "training_page_activity";
    private static final String TAG_ADD_ATTENDENCE = "training_page_join";
    private static final String TAG_QUIT_ATTENDENCE = "training_page_quit";

    private boolean isAttending = false;

    private int user_id;
    private String name;
    private int distance;
    private String place;
    private String time;
    private int tid;
    private int organizer_id;
    private AttendingListAdapter adapter;
    private SQLiteHandler db;


    @Bind(R.id.activity_training_page_lv) protected ListView mList;
    @Bind(R.id.activity_training_page_name_tv) protected TextView mNameTv;
    @Bind(R.id.activity_training_page_place) protected TextView mPlaceTv;
    @Bind(R.id.activity_training_page_distance) protected TextView mDistanceTv;
    @Bind(R.id.activity_training_page_time) protected TextView mTimeTv;
    @Bind(R.id.activity_training_page_join_tv) protected TextView mJoinTv;
    @Bind(R.id.activity_training_page_go_back_iv) protected ImageView mGoBackIv;
    @Bind(R.id.activity_training_page_circleView) protected ImageView mProfileIv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_page);
        ButterKnife.bind(this);

        pDialog = new ProgressDialog(this);
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");
        distance = bundle.getInt("distance");
        place = bundle.getString("place");
        time = bundle.getString("time");
        organizer_id = bundle.getInt("organizer_id");
        tid = bundle.getInt("tid");

        mNameTv.setText(name);
        mPlaceTv.setText(place);
        mDistanceTv.setText(Integer.toString(distance) + " km");
        mTimeTv.setText(time);

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        user_id = Integer.parseInt(user.get("id"));

        mGoBackIv.setOnClickListener(this);
        mNameTv.setOnClickListener(this);
        mProfileIv.setOnClickListener(this);
        mJoinTv.setOnClickListener(this);

        getAttendingList();
        adapter = new AttendingListAdapter(this);
        mList.setAdapter(adapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AttendingListItem item = adapter.getItem(position);
                Intent intent = new Intent(TrainingPageActivity.this, ProfileActivity.class);
                intent.putExtra("user_id", Integer.toString(item.getUserId()));
                startActivity(intent);
            }
        });

        hideDialog();
    }

    private void getAttendingList() {
        showDialog();
        Map<String, String> params = new HashMap<String, String>();
        params.put("tid", Integer.toString(tid));
        JSONObject jsonObjParams = new JSONObject(params);


        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.POST, AppConfig.URL_GET_ATTENDINGS, jsonObjParams, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GetAttendingsList", "Before getting first object");

                        try {
                            JSONObject statusJsonObj = response.getJSONObject(0);
                            Log.d("GetAttenfingsList", "Get response: " + statusJsonObj.toString());
                            boolean error = statusJsonObj.getBoolean("error");
                            if (!error) {
                                for(int i=1; i<response.length(); i++) {
                                    if(response.getJSONObject(i).getInt("user_id")==user_id) {
                                        adapter.add(response.getJSONObject(i).getString("name"), Integer.parseInt(response.getJSONObject(i).getString("user_id")));
                                        isAttending = true;
                                        mJoinTv.setText("Quit");

                                    } else
                                        adapter.add(response.getJSONObject(i).getString("name"), Integer.parseInt(response.getJSONObject(i).getString("user_id")));
                                }

                            } else {

                                // Error occurred during downloading. Get the error message
                                String errorMsg = statusJsonObj.getString("error_msg");

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("AttendingListFragment", "Error: " + error.getMessage());
                        //Toast.makeText(getApplicationContext(),
                                //error.getMessage(), Toast.LENGTH_LONG).show();
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
            case R.id.activity_training_page_go_back_iv:
                finish();
                break;

            case R.id.activity_training_page_circleView:
            case R.id.activity_training_page_name_tv:
                Intent intent = new Intent(TrainingPageActivity.this, ProfileActivity.class);
                intent.putExtra("user_id", Integer.toString(organizer_id));
                startActivity(intent);
                break;

            case R.id.activity_training_page_join_tv:
                if(isAttending) {
                    QuitTheTraining();

                } else {
                    JoinTheTraining();
                }
                break;
        }
    }
    private void JoinTheTraining() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", Integer.toString(user_id));
        params.put("tid", Integer.toString(tid));
        JSONObject jsonObjParams = new JSONObject(params);
        Log.e("params before sending: ", jsonObjParams.toString());

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.POST, AppConfig.URL_ADD_ATTENDENCE, jsonObjParams, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Add attendence", "Before sending object");

                        try {
                            JSONObject statusJsonObj = response.getJSONObject(0);
                            Log.d("Add attendence", "Get response: " + statusJsonObj.toString());
                            boolean error = statusJsonObj.getBoolean("error");
                            //if (!error) {
                                String message = statusJsonObj.getString("status");
                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                isAttending = true;

                           // } else {
                                String errorMsg = statusJsonObj.getString("error_msg");
                                //Toast.makeText(getApplicationContext(),
                                       // "Sorry, try again!", Toast.LENGTH_LONG).show();
                            //}
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MailboxFragment", "Error sending: " + error.getMessage());
                        //Toast.makeText(getApplicationContext(),
                                //error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest, TAG_ADD_ATTENDENCE);
        adapter.clear();
        mJoinTv.setText("Quit");
        getAttendingList();
        hideDialog();


    }

    private void QuitTheTraining() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", Integer.toString(user_id));
        params.put("tid", Integer.toString(tid));
        JSONObject jsonObjParams = new JSONObject(params);
        Log.e("params before sending: ", jsonObjParams.toString());

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.POST, AppConfig.URL_DELETE_ATTENDENCE, jsonObjParams, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Quit attendence", "Before sending object");

                        try {
                            JSONObject statusJsonObj = response.getJSONObject(0);
                            Log.d("Quit attendence", "Get response: " + statusJsonObj.toString());
                            boolean error = statusJsonObj.getBoolean("error");
                            //if (!error) {
                                String message = statusJsonObj.getString("status");
                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                isAttending = false;
                            //} else {
                                String errorMsg = statusJsonObj.getString("error_msg");
                               // Toast.makeText(getApplicationContext(),
                               //         "Sorry, try again!", Toast.LENGTH_LONG).show();
                            //}
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MailboxFragment", "Error sending: " + error.getMessage());
                        //Toast.makeText(getApplicationContext(),
                         //       error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest, TAG_QUIT_ATTENDENCE);
        adapter.clear();
        mJoinTv.setText("Join");
        getAttendingList();
        hideDialog();

    }
}