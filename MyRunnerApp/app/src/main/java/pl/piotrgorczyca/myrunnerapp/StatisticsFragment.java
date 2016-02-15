package pl.piotrgorczyca.myrunnerapp;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import pl.piotrgorczyca.myrunnerapp.helper.SQLiteHandler;

/**
 * Created by Piotr on 2016-01-04. Enjoy!
 */
public class StatisticsFragment extends Fragment {

    private String user_id;
    private SQLiteHandler db;
    private static final String TAG = "statistics_fragment";

    @Bind(R.id.statistics_total_distance) protected TextView mTotalDistanceTv;
    @Bind(R.id.statistics_max_distance) protected TextView mMaxDistanceTv;
    @Bind(R.id.statistics_avg_man_distance) protected TextView mAvgManDistanceTv;
    @Bind(R.id.statistics_avg_woman_distance) protected TextView mAvgWomanDistanceTv;
    @Bind(R.id.statistics_new_user_man) protected TextView mNewUserCountManTv;
    @Bind(R.id.statistics_new_user_woman) protected  TextView mNewUserCountWomanTv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new SQLiteHandler(getActivity().getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        user_id = user.get("id");
        getStatistics();
    }

    private void getStatistics() {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        JSONObject objParams = new JSONObject(params);

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.POST, AppConfig.URL_GET_STATISTICS, objParams, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject statusJsonObj = response.getJSONObject(0);
                            boolean error = statusJsonObj.getBoolean("error");
                            if (!error) {
                                String message = statusJsonObj.getString("status");
                                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                mTotalDistanceTv.setText("Total distance: " + statusJsonObj.getString("total_distance") + " km");
                                mMaxDistanceTv.setText("Max distance: " + statusJsonObj.getString("max_distance") + " km");
                                mAvgManDistanceTv.setText("Avg man distance: " + statusJsonObj.getString("average_distance_male") + " km");
                                mAvgWomanDistanceTv.setText("Avg woman distance: " + statusJsonObj.getString("average_distance_female") + " km");
                                mNewUserCountManTv.setText("Man: " + statusJsonObj.getString("new_user_count_male") + " new users");
                                mNewUserCountWomanTv.setText("Woman: " + statusJsonObj.getString("new_user_count_female") + " new users");


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
                        Log.e("StatisticsFragment", "Error: " + error.getMessage());
                        Toast.makeText(getActivity().getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsArrayRequest, TAG);

    }


}
