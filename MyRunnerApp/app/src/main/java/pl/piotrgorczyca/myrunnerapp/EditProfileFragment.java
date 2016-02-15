package pl.piotrgorczyca.myrunnerapp;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import pl.piotrgorczyca.myrunnerapp.helper.CalendarHelper;
import pl.piotrgorczyca.myrunnerapp.helper.SQLiteHandler;

/**
 * Created by Piotr on 2016-01-05. Enjoy!
 */
public class EditProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "edit_profile_fragment_GET";
    private static final String TAG_PUT = "edit_profile_fragment_PUT";

    private String gender = "N/A";
    private String name;
    private String dateOfBirth;

    @Bind(R.id.edit_profile_name) protected TextView mNameTv;
    @Bind(R.id.edit_profile_mileage) protected TextView mTotalDistanceTv;
    @Bind(R.id.edit_profile_gender) protected TextView mGenderTv;
    @Bind(R.id.edit_profile_age) protected TextView mAgeTv;
    @Bind(R.id.edit_profile_new_name_ett) protected EditText mNewNameEt;
    @Bind(R.id.edit_profile_new_birth_date_et) protected EditText mNewBirthDateEt;
    @Bind(R.id.edit_profile_radio_group) protected  RadioGroup mRadioGroup;
    @Bind(R.id.edit_profile_confirm) protected ImageView mConfirmIv;

    private SQLiteHandler db;
    private String user_id;
    private String user_uid;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, null);
        ButterKnife.bind(this, view);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_male:
                        //Toast.makeText(getActivity(), "male", Toast.LENGTH_SHORT).show();
                        gender = "male";
                        break;
                    case R.id.radio_female:
                       // Toast.makeText(getActivity(), "female", Toast.LENGTH_SHORT).show();
                        gender = "female";
                        break;
                }
            }
        });
        mConfirmIv.setOnClickListener(this);
        return view;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new SQLiteHandler(getActivity().getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        user_id = user.get("id");
        user_uid = user.get("uid");
        getProfileData();
    }

    private void getProfileData() {
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
                                Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                mNameTv.setText(statusJsonObj.getString("name"));
                                name = statusJsonObj.getString("name");
                                mGenderTv.setText("Gender: " + statusJsonObj.getString("gender"));
                                gender = statusJsonObj.getString("name");
                                mAgeTv.setText("Age: " + CalendarHelper.countAge(statusJsonObj.getString("date_of_birth")));
                                dateOfBirth = statusJsonObj.getString("date_of_birth");
                                mTotalDistanceTv.setText("Total distance: " + statusJsonObj.getString("mileage") + " km");
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
                        Log.e("ProfileActivity", "Error: " + error.getMessage());
                        Toast.makeText(getActivity().getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsArrayRequest, TAG);

    }

    private void putProfileData() {
        Map<String, String> params = new HashMap<>();
        params.put("uid" , user_uid);
        params.put("birth_date", (mNewBirthDateEt.getText().toString()==null)?dateOfBirth:mNewBirthDateEt.getText().toString());
        params.put("name", (mNewNameEt.getText().toString()==null)?name:mNewNameEt.getText().toString());
        params.put("gender", gender);

        JSONObject objParams = new JSONObject(params);

        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.PUT, AppConfig.URL_GET_USERS_PROFILE, objParams, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject statusJsonObj = response.getJSONObject(0);
                            boolean error = statusJsonObj.getBoolean("error");
                            if (!error) {
                                Toast.makeText(getActivity(), "Changed profile data", Toast.LENGTH_SHORT).show();
                            } else {

                                // Error occurred during downloading. Get the error message
                                String errorMsg = statusJsonObj.getString("error_msg");
                                //Toast.makeText(getActivity().getApplicationContext(),
                                   //     errorMsg, Toast.LENGTH_LONG).show();
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
                        //Toast.makeText(getActivity().getApplicationContext(),
                               // error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsArrayRequest, TAG_PUT);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.edit_profile_confirm:
                putProfileData();
                getProfileData();
                break;
        }
    }
}
