package pl.piotrgorczyca.myrunnerapp.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.piotrgorczyca.myrunnerapp.AppConfig;
import pl.piotrgorczyca.myrunnerapp.MySingleton;
import pl.piotrgorczyca.myrunnerapp.R;
import pl.piotrgorczyca.myrunnerapp.helper.SQLiteHandler;

/**
 * Created by Piotr on 2015-12-30. Enjoy!
 */
public class AddCommentActivity extends Activity implements View.OnClickListener {



    @Bind(R.id.add_comment_go_back_iv) protected ImageView mGoBackIv;
    @Bind(R.id.add_comment_btn) protected TextView mAddCommentTv;
    @Bind(R.id.add_comment_content) protected EditText mContent;
    private ProgressDialog pDialog;

    private String content;
    private String sender_id;
    private String user_id;

    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        ButterKnife.bind(this);
        pDialog = new ProgressDialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        sender_id = user.get("id");
        user_id = getIntent().getStringExtra("user_id");

        mGoBackIv.setOnClickListener(this);
        mAddCommentTv.setOnClickListener(this);
    }


    private void addComment(final String user_id,
                                final String sender_id, final String content) {
        // Tag used to cancel the request
        String tag_string_req = "req_add_comment";

        showDialog();
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        params.put("sender_id",sender_id);
        params.put("content", content);
        JSONObject jsonParams = new JSONObject(params);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, AppConfig.URL_ADD_COMMENT, jsonParams, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("CreateTrainingActivity", "Register Response: " + response.toString());
                        hideDialog();

                        try {
                            //JSONObject jObj = new JSONObject(response);
                            boolean error = response.getBoolean("error");
                            if (!error) {
                                // Inserting row in users table
                                //db.addUser(name, email, uid, created_at);
                                //String message = response.getString("status");
                                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                setResult(Activity.RESULT_OK);
                                finish();
                            } else {

                                // Error occurred in registration. Get the error
                                // message
                                //String errorMsg = response.getString("error_msg");
                                //Toast.makeText(getApplicationContext(),
                                     //   errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("AddCommentActivity", "Error: " + error.getMessage());
                        hideDialog();
                        //Toast.makeText(getApplicationContext(),
                         //       error.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });


        // Adding request to the RequestQueue.
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

            case R.id.add_comment_go_back_iv:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;

            case R.id.add_comment_btn:
                content = mContent.getText().toString();
                if (!content.isEmpty() && !user_id.isEmpty() && !sender_id.isEmpty()) {
                    addComment(user_id, sender_id, content);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Some data is missing!", Toast.LENGTH_LONG)
                            .show();}
                break;
        }
    }
}