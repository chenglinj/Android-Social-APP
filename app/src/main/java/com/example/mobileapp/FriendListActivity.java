package com.example.mobileapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.mobileapp.models.User;
import com.example.mobileapp.models.UserData;
import com.example.mobileapp.utilities.ApiHelper;
import com.example.mobileapp.utilities.UserAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendListActivity extends AppCompatActivity {

    private List<User> friendList = new ArrayList<>();
    private String sessionkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

        Intent previous_intent = getIntent();
        sessionkey = previous_intent.getStringExtra("sessionkey");

        initFriends();
        Collections.sort(friendList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                if (o1.getName().compareTo(o2.getName()) < 0) {
                    return -1;
                }
                else if (o1.getName().compareTo(o2.getName()) > 0){
                    return 1;
                }
                return 0;
            }
        });
        UserAdapter adapter = new UserAdapter(FriendListActivity.this, R.layout.friend_list, friendList);
        ListView listView = (ListView) findViewById(R.id.friend_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User friend = friendList.get(position);
                if(friend!=null){
                    Intent intent = new Intent(FriendListActivity.this, FriendProfileActivity.class);
                    intent.putExtra("selected_friend", friend);
                    startActivityForResult(intent, 666);
                }
            }
        });

        ImageView addFriend = (ImageView) findViewById(R.id.add_friend_button);
        addFriend.setImageResource(R.drawable.plus_sign);
        addFriend.setClickable(true);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendListActivity.this, AddFriendActivity.class);
                startActivity(intent);
            }
        });

        ImageView friendRequest = (ImageView) findViewById(R.id.friend_request_button);
        friendRequest.setImageResource(R.drawable.bell);
        friendRequest.setClickable(true);
        friendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendListActivity.this, FriendRequestActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_friend_list);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }

        Intent previous_intent = getIntent();
        sessionkey = previous_intent.getStringExtra("sessionkey");

        initFriends();
        Collections.sort(friendList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                if (o1.getName().compareTo(o2.getName()) < 0) {
                    return -1;
                }
                else if (o1.getName().compareTo(o2.getName()) > 0){
                    return 1;
                }
                return 0;
            }
        });
        UserAdapter adapter = new UserAdapter(FriendListActivity.this, R.layout.friend_list, friendList);
        ListView listView = (ListView) findViewById(R.id.friend_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User friend = friendList.get(position);
                if(friend!=null){
                    Intent intent = new Intent(FriendListActivity.this, FriendProfileActivity.class);
                    intent.putExtra("selected_friend", friend);
                    startActivityForResult(intent, 666);
                }
            }
        });

        ImageView addFriend = (ImageView) findViewById(R.id.add_friend_button);
        addFriend.setImageResource(R.drawable.plus_sign);
        addFriend.setClickable(true);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendListActivity.this, AddFriendActivity.class);
                startActivity(intent);
            }
        });

        ImageView friendRequest = (ImageView) findViewById(R.id.friend_request_button);
        friendRequest.setImageResource(R.drawable.bell);
        friendRequest.setClickable(true);
        friendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendListActivity.this, FriendRequestActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initFriends() {
        try {
            Collection<User> friends = UserData.getInstance().getFriends();
            //JSONObject friends_feedback = new JSONObject(ApiHelper.friends(sessionkey));
            //JSONArray friends = friends_feedback.getJSONArray("friends");
            friendList = new ArrayList<>();
            friendList.addAll(friends);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 666) {
            if (resultCode == 777) {
                setResult(888, data);
                finish();
            }
        }
    }
}
