package com.example.fypwebhost;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinedClasses extends Fragment {


    ListView listView;
    JoinedClassesAdapter adapter;
    ProgressBar progressBar;
    String userEmail, userIdOld, userName, userPassword;;
    JoinedClassesModel joinedClassesModel;
    public static ArrayList<JoinedClassesModel> joinedClassesArrayList = new ArrayList<>();

    public JoinedClasses(String email, String userIdOld, String userName, String userPassword) {

        this.userEmail = email;
        this.userIdOld = userIdOld;
        this.userName = userName;
        this.userPassword = userPassword;
    }


    public static String URL="https://temp321.000webhostapp.com/connect/getJoinedClass.php";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.joined_class_frgment, container, false);
        listView = view.findViewById(R.id.mylistview);

        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        Sprite circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);
        retrieveData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                joinedClassesModel = joinedClassesArrayList.get(position);
                //Toast.makeText(getContext(), "Class_id"+joinedClassesModel.getId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), CurrentClass.class);

                intent.putExtra("Class_name", joinedClassesModel.getName());
                intent.putExtra("Class_id", joinedClassesModel.getId());
                intent.putExtra("classID", joinedClassesModel.getClassID());
                //intent.putExtra("userType", type);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                joinedClassesModel = joinedClassesArrayList.get(position);

                showLeaveDialog(userIdOld, joinedClassesModel.getClassID());
                return true;
            }
        });
        return view;
    }


    public void retrieveData() {

        progressBar.setVisibility(View.INVISIBLE);
        joinedClassesArrayList.clear();

        final String userId = userIdOld;

        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                           // //Toast.makeText(getActivity(), "doing it 1", Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject=new JSONObject(response);
                            String sucess = jsonObject.getString("success");
                            JSONArray jsonArray=jsonObject.getJSONArray("data");
                           // //Toast.makeText(getContext(), sucess, Toast.LENGTH_SHORT).show();
                            if(sucess.contains("1")){

                                progressBar.setVisibility(View.INVISIBLE);
                                //Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                                for(int i=0; i< jsonArray.length(); i++){
                                    if(sucess.equals("1")){
                                        JSONObject object=jsonArray.getJSONObject(i);
                                        String classID = object.getString("classID");
                                        String classCode = object.getString("classCode");
                                        String className=object.getString("class");
                                        String classSubject=object.getString("subject");
                                        joinedClassesArrayList.add(
                                                new JoinedClassesModel(classCode, className, classSubject, classID)
                                        );
                                    }
                                    adapter = new JoinedClassesAdapter(getContext() ,joinedClassesArrayList);
                                    adapter.notifyDataSetChanged();
                                    listView.setAdapter(adapter);
                                }
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("classTeacher", userId);
                return params;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }



    private void showLeaveDialog(final String studentIDR, final String classID)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.leave_class_dialog,null);

        dialogBuilder.setView(dialogView);


        final Button buttonLeaveClass = (Button) dialogView.findViewById(R.id.buttonLeaveClass);

        dialogBuilder.setTitle("leave class:");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonLeaveClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                StringRequest request = new StringRequest(Request.Method.POST, "https://temp321.000webhostapp.com/connect/leaveClass.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.contains("Class leaved"))
                                {
                                    Toast.makeText(getActivity(), "Class leaved", Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                ){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String > params = new HashMap<String, String>();

                        params.put("studentID", studentIDR);
                        params.put("classID", classID);

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                requestQueue.add(request);








                alertDialog.dismiss();
            }
        });


    }
}
