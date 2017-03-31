package com.mysampleapp.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.mobile.user.signin.CognitoUserPoolsSignInProvider;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.carvis.TemporarySpeedCamera;
import com.carvis.UserStat;
import com.mysampleapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeDemoFragment extends DemoFragmentBase {


    CognitoUserPoolsSignInProvider provider;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       provider = new CognitoUserPoolsSignInProvider(getActivity().getApplicationContext());
        getDaysSinceLastOverSpeed(provider.getUserName());

        return inflater.inflate(R.layout.fragment_demo_home, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final DemoListAdapter adapter = new DemoListAdapter(getActivity());
        adapter.addAll(DemoConfiguration.getDemoFeatureList());
        TextView textView = (TextView) view.findViewById(R.id.daysSinceOverSpeed);
        textView.setText("Hi "+provider.getUserName());


        ListView listView = (ListView) view.findViewById(android.R.id.list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                final DemoConfiguration.DemoFeature item = adapter.getItem(position);
                final AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null) {
                    final Fragment fragment = DemoInstructionFragment.newInstance(item.name);

                    activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, fragment, item.name)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();

                    // Set the title for the fragment.
                    final ActionBar actionBar = activity.getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setTitle(item.titleResId);
                    }
                }
            }
        });
    }

    private static final class DemoListAdapter extends ArrayAdapter<DemoConfiguration.DemoFeature> {
        private LayoutInflater inflater;

        public DemoListAdapter(final Context context) {
            super(context, R.layout.list_item_icon_text_with_subtitle);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view;
            ViewHolder holder;
            if (convertView == null) {
                view = inflater.inflate(R.layout.list_item_icon_text_with_subtitle, parent, false);
                holder = new ViewHolder();
                holder.iconImageView = (ImageView) view.findViewById(R.id.list_item_icon);
                holder.titleTextView = (TextView) view.findViewById(R.id.list_item_title);
                holder.subtitleTextView = (TextView) view.findViewById(R.id.list_item_subtitle);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) convertView.getTag();
            }

            DemoConfiguration.DemoFeature item = getItem(position);
            holder.iconImageView.setImageResource(item.iconResId);
            holder.titleTextView.setText(item.titleResId);
            holder.subtitleTextView.setText(item.subtitleResId);

            return view;
        }
    }

    private static final class ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
        TextView subtitleTextView;
    }

    public void getDaysSinceLastOverSpeed(String username) {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/daysinceoverspeed?username="+username;
        //final TextView speedLimitTextView = (TextView) findViewById(R.id.speedLimit);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("days", response.toString());
                            JSONObject obj = new JSONObject(response.toString());
                            int daysSinceOverSpeed = obj.getInt("daysOverSpeed");
                            //u.setDaysSinceOverSpeed(daysSinceOverSpeed);
                            TextView textView = (TextView) getActivity().findViewById(R.id.daysSinceOverSpeed);
                            textView.setText("Hi "+provider.getUserName()+" its been "+daysSinceOverSpeed+" days since last overspeed ");


                        } catch (JSONException e) {
                            Log.i("days", e.getMessage());
                        }
                        catch(Exception e){
                            Log.i("days ", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Sdays","ERROR");
                    }
                });
        queue.add(jsObjRequest);
    }
}
