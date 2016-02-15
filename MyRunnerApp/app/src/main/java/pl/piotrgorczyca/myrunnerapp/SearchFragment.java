package pl.piotrgorczyca.myrunnerapp;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.piotrgorczyca.myrunnerapp.Activities.ProfileActivity;
import pl.piotrgorczyca.myrunnerapp.adapters.AttendingListAdapter;
import pl.piotrgorczyca.myrunnerapp.adapters.AttendingListItem;

/**
 * Created by Piotr on 2016-01-20. Enjoy!
 */
public class SearchFragment extends ListFragment implements View.OnClickListener {

    static final int SEARCH_USER_REQUEST = 1;
    private static final String TAG = "search_fragment";
    private AttendingListAdapter adapter;
    private ProgressDialog pDialog;
    @Bind(R.id.fragment_search_btn) protected ImageView mSearchIv;
    @Bind(R.id.fragment_search_et) protected EditText mSearchEt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, null);
        ButterKnife.bind(this, view);
        mSearchIv.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pDialog = new ProgressDialog(getActivity());
        adapter = new AttendingListAdapter(getActivity());
        setListAdapter(adapter);
        //hideDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        MySingleton.getInstance(this.getActivity()).cancelPendingRequests(TAG);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        AttendingListItem item = adapter.getItem(position);
        Intent intent = new Intent(getActivity().getApplicationContext(), ProfileActivity.class);
        intent.putExtra("user_id", Integer.toString(item.getUserId()));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_search_btn:
                Toast.makeText(getActivity(), "Search!", Toast.LENGTH_SHORT).show();
                adapter.clear();
                searchForUser();
                break;
        }
    }

    private void searchForUser() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_hint", mSearchEt.getText().toString());
        JSONObject jsonObjParams = new JSONObject(params);

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.POST, AppConfig.URL_SEARCH_FOR_USER, jsonObjParams, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("SearchList", "Before getting first object");

                        try {
                            JSONObject statusJsonObj = response.getJSONObject(0);
                            Log.d("SearchList", "Get response: " + statusJsonObj.toString());
                            boolean error = statusJsonObj.getBoolean("error");
                            if (!error) {
                                for(int i=1; i<response.length(); i++) {
                                    adapter.add(response.getJSONObject(i).getString("name"), Integer.parseInt(response.getJSONObject(i).getString("pid")));
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
                        Log.e("SearchFragment", "Error: " + error.getMessage());

                    }
                });
        MySingleton.getInstance(this.getActivity()).addToRequestQueue(jsArrayRequest, TAG);

    }
}
