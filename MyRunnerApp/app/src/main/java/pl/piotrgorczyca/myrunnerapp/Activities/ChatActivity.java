package pl.piotrgorczyca.myrunnerapp.Activities;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.piotrgorczyca.myrunnerapp.AppConfig;
import pl.piotrgorczyca.myrunnerapp.MySingleton;
import pl.piotrgorczyca.myrunnerapp.R;
import pl.piotrgorczyca.myrunnerapp.adapters.ChatListAdapter;
import pl.piotrgorczyca.myrunnerapp.adapters.MailboxListAdapter;
import pl.piotrgorczyca.myrunnerapp.adapters.MailboxListItem;
import pl.piotrgorczyca.myrunnerapp.helper.SQLiteHandler;

/**
 * Created by Piotr on 2015-12-27. Enjoy!
 */
public class ChatActivity extends Activity implements View.OnClickListener {

    private ProgressDialog pDialog;
    private static final String TAG = "chat_list_activity";
    private static final String TAG_SEND = "chat_list_activity_send";

    private String user_id;
    private String name;
    private int sender_id;
    private int user_pid;
    private ChatListAdapter adapter;

    @Bind(R.id.chat_lv) protected ListView mList;
    @Bind(R.id.chat_list_header_name) protected TextView mName;
    @Bind(R.id.activity_chat_send_tv) protected TextView mSendTv;
    @Bind(R.id.activity_chat_send_et) protected EditText mSendEt;
    @Bind(R.id.chat_list_back_iv) protected ImageView mImageBackBtn;
    @Bind(R.id.chat_profile_circleView) protected ImageView mImageProfileBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        ButterKnife.bind(this);
        pDialog = new ProgressDialog(this);

        final Bundle bundle = getIntent().getExtras();
        user_id = bundle.getString("uid");
        name = bundle.getString("name");
        sender_id = bundle.getInt("sender_id");
        user_pid = bundle.getInt("user_pid");

        mName.setText(name);
        mImageBackBtn.setOnClickListener(this);
        mName.setOnClickListener(this);
        mImageProfileBtn.setOnClickListener(this);
        mSendTv.setOnClickListener(this);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        getChatList();
        adapter = new ChatListAdapter(this);
        mList.setAdapter(adapter);
        hideDialog();
    }

    private void getChatList() {
        showDialog();
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", user_id);
        params.put("sender_id", String.valueOf(sender_id));
        params.put("user_pid", String.valueOf(user_pid));
        JSONObject jsonObjParams = new JSONObject(params);


        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.POST, AppConfig.URL_GET_MESSAGES_FROM_USER, jsonObjParams, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GetChatList", "Before getting first object");

                        try {
                            JSONObject statusJsonObj = response.getJSONObject(0);
                            Log.d("GetChatList", "Get response: " + statusJsonObj.toString());
                            boolean error = statusJsonObj.getBoolean("error");
                            if (!error) {
                                for(int i=1; i<response.length(); i++) {
                                    if(response.getJSONObject(i).getInt("sender_id")==user_pid) {
                                        adapter.addSendedItem(response.getJSONObject(i).getString("content"), response.getJSONObject(i).getString("created_at"));
                                    } else
                                        adapter.addReceivedItem(response.getJSONObject(i).getString("content"), response.getJSONObject(i).getString("created_at"));
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
                        Log.e("MailboxFragment", "Error: " + error.getMessage());

                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest, TAG);

    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.setMessage("Downloading messages ...");
        pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.chat_list_back_iv:
                finish();
                break;

            case R.id.chat_profile_circleView:
            case R.id.chat_list_header_name:
                Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                intent.putExtra("user_id", Integer.toString(sender_id));
                startActivity(intent);
                break;

            case R.id.activity_chat_send_tv:
                sendNewMessage();
                break;
        }
    }

    private void sendNewMessage() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("content", mSendEt.getText().toString());
        params.put("receiver_id", String.valueOf(sender_id));
        params.put("user_pid", String.valueOf(user_pid));
        JSONObject jsonObjParams = new JSONObject(params);
        Log.e("params before sending: ", jsonObjParams.toString());

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.POST, AppConfig.URL_SEND_MESSAGE_TO_USER, jsonObjParams, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GetChatList", "Before sending object");

                        try {
                            JSONObject statusJsonObj = response.getJSONObject(0);
                            Log.d("GetChatList", "Get response: " + statusJsonObj.toString());
                            boolean error = statusJsonObj.getBoolean("error");
                            if (!error) {
                                String message = statusJsonObj.getString("status");

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
                        Log.e("MailboxFragment", "Error sending: " + error.getMessage());

                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest, TAG_SEND);
        adapter.clear();

        mSendEt.setText("");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getChatList();
        hideDialog();
    }
}
