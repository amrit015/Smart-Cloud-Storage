package com.amrit.smartcloudstorage;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Amrit on 12/4/2017.
 * RecyclerView Adapter for displaying the group and handling the user-click on the group
 */

public class MyGroupViewAdapter extends RecyclerView.Adapter<MyGroupViewAdapter.GroupHolder> {
    private static String LOG_TAG = "MyFilesViewAdapter";
    Context context;
    CreateGroupModule createGroupModule;
    private ArrayList<CreateGroupModule> mDataset;
    private String rGroupName;
    private int position;
    private String rGroupPass;
    public static final String SHARED_PREF_NAME = "cloudLogIn";

    public MyGroupViewAdapter(GroupActivity groupActivity, ArrayList<CreateGroupModule> groupList) {
        context = groupActivity;
        this.mDataset = groupList;
    }

    @Override
    public MyGroupViewAdapter.GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_mygroup_view, parent, false);
        context = parent.getContext();
        MyGroupViewAdapter.GroupHolder groupHolder = new MyGroupViewAdapter.GroupHolder(view);
        return groupHolder;
    }

    @Override
    public void onBindViewHolder(final MyGroupViewAdapter.GroupHolder holder, final int position) {
        // binding the views to display the group name in the recyclerview
        createGroupModule = mDataset.get(position);
        if (!createGroupModule.getGroupName().equals("")) {
            rGroupName = createGroupModule.getGroupName();
            rGroupPass = createGroupModule.getGroupPass();
            holder.groupTitle.setText(rGroupName);
        }

        // handling on-click on the group view
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String checkEmail = "";
                String checkGroup = "";

                //getting the group on which the user is currently logged in
                // fetching value from sharedpreference
                SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

                String email = sharedPreferences.getString("email", "");
                String group = sharedPreferences.getString("user_group", "");

                if (email.equals(checkEmail) && group.equals(checkGroup)) {
                    Intent intent = new Intent(context, UserHomeActivity.class);
                    context.startActivity(intent);
                } else {
                    // in case the user hasn't logged in the group before
                    createGroupModule = mDataset.get(position);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    final View view = inflater.inflate(R.layout.alert_create_group, null);
                    final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Enter Group Details!");
                    alertDialog.setCancelable(false);
                    final EditText pass = view.findViewById(R.id.create_group_pass);
                    EditText name = view.findViewById(R.id.create_group_name);
                    name.setText(createGroupModule.getGroupName());
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String groupPass = pass.getText().toString();
                            if (groupPass.equals(createGroupModule.getGroupPass())) {
                                //Creating a shared preference
                                SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

                                //Creating editor to store values to shared preferences
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                //Adding values to editor
                                editor.putString("user_group", createGroupModule.getGroupName());

                                //Saving values to editor
                                editor.commit();
                                Intent intent = new Intent(context, UserHomeActivity.class);
                                context.startActivity(intent);
                            }
                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.setView(view);
                    alertDialog.show();
                }
            }

        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // for binding the individual group with the cardview
    public class GroupHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView groupTitle;

        public GroupHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            groupTitle = itemView.findViewById(R.id.group_title);
        }
    }
}
