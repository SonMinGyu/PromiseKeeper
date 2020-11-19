package org.application.promisekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import fragment.chattingFragment;
import fragment.findPlaceFragment;

public class PromiseRoomActivity extends AppCompatActivity {

    TextView promiseTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promise_room);

        promiseTitle = (TextView) findViewById(R.id.promise_room_titleText);

        promiseTitle.setText(getStDate(getIntent().getExtras().getString("promiseDate")) + " - " + getIntent().getExtras().getString("promiseTitle"));

        Bundle bundle = new Bundle();
        bundle.putString("promiseDate", getIntent().getExtras().getString("promiseDate"));
        bundle.putString("promiseTitle", getIntent().getExtras().getString("promiseTitle"));
        bundle.putString("promisePlace", getIntent().getExtras().getString("promisePlace"));
        bundle.putStringArrayList("memberUid", getIntent().getExtras().getStringArrayList("memberUid"));
        findPlaceFragment fp = new findPlaceFragment();
        fp.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.promise_room_framLayout,  fp).commit();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.promise_room_bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.promise_room_bottomnavigation_selectPlace:

                        Bundle bundle = new Bundle();
                        bundle.putString("promiseDate", getIntent().getExtras().getString("promiseDate"));
                        bundle.putString("promiseTitle", getIntent().getExtras().getString("promiseTitle"));
                        bundle.putString("promisePlace", getIntent().getExtras().getString("promisePlace"));
                        bundle.putStringArrayList("memberUid", getIntent().getExtras().getStringArrayList("memberUid"));
                        findPlaceFragment fp = new findPlaceFragment();
                        fp.setArguments(bundle);

                        Fragment promiseRoomFragment = getSupportFragmentManager().findFragmentById(R.id.promise_room_framLayout);
                        if(promiseRoomFragment instanceof findPlaceFragment) {
                            return true;
                        }
                        else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.promise_room_framLayout,  fp).commit();
                            //menuTitleText.setText("Friends");
                            return true;
                        }

                    case R.id.promise_room_bottomnavigation_chat:

                        Bundle bundle2 = new Bundle();
                        bundle2.putString("promiseDate", getIntent().getExtras().getString("promiseDate"));
                        bundle2.putString("promiseTitle", getIntent().getExtras().getString("promiseTitle"));
                        bundle2.putString("promisePlace", getIntent().getExtras().getString("promisePlace"));
                        bundle2.putStringArrayList("memberUid", getIntent().getExtras().getStringArrayList("memberUid"));
                        chattingFragment fp2 = new chattingFragment();
                        fp2.setArguments(bundle2);

                        Fragment promiseRoomFragment2 = getSupportFragmentManager().findFragmentById(R.id.promise_room_framLayout);
                        if(promiseRoomFragment2 instanceof chattingFragment) {
                            return true;
                        }
                        else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.promise_room_framLayout,  fp2).commit();
                            return true;
                        }


                        /*
                        Fragment promiseFragment = getSupportFragmentManager().findFragmentById(R.id.mainFragment_framLayout);
                        if(promiseFragment instanceof fragment.promiseFragment) {
                            return true;
                        }
                        else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment_framLayout, new promiseFragment()).commit();
                            //menuTitleText.setText("Promise");
                            return true;
                        }

                         */
                }

                return false;
            }
        });
    }

    public String getStDate(String date)
    {
        String[] parseSt = date.split("\\+");
        String stDate = parseSt[0] + "년 " +parseSt[1] + "월 " + parseSt[2] + "일";

        return stDate;
    }


}