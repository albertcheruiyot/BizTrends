package com.albert.biztrends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindMyTrenderActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ImageButton searchButton;
    private EditText searchInputText;

    private RecyclerView searchResultList;

    private DatabaseReference allUserDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_my_trender);

        mToolbar = findViewById(R.id.find_trenders_appbar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Search Trends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        allUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        searchResultList = findViewById(R.id.search_result_list);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(FindMyTrenderActivity.this));

        searchButton = findViewById(R.id.search_trenders);
        searchInputText = findViewById(R.id.search_box_input);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchBoxInput = searchInputText.getText().toString();

                searchPeople(searchBoxInput);
            }
        });
    }

    private void searchPeople(String searchBoxInput)
    {
        Toast.makeText(FindMyTrenderActivity.this, "Searching...", Toast.LENGTH_LONG).show();

        Query searchPeopleQuery = allUserDatabaseRef.orderByChild("fullname")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");

        FirebaseRecyclerOptions<FindTrends> options =
                new FirebaseRecyclerOptions.Builder<FindTrends>()
                        .setQuery(searchPeopleQuery, FindTrends.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FindTrends, FindFriendsViewHolder>(options) {
            @Override
            public FindMyTrenderActivity.FindFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_user_display_layout, parent, false);

                return new FindFriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull FindTrends model) {
                // Bind the image_details object to the BlogViewHolder
                // ...
                holder.setFullname(model.getFullname());
                holder.setStatus(model.getStatus());
                holder.setImg(getApplicationContext(),model.getImg());


            }
        };
        firebaseRecyclerAdapter.startListening();
        searchResultList.setAdapter(firebaseRecyclerAdapter);

        /*FirebaseRecyclerAdapter<FindTrends,FindFriendsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<FindTrends, FindFriendsViewHolder>(

                        FindTrends.class,
                R.layout.all_user_display_layout,
                FindFriendsViewHolder.class,
                allUserDatabaseRef

        ) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position, @NonNull FindTrends model) {
                holder.setFullname(model.getFullname());
                holder.setStatus(model.getStatus());
                holder.setImg(getApplicationContext(),model.getImg());

            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }
        };
        searchResultList.setAdapter(firebaseRecyclerAdapter);*/




    }
    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FindFriendsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mView = itemView;
        }
        public void setImg(Context ctx,String img){
            CircleImageView myImage = mView.findViewById(R.id.all_user_profile_image);
            Picasso.with(ctx).load(img).placeholder(R.drawable.profile).into(myImage);
        }
        public void setFullname(String fullname) {
            TextView myName = mView.findViewById(R.id.all_users_prfile_name);
            myName.setText(fullname);
        }
        public void setStatus(String status) {
            TextView myStatus = mView.findViewById(R.id.all_users_status);
            myStatus.setText(status);
        }
    }
}