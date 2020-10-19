package org.application.promisekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.application.promisekeeper.Model.InviteCheckModel;
import org.application.promisekeeper.Model.PromiseModel;
import org.application.promisekeeper.Model.UserModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PromiseOfTheDayActivity extends Activity {

    RecyclerView recyclerView;
    TextView promiseDate;
    FloatingActionButton addPromiseButton;
    public static ArrayList<PromiseModel> staticPromiseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promise_of_the_day);

        promiseDate = (TextView) findViewById(R.id.promise_of_the_day_dayText);
        addPromiseButton = (FloatingActionButton) findViewById(R.id.promise_of_the_day_button);

        recyclerView = (RecyclerView) findViewById(R.id.promise_of_the_day_recyclerView);
        recyclerView.setAdapter(new PromisesRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        promiseDate.setText(getStDate(getIntent().getExtras().getString("selectDate")));

        addPromiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createPromise = new Intent(PromiseOfTheDayActivity.this, CreatePromise.class);
                createPromise.putExtra("selectDate2", getIntent().getExtras().getString("selectDate"));
                PromiseOfTheDayActivity.this.startActivity(createPromise);
            }
        });

    }

    class PromisesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<PromiseModel> theDayPromiseModels = new ArrayList<>();

        public PromisesRecyclerViewAdapter() {

            FirebaseDatabase.getInstance().getReference().child("promise")
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    theDayPromiseModels.clear();
                    for(DataSnapshot item :dataSnapshot.getChildren())
                    {
                        if(getIntent().getExtras().getString("selectDate").equals(item.getValue(PromiseModel.class).getPromiseDate())) {
                            for(int i = 0; i < item.getValue(PromiseModel.class).getMemberUids().size(); i++)
                            {
                                if(item.getValue(PromiseModel.class).getMemberUids().get(i)
                                        .equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                {
                                    theDayPromiseModels.add(item.getValue(PromiseModel.class));
                                }
                            }
                        }
                    }

                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @NonNull
        @Override // item_studyroom을 리사이클러뷰에 연결
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promise_of_the_day, parent, false);

            PromiseOfTheDayActivity.PromisesRecyclerViewAdapter.CustomViewHolder holder = new PromiseOfTheDayActivity.PromisesRecyclerViewAdapter.CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final PromiseOfTheDayActivity.PromisesRecyclerViewAdapter.CustomViewHolder customViewHolder = (PromiseOfTheDayActivity.PromisesRecyclerViewAdapter.CustomViewHolder) holder;

            /*
            Glide.with(customViewHolder.itemView.getContext())
                    .load(studyModels.get(position).getProfile())
                    .error(R.drawable.studyroom_logo)
                    .into(((CustomViewHolder)holder).imageView);

             */

            customViewHolder.promiseTitle.setText(theDayPromiseModels.get(position).getPromiseTitle());
            customViewHolder.promiseTime.setText(theDayPromiseModels.get(position).getPromiseTime());
        }

        @Override
        public int getItemCount() {
            return (theDayPromiseModels != null ? theDayPromiseModels.size() : 1);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView promiseTitle;
            public TextView promiseTime;

            public CustomViewHolder(View view) {
                super(view);
                promiseTitle =(TextView) view.findViewById(R.id.item_promise_of_the_day_title);
                promiseTime = (TextView) view.findViewById(R.id.item_promise_of_the_day_time);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            if(getToDay().equals(getStDate(theDayPromiseModels.get(position).getPromiseDate()))
                                    && (get_Hour(theDayPromiseModels.get(position).getPromiseStTime()) - get_Hour(getTime()) <= 100))
                            {

                                String promiseTitle = theDayPromiseModels.get(position).getPromiseTitle();
                                String promiseDate = theDayPromiseModels.get(position).getPromiseDate();
                                staticPromiseModel = bringPromiseMidel(promiseDate, promiseTitle);
                                ArrayList<String> memberUid = theDayPromiseModels.get(position).getMemberUids();
                                Intent MainIntent = new Intent(PromiseOfTheDayActivity.this, MainActivity.class);
                                MainIntent.putExtra("promiseDate", promiseDate);
                                MainIntent.putExtra("promiseTitle", promiseTitle);
                                MainIntent.putExtra("memberUidSize", memberUid.size());
                                MainIntent.putExtra("memberUid", memberUid);
                                PromiseOfTheDayActivity.this.startActivity(MainIntent);
                                //System.out.println("mainmainmain 약속 1시간전 실행");
                            }
                            else {
                                String promiseTitle = theDayPromiseModels.get(position).getPromiseTitle();
                                String promisePlace = theDayPromiseModels.get(position).getPromisePlace();
                                String promiseDate = theDayPromiseModels.get(position).getPromiseDate();
                                ArrayList<String> memberUid = theDayPromiseModels.get(position).getMemberUids();
                                //System.out.println("mainmainmain click " + memberUid.get(0));
                                Intent promiseRoomIntent = new Intent(PromiseOfTheDayActivity.this, PromiseRoomActivity.class);
                                promiseRoomIntent.putExtra("promiseDate", promiseDate);
                                promiseRoomIntent.putExtra("promiseTitle", promiseTitle);
                                promiseRoomIntent.putExtra("promisePlace", promisePlace);
                                promiseRoomIntent.putExtra("memberUid", memberUid);
                                PromiseOfTheDayActivity.this.startActivity(promiseRoomIntent);
                            }
                        }

                    }
                });

            }
        }
    }

    public static String getToDay()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        return sdf.format(new Date());
    }

    public String getTime()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH+mm");
        return sdf.format(new Date());
    }

    public String getStDate(String date)
    {
        String[] parseSt = date.split("\\+");
        String stDate = parseSt[0] + "년 " +parseSt[1] + "월 " + parseSt[2] + "일";

        return stDate;
    }

    public int get_Hour(String time) // 약속시간을 정수화
    {
        String[] parse1 = time.split("\\+");
        int hourInt = Integer.parseInt(parse1[0]);
        int minuteInt = Integer.parseInt(parse1[1]);

        int resultTime = hourInt*100 + minuteInt;

        return resultTime;
    }

    public static ArrayList<PromiseModel> bringPromiseMidel(final String promiseDate, final String promiseTitle)
    {
        final ArrayList<PromiseModel> promiseModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("promise")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        promiseModels.clear();
                        for(DataSnapshot item :dataSnapshot.getChildren())
                        {
                            if(promiseDate.equals(item.getValue(PromiseModel.class).getPromiseDate()) && promiseTitle.equals(item.getValue(PromiseModel.class).getPromiseTitle())) {
                                promiseModels.add(item.getValue(PromiseModel.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return promiseModels;
    }
}