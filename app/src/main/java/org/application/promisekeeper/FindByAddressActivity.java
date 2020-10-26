package org.application.promisekeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindByAddressActivity extends AppCompatActivity {

    private WebView browser;
    final Geocoder geocoder = new Geocoder(this);

    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data) {
            Bundle extra = new Bundle();
            Intent intent = new Intent();
            extra.putString("data", data);
            intent.putExtras(extra);
            setResult(RESULT_OK, intent);

            List<Address> list = null;

            String str = data;
            try {
                list = geocoder.getFromLocationName(
                        str, // 지역 이름
                        10); // 읽을 개수
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
            }

            if (list != null) {
                if (list.size() == 0) {
                    //tv.setText("해당되는 주소 정보는 없습니다");
                } else {
                    //tv.setText(list.get(0).toString());
                    //          list.get(0).getCountryName();  // 국가명
                    //          list.get(0).getLatitude();        // 위도
                    //          list.get(0).getLongitude();    // 경도
                }
            }

            Map<String, Object> taskMap = new HashMap<String, Object>();
            taskMap.put("promisePlace", data);
            taskMap.put("promisePlaceLatitude", list.get(0).getLatitude());
            taskMap.put("promisePlaceLongitude", list.get(0).getLongitude());
            FirebaseDatabase.getInstance().getReference().child("promise")
                    .child(getIntent().getExtras().getString("promiseKey")).updateChildren(taskMap);

            //Toast.makeText(getApplicationContext(), "약속 장소가 설정되었습니다!", Toast.LENGTH_SHORT).show();

            Toast.makeText(getApplicationContext(), "실행", Toast.LENGTH_SHORT).show();
            System.out.println("mainmainmain save");
            //et_address.setText(data);

            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_by_address);

        browser = (WebView) findViewById(R.id.find_by_address_webView);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "Android");

        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                browser.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        browser.loadUrl("http://studyserver.dothome.co.kr/daum.html");
    }
}