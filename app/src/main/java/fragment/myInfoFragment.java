package fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.application.promisekeeper.Model.UserModel;
import org.application.promisekeeper.R;

public class myInfoFragment extends Fragment {

    UserModel userModel = new UserModel();
    TextView userNumber;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_info, container, false);

        userNumber = (TextView) view.findViewById(R.id.fragment_my_info_userNumber);

        FirebaseDatabase.getInstance().getReference().child("users")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item :snapshot.getChildren())
                {
                    if(item.getValue(UserModel.class).getUserUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        userModel.setUserNumberCode(item.getValue(UserModel.class).getUserNumberCode());
                    }
                }

                userNumber.setText("My User Number: " + userModel.getUserNumberCode());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}
