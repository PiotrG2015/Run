package pl.piotrgorczyca.myrunnerapp;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.piotrgorczyca.myrunnerapp.Activities.ChatActivity;
import pl.piotrgorczyca.myrunnerapp.adapters.MailboxListAdapter;
import pl.piotrgorczyca.myrunnerapp.adapters.MailboxListItem;
import pl.piotrgorczyca.myrunnerapp.helper.SQLiteHandler;

public class MailboxFragment extends ListFragment {

    private ProgressDialog pDialog;
    private static final String TAG = "mailbox_list_fragment";
    private MailboxListAdapter adapter;
    private SQLiteHandler db;
    private String user_id;
    private String user_pid;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pDialog = new ProgressDialog(getActivity());
        db = new SQLiteHandler(getActivity().getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        user_id = user.get("uid");
        user_pid = user.get("id");
        getMailboxList();
        adapter = new MailboxListAdapter(getActivity());
        setListAdapter(adapter);
        hideDialog();
    }

    private void getMailboxList() {
        showDialog();
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", user_id);
        JSONObject jsonObjParams = new JSONObject(params);


        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.POST, AppConfig.URL_GET_MESSAGES, jsonObjParams, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GetMailboxList", "Before getting first object");

                        try {
                            JSONObject statusJsonObj = response.getJSONObject(0);
                            Log.d("GetMailboxList", "Get mailbox Response: " + statusJsonObj.toString());
                            boolean error = statusJsonObj.getBoolean("error");
                            if (!error) {
                                String message = statusJsonObj.getString("status");
                                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                for(int i=1; i<response.length(); i++) {
                                    if(response.getJSONObject(i).getInt("is_new")==1 && !response.getJSONObject(i).getString("sender_id").equals(user_pid)){
                                        adapter.addNewItem(response.getJSONObject(i).getString("name"), response.getJSONObject(i).getString("content"), response.getJSONObject(i).getInt("sender_id"), response.getJSONObject(i).getInt("is_new"), response.getJSONObject(i).getString("created_at"));

                                    } else {
                                    adapter.addNormalItem(response.getJSONObject(i).getString("name"), response.getJSONObject(i).getString("content"), response.getJSONObject(i).getInt("sender_id"), response.getJSONObject(i).getInt("is_new"), response.getJSONObject(i).getString("created_at"));
                                    }
                                }
                                adapter.notifyDataSetChanged();

                            } else {

                                // Error occurred during downloading. Get the error message
                                String errorMsg = statusJsonObj.getString("error_msg");
                                Toast.makeText(getActivity().getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                                //messsages.add(new MailboxListItem("No messages", "An error occurred", 0, "00:00", 0));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("MailboxFragment", "Error: " + error.getMessage());
                        Toast.makeText(getActivity().getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        MySingleton.getInstance(this.getActivity()).addToRequestQueue(jsArrayRequest, TAG);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        MailboxListItem item = adapter.getItem(position);
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("uid", user_id);
        bundle.putString("name", item.getName());
        bundle.putInt("sender_id", item.getSenderId());
        bundle.putInt("user_pid", Integer.parseInt(user_pid));
        intent.putExtras(bundle);
        startActivity(intent);
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
}