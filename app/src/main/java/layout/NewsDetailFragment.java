package layout;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import site.cvwit.cvclient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsDetailFragment extends Fragment {


    public NewsDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news_detail, container, false);
        //从NewsListFragment中获得news内容地址
        String slug = getArguments().getString("slug");
        int pk = getArguments().getInt("pk");
        String newsDetailUrl="http://192.168.1.100:8080/article/"+pk+"/"+slug;
        WebView webview = (WebView) view.findViewById(R.id.id_webview);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(newsDetailUrl);
        return view;
    }


}
