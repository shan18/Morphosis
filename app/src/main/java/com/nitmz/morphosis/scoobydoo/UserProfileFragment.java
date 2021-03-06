package com.nitmz.morphosis.scoobydoo;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nitmz.morphosis.CircleImageView;
import com.nitmz.morphosis.R;


public class UserProfileFragment extends Fragment {

    FirebaseAuth mAuth;
    private FirebaseDatabase mDB;
    private DatabaseReference mScoreRef;

    CircleImageView mUserImage;
    TextView mUserScore;
    TextView mUserName;
    TextView mUserRank;
    TextView rank;
    Button Logout;
    Button Share;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leader_board_user_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseDatabase.getInstance();
        mScoreRef = mDB.getReference("users/" + mAuth.getCurrentUser().getUid()+"/score");
        Logout = (Button) view.findViewById(R.id.logout_bnav);
        Logout.setVisibility(View.VISIBLE);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ScoobyDooBNavHome) getActivity()).logout();
            }
        });
        Share = (Button) view.findViewById(R.id.share_bnav);
        Share.setVisibility(View.VISIBLE);
        Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((ScoobyDooBNavHome) getActivity()).share();

            }
        });

        mUserImage = (CircleImageView)view.findViewById(R.id.user_image_leaderboard_details);
        mUserName = (TextView)view.findViewById(R.id.user_name_input);
        mUserScore = (TextView)view.findViewById(R.id.user_score_input);
        mUserRank = (TextView)view.findViewById(R.id.user_rank_input);
        mUserRank.setVisibility(View.GONE);
        rank = (TextView) view.findViewById(R.id.user_rank_text);
        rank.setVisibility(View.GONE);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String score = dataSnapshot.getValue().toString();
                mUserScore.setText(score);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mScoreRef.addValueEventListener(listener);

        try {

            String purl = mAuth.getCurrentUser().getPhotoUrl().toString();
            purl = purl.replace("/s96-c/","/s400-c/");

            Glide.with(getContext())
                    .load(Uri.parse(purl))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .fitCenter()
                    .error(R.drawable.ic_account_circle_white_48dp)
                    .placeholder(R.drawable.ic_account_circle_white_48dp)
                    .dontAnimate()
                    .into(mUserImage);

            mUserName.setText(toTitleCase(mAuth.getCurrentUser().getDisplayName().toLowerCase().trim()));
        } catch (Exception e)
        {
            FirebaseCrash.report(new Exception("user_profile_pic_fetch_error"+" ::: "+mAuth.getCurrentUser().getUid()+":::"+e.getMessage()));
            Toast.makeText(getContext(), "Error Loading Profile pic, Please try after sometime", Toast.LENGTH_SHORT).show();

        }


    }

    // Converts a string into Title Case
    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }

        return titleCase.toString();
    }


}
