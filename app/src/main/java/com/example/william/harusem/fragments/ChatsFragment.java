package com.example.william.harusem.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.william.harusem.R;
import com.example.william.harusem.adapters.ChatMainAdapter;
import com.example.william.harusem.models.ChatList;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class ChatsFragment extends Fragment {


    ChatMainAdapter chatAdapter;
    ArrayList<ChatList> dataModels;
    ListView listView;
    MaterialSearchView searchView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);
        setHasOptionsMenu(true);


        dataModels = new ArrayList<>();
        dataModels.add(new ChatList(R.drawable.ic_chat_profile_photo, "Muslim", "19:01", "Ramadan Kareem"));
        dataModels.add(new ChatList(R.drawable.ic_chat_profile_photo, "Mahmoud", "17:01", "Hello"));
        dataModels.add(new ChatList(R.drawable.ic_chat_profile_photo, "Ahmad", "10:22", "Hello Mahmoud"));
        dataModels.add(new ChatList(R.drawable.ic_chat_profile_photo, "Emine", "13:51", "Ahmad"));

        chatAdapter = new ChatMainAdapter(getActivity(), dataModels);

        listView = rootView.findViewById(R.id.chat_listview);

        listView.setAdapter(chatAdapter);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Search");
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_search_menu_item, menu);
        searchView = (MaterialSearchView) getView().findViewById(R.id.search_view);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
    }
}

