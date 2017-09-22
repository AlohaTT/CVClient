package layout;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

import db.News;
import site.cvwit.cvclient.R;

public class NewsListFragment extends Fragment {
    private static final String NEWSURL = "http://192.168.1.100:8080/android_JSON/news/";
    private ArrayList<News> newsList;
    private NewsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        int columnID = getArguments().getInt("columnID");
        newsList = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview);
        getNewsFromServer(getActivity(), columnID);
        adapter = new NewsAdapter(newsList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new NewsItemDecoration());
        recyclerView.setAdapter(adapter);
        //recyclerView的item点击事件
        adapter.setOnItemClickListener(new NewsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                //将pk和slug发送到newsDetailFragment
                String slug = newsList.get(position).getSlug();
                int pk = newsList.get(position).getPk();
                NewsDetailFragment newsDetailFragment = new NewsDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("pk",pk);
                bundle.putString("slug", slug);
                newsDetailFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction().replace(R.id.news_fragment, newsDetailFragment);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    /**
     * 从服务器上获取数据，按照columnID来进行news划分
     *
     * @param context
     * @param columnID
     */
    public void getNewsFromServer(Context context, final int columnID) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(NEWSURL, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                parseNewsJsonData(response, columnID);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    /**
     * 解析服务器上获得的News Json数据
     *
     * @param response
     */
    private void parseNewsJsonData(JSONArray response, int columnID) {
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObject = response.getJSONObject(i);
                JSONObject fields = jsonObject.getJSONObject("fields");
                String title = fields.getString("title");
                String author = fields.getString("author");
                int column = fields.getInt("column");
                String slug = fields.getString("slug");
                int pk = jsonObject.getInt("pk");
                if (column == columnID) {
                    News news = new News();
                    news.setAuthor(author);
                    news.setTitle(title);
                    news.setColumnID(columnID);
                    news.setSlug(slug);
                    news.setPk(pk);
                    newsList.add(news);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * news的适配器
     */
    private static class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> implements View.OnClickListener {
        List<News> newsList;
        private OnItemClickListener mOnItemClickListener = null;

        public NewsAdapter(List<News> newsList) {
            this.newsList = newsList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            view.setOnClickListener(this);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            News news = newsList.get(position);
            holder.newsTitle.setText(news.getTitle().toString());

            holder.itemView.setTag(position);

        }

        @Override
        public int getItemCount() {
            return newsList.size();
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                //注意这里使用getTag方法获取position
                mOnItemClickListener.onItemClick(v, (int) v.getTag());
            }
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView newsTitle;


            public ViewHolder(View itemView) {
                super(itemView);
                newsTitle = (TextView) itemView.findViewById(R.id.news_title);
            }
        }

        public static interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }
    }

    /**
     *
     */
    private static class NewsItemDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, 0, 1);
        }
    }

}
