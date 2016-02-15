package pl.piotrgorczyca.myrunnerapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.piotrgorczyca.myrunnerapp.Activities.CreateTrainingActivity;
import pl.piotrgorczyca.myrunnerapp.Activities.TrainingPageActivity;
import pl.piotrgorczyca.myrunnerapp.adapters.TrainingListAdapter;
import pl.piotrgorczyca.myrunnerapp.adapters.TrainingListItem;

public class TrainingListFragment extends ListFragment implements View.OnClickListener {

    static final int CREATE_TRAINING_REQUEST = 1;
    private ProgressDialog pDialog;
    private static final String TAG = "training_list_fragment";
    private TrainingListAdapter adapter;

    @Bind(R.id.training_list_create_btn) protected ImageView mCreateTrainingIv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_list, null);
        ButterKnife.bind(this, view);
        mCreateTrainingIv.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pDialog = new ProgressDialog(getActivity());
        getTrainingsList();
        adapter = new TrainingListAdapter(getActivity());
        setListAdapter(adapter);
        hideDialog();
    }

    @Override
    public void onStop() {
        super.onStop();
        MySingleton.getInstance(this.getActivity()).cancelPendingRequests(TAG);
    }

    private void getTrainingsList() {
        showDialog();
        JSONObject objParams = null;
        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, AppConfig.URL_GET_TRAININGS, objParams, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("GetTrainingsList", "Before getting first object");

                        try {
                            JSONObject statusJsonObj = response.getJSONObject(0);
                            Log.d("GetTrainingsList", "Get trainings Response: " + statusJsonObj.toString());
                            boolean error = statusJsonObj.getBoolean("error");
                            if (!error) {
                                String message = statusJsonObj.getString("status");
                                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                for(int i=1; i<response.length(); i++) {
                                    Log.d("Get insideloop", response.getJSONObject(i).getString("name"));
                                    adapter.add(response.getJSONObject(i).getString("name"), response.getJSONObject(i).getString("place"), response.getJSONObject(i).getInt("distance"), response.getJSONObject(i).getString("time_of_training"), response.getJSONObject(i).getInt("tid"), response.getJSONObject(i).getInt("user_id"));
                                }
                            } else {

                                // Error occurred during downloading. Get the error message
                                String errorMsg = statusJsonObj.getString("error_msg");
                                Toast.makeText(getActivity().getApplicationContext(),
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
                        Log.e("CreateTrainingActivity", "Registration Error: " + error.getMessage());
                        //Toast.makeText(getActivity().getApplicationContext(),
                              //  error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        MySingleton.getInstance(this.getActivity()).addToRequestQueue(jsArrayRequest, TAG);
        hideDialog();

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        TrainingListItem item = adapter.getItem(position);
        Intent intent = new Intent(getActivity().getApplicationContext(), TrainingPageActivity.class);
        intent.putExtra("name", item.getName());
        intent.putExtra("place", item.getPlace());
        intent.putExtra("time", item.getFullDate());
        intent.putExtra("distance", item.getDistance());
        intent.putExtra("tid", item.getTid());
        intent.putExtra("organizer_id", item.getUserId());
        startActivity(intent);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.setMessage("Downloading trainings ...");
        pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.training_list_create_btn:
                Intent intent = new Intent(getActivity(), CreateTrainingActivity.class);
                startActivityForResult(intent, CREATE_TRAINING_REQUEST);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CREATE_TRAINING_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // The user created a training.
                // refreshing the trainings list
                adapter.clear();
                getTrainingsList();
            }
        }
    }
}
