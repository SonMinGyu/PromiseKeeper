package org.application.promisekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class CreatePromise extends Activity {

    EditText titleEditText;
    Spinner hourSpinner;
    Spinner minuteSpinner;
    Button createButton;
    RecyclerView recyclerView;
    int hour;
    int minute;

    ArrayList<InviteCheckModel> inviteCheckModels = new ArrayList<>(); // 약속에 참여하는 맴버 검사 uid저장
    ArrayList<String> stringInviteCheckModels = new ArrayList<>(); // 약속에 참여하는 맴버 uid저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_promise);

        titleEditText = (EditText) findViewById(R.id.create_promise_promiseTitle);
        hourSpinner = (Spinner) findViewById(R.id.create_promise_hourSpinner);
        minuteSpinner = (Spinner) findViewById(R.id.create_promise_minuteSpinner);
        createButton = (Button) findViewById(R.id.create_promise_createButton);

        recyclerView = (RecyclerView) findViewById(R.id.create_promise_recyclerView);
        recyclerView.setAdapter(new selectFriendsRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        hourSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItemPosition() == 0)
                {
                    hour = -1;
                }
                else
                {
                    hour = adapterView.getSelectedItemPosition() - 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        minuteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getSelectedItemPosition() == 0)
                {
                    minute = -1;
                }
                else
                {
                    minute = (adapterView.getSelectedItemPosition() - 1) * 10;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /////////////////////////////////////////// 빈칸일 경우 처리하기

                if(hour == -1 || minute == -1)
                {
                    Toast.makeText(getApplicationContext(), "시간을 설정해 주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                PromiseModel promiseModel = new PromiseModel();
                promiseModel.setPromiseTitle(titleEditText.getText().toString());
                String time = Integer.toString(hour) +  "시 " + Integer.toString(minute) + "분";
                String stTime = Integer.toString(hour) +  "+" + Integer.toString(minute);
                promiseModel.setPromiseTime(time);
                promiseModel.setPromiseStTime(stTime);
                promiseModel.setPromiseDate(getIntent().getExtras().getString("selectDate2"));
                promiseModel.setPromiseHostUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

                for(int i = 0; i < inviteCheckModels.size(); i++)
                {
                    if(inviteCheckModels.get(i).isCheck())
                    {
                        stringInviteCheckModels.add(inviteCheckModels.get(i).getUid());
                    }
                }
                stringInviteCheckModels.add(FirebaseAuth.getInstance().getCurrentUser().getUid());

                promiseModel.setMemberUids(stringInviteCheckModels);
                promiseModel.setPromisePlace("아직 약속 장소를 정하지 않았습니다!");

                FirebaseDatabase.getInstance().getReference().child("promise")
                        .child(getIntent().getExtras().getString("selectDate2") + "+" + promiseModel.getPromiseTitle())
                        .setValue(promiseModel);

                /*
                for(int i = 0; i < stringInviteCheckModels.size(); i++)
                {
                    FirebaseDatabase.getInstance().getReference().child("promise").child(stringInviteCheckModels.get(i))
                            .child(getIntent().getExtras().getString("selectDate2") + "+" + promiseModel.getPromiseTitle())
                            .setValue(promiseModel);
                }

                 */
                finish(); // 일단은 꺼지도록 설정정
            }
       });

    }

    class selectFriendsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<UserModel> selectFriendsModels = new ArrayList<>();

        public selectFriendsRecyclerViewAdapter() {

            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("friends").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    selectFriendsModels.clear();
                    for(DataSnapshot item :dataSnapshot.getChildren())
                    {
                        selectFriendsModels.add(item.getValue(UserModel.class));
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_create_promise, parent, false);

            CreatePromise.selectFriendsRecyclerViewAdapter.CustomViewHolder holder = new CreatePromise.selectFriendsRecyclerViewAdapter.CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final CreatePromise.selectFriendsRecyclerViewAdapter.CustomViewHolder customViewHolder = (CreatePromise.selectFriendsRecyclerViewAdapter.CustomViewHolder) holder;

            /*
            Glide.with(customViewHolder.itemView.getContext())
                    .load(studyModels.get(position).getProfile())
                    .error(R.drawable.studyroom_logo)
                    .into(((CustomViewHolder)holder).imageView);

             */

            customViewHolder.friendName.setText(selectFriendsModels.get(position).getUserName());
        }

        @Override
        public int getItemCount() {
            return (selectFriendsModels != null ? selectFriendsModels.size() : 1);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView friendName;
            public CheckBox inviteCheckBox;

            public CustomViewHolder(View view) {
                super(view);
                imageView =(ImageView) view.findViewById(R.id.item_create_promise_imageView);
                friendName = (TextView) view.findViewById(R.id.item_create_promise_nameText);
                inviteCheckBox = (CheckBox) view.findViewById(R.id.item_create_promise_checkBox);

                inviteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(inviteCheckBox.isChecked())
                        {
                            InviteCheckModel inviteCheckModel = new InviteCheckModel();
                            inviteCheckModel.setCheck(true);
                            inviteCheckModel.setPosition(getAdapterPosition());
                            inviteCheckModel.setUid(selectFriendsModels.get(getAdapterPosition()).getUserUid());

                            inviteCheckModels.add(inviteCheckModel);
                        }
                        else if(!inviteCheckBox.isChecked())
                        {
                            for(int i = 0; i < inviteCheckModels.size(); i++)
                            {
                                if(inviteCheckModels.get(i).getPosition() == getAdapterPosition())
                                {
                                    inviteCheckModels.get(i).setCheck(false);
                                }
                            }
                        }
                    }
                });

                /*
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            String studyKey = keyList.get(position);
                            Log.d(TAG, keyList.get(position));
                            Intent intent = new Intent(getActivity(), StudyRoomActivity.class);
                            intent.putExtra("studykey", studyKey);
                            getActivity().startActivity(intent);
                            //Toast.makeText(getContext(),"클릭",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                 */
            }
        }
    }

    public static String getToDay()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy+MM+dd+HH+mm+ss");
        return sdf.format(new Date());
    }
}