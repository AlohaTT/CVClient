package layout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import site.cvwit.cvclient.R;

public class NewsFragment extends Fragment {

    private static final String url = "http://192.168.1.100:8080/android_JSON/news_column/";
    private static final String TAG = "NewsFragment";
    private TabLayout newsTab;
    private ViewPager newsViewPager;
    private List<Integer> tabList = new ArrayList<Integer>();
    int tabSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        newsTab = (TabLayout) view.findViewById(R.id.id_tablayout);
        for (int i = 0; i < 5; i++) {
            tabList.add(i);
        }

        newsViewPager = (ViewPager) view.findViewById(R.id.id_viewpager);


        tabSize = 0;


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(final JSONArray response) {
                final int newsColumnSize = response.length();
                FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {
                    @Override
                    public int getCount() {
                        return newsColumnSize;
                    }

                    @Override
                    public Fragment getItem(int position) {
                        return new Fragment2();
                    }

                    @Override
                    public CharSequence getPageTitle(int position) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(position);
                            JSONObject field = jsonObject.getJSONObject("fields");
                            String name = field.getString("name");
                            return name;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                };
                newsViewPager.setAdapter(adapter);
                newsTab.setupWithViewPager(newsViewPager);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, error.toString());
            }
        });
        requestQueue.add(jsonArrayRequest);
        Log.d(TAG, String.valueOf(tabSize));
        return view;
    }


    public NewsFragment() {
        super();
    }

//    @Override
//    public void onSuccess(JSONObject result) {
//        tabSize=result.length();
//        Log.d(TAG, String.valueOf(tabSize));
//    }
//
//    private interface VolleyCallback{
//        void onSuccess(JSONObject result);
//    }
}
