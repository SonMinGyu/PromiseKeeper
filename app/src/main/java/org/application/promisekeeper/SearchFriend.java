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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.application.promisekeeper.Model.UserModel;

import java.util.ArrayList;
import java.util.List;

import static org.application.promisekeeper.MainActivity.bringUserName;
import static org.application.promisekeeper.MainActivity.userModels;

public class SearchFriend extends Activity {

    EditText keywordText;
    Button searchButton;
    int friendKeyword;

    RecyclerView recyclerView;
    boolean alreadyFriend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friend);

        keywordText = (EditText) findViewById(R.id.search_friend_keywordText);
        searchButton = (Button) findViewById(R.id.search_friend_searchButton);

        recyclerView = (RecyclerView) findViewById(R.id.search_friend_recyclerView);
        recyclerView.setAdapter(new searchRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(keywordText.getText().length() <= 0))
                {
                    String stFriendKeyword = keywordText.getText().toString();
                    friendKeyword = Integer.parseInt(stFriendKeyword);
                }
                else
                {
                    friendKeyword = -1;
                }

                recyclerView.setAdapter(new searchRecyclerViewAdapter());
            }
        });

    }

    class searchRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private List<UserModel> findFriend = new ArrayList<>();

        public searchRecyclerViewAdapter() {
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    findFriend.clear();
                    for(DataSnapshot item :dataSnapshot.getChildren())
                    {
                        if(item.getValue(UserModel.class).getUserNumberCode() == friendKeyword
                                && !item.getValue(UserModel.class).getUserUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        {
                            findFriend.add(item.getValue(UserModel.class));
                        }
                        //findFriend.add(item.getValue(UserModel.class));
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_friend, parent, false);

            searchRecyclerViewAdapter.CustomViewHolder holder = new searchRecyclerViewAdapter.CustomViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            final searchRecyclerViewAdapter.CustomViewHolder customViewHolder = (searchRecyclerViewAdapter.CustomViewHolder) holder;

            /*
            Glide.with(customViewHolder.itemView.getContext())
                    .load(studyModels.get(position).getProfile())
                    .error(R.drawable.studyroom_logo)
                    .into(((CustomViewHolder)holder).imageView);

             */

            customViewHolder.friendName.setText(findFriend.get(position).getUserName());
        }

        @Override
        public int getItemCount() {
            return (findFriend != null ? findFriend.size() : 1);
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView friendName;
            public Button addFriendButton;

            public CustomViewHolder(View view) {
                super(view);
                imageView =(ImageView) view.findViewById(R.id.item_add_friend_imageView);
                friendName = (TextView) view.findViewById(R.id.item_add_friend_nameText);
                addFriendButton = (Button) view.findViewById(R.id.item_add_friend_button);

                addFriendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            //final ArrayList<UserModel> allFriends = new ArrayList<>();
                            alreadyFriend = false;
                            FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //allFriends.clear();
                                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                                        //allFriends.add(item.getValue(UserModel.class));
                                        if(item.getValue(UserModel.class).getUserUid().equals(findFriend.get(position).getUserUid()))
                                        {
                                            alreadyFriend = true;
                                        }
                                    }

                                    if(!alreadyFriend)
                                    {
                                        for(int i = 0; i < userModels.size(); i++)
                                        {
                                            if(userModels.get(i).getUserUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                            {
                                                UserModel friendUser = new UserModel();
                                                friendUser.setUserUid(findFriend.get(position).getUserUid());
                                                friendUser.setUserEmail(findFriend.get(position).getUserEmail());
                                                friendUser.setUserPassword(findFriend.get(position).getUserPassword());
                                                friendUser.setUserName(findFriend.get(position).getUserName());
                                                friendUser.setUserNumberCode(findFriend.get(position).getUserNumberCode());

                                                FirebaseDatabase.getInstance().getReference().child("users")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("friends").push().setValue(friendUser);

                                                Toast.makeText(getApplicationContext(), "친구 추가 되었습니다!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), "이미 추가한 친구입니다!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            /*
                            for(int i = 0; i < userModels.size(); i++)
                            {
                                if(userModels.get(i).getUserUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                {
                                    UserModel friendUser = new UserModel();
                                    friendUser.setUserUid(findFriend.get(position).getUserUid());
                                    friendUser.setUserEmail(findFriend.get(position).getUserEmail());
                                    friendUser.setUserPassword(findFriend.get(position).getUserPassword());
                                    friendUser.setUserName(findFriend.get(position).getUserName());
                                    friendUser.setUserNumberCode(findFriend.get(position).getUserNumberCode());

                                    FirebaseDatabase.getInstance().getReference().child("users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("friends").push().setValue(friendUser);

                                    Toast.makeText(getApplicationContext(), "친구 추가 되었습니다!", Toast.LENGTH_SHORT).show();
                                }
                            }

                             */
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
}