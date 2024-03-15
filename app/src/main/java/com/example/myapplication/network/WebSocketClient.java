package com.example.myapplication.network;

import static com.example.myapplication.LoginActivity.TAG;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.Support.Activity;
import com.example.myapplication.Support.Machine;
import com.example.myapplication.Support.SubmitHolder;
import com.example.myapplication.Support.Task;
import com.example.myapplication.Support.User;
import com.example.myapplication.Support.UserPreferences;
import com.example.myapplication.model.ItemModel;
import com.example.myapplication.service.MyForegroundService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient extends WebSocketListener {
    WebSocket webSocket;
    ItemModel model;
    Context context;
    MyForegroundService foregroundService;
    private Timer timer;
    private final int maxRetries = 5;
    private int retryCount = 0;
    int getActivityCallCount = 0;
    private final String ipAddress= "ws://192.168.43.174:8000/chat/";
    public WebSocketClient(Context context, MyForegroundService foregroundService){
        connect();
        this.context=context;
        this.foregroundService=foregroundService;

    }
    public WebSocketClient(Context context){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder()
                .url(ipAddress)
                .build();
        webSocket=okHttpClient.newWebSocket(request,this);
        this.context=context;
    }

    public void connect(){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder()
                .url(ipAddress)
                .build();
        webSocket=okHttpClient.newWebSocket(request,this);
    }

    public void getExistingActivity(){
        getActivityCallCount++;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject=new JSONObject();
        User user=UserPreferences.getUser(context.getApplicationContext());
        if(user!=null) {
            try {
                jsonObject.put("activity", "existing");
                jsonObject.put("username", user.getUsername());
                jsonObject.put("password", user.getPassword());
                jsonObject.put("usermode",user.getUsermode());
            } catch (JSONException e) {
                Log.d(TAG, "Auth: ", e);
                ;
            }
        }else{
            Log.d(TAG, "getExistingActivity: both user is not logged in and service started ");
        }
        Log.d(TAG, "getExistingActivity: sending activity to server ");
        webSocket.send(jsonObject.toString());
    }

    public boolean Auth(String user,String password){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("username",user);
            jsonObject.put("password",password);
            jsonObject.put("auth","auth");
        } catch (JSONException e) {
            Log.d(TAG, "Auth: ",e);;
        }
        Log.d(TAG, "Auth: json converted data "+ jsonObject +" "+password+" "+user);
            return webSocket.send(jsonObject.toString());
    }
    public void getSchedules(){
        User user=UserPreferences.getUser(context.getApplicationContext());
        JSONObject jsonObject=new JSONObject();
        try {
            if (user != null) {
                jsonObject.put("username",user.getUsername());
                jsonObject.put("password",user.getPassword());
                jsonObject.put("usermode",user.getUsermode());
                jsonObject.put("schedule","schedule");
                webSocket.send(jsonObject.toString());
            }else{
                Log.d(TAG, "getSchedules: User not defined yet ");
            }
        } catch (JSONException e) {
            Log.d(TAG, "Auth: ",e);;
        }
    }
    public void CreateActivity(Activity activity){
        User user=UserPreferences.getUser(context.getApplicationContext());
        JSONObject jsonObject=new JSONObject();
        try {
            if (user != null) {
                Log.d(TAG, "CreateActivity: activity id "+activity.activityId);
                jsonObject.put("username",user.getUsername());
                jsonObject.put("password",user.getPassword());
                jsonObject.put("usermode",user.getUsermode());
                jsonObject.put("create","create");
                JSONObject activityData=new JSONObject();
                activityData.put("activity_description",activity.activityDescription);
                activityData.put("activity_name",activity.activityName);
                activityData.put("activity_issued_date",activity.issued_date);
                activityData.put("activity_machine_id",activity.machineId);
                activityData.put("activity_component_id",activity.componentId);
                activityData.put("activity_schedule_id",activity.componentId);
                activityData.put("activity_status_id",activity.activity_satuts_id);
                activityData.put("assigned_to_user",activity.assigned_to_user);
                jsonObject.put("activity",activityData);
                webSocket.send(jsonObject.toString());
            }else{
                Log.d(TAG, "getSchedules: User not defined yet ");
            }
        } catch (JSONException e) {
            Log.d(TAG, "Auth: ",e);;
        }
    }
    public void deleteActivity(Activity activity){
        User user=UserPreferences.getUser(context.getApplicationContext());
        JSONObject jsonObject=new JSONObject();
        try {
            if (user != null) {
                Log.d(TAG, "updateActivity: activity id before sending is "+activity.activityId);
                jsonObject.put("username",user.getUsername());
                jsonObject.put("password",user.getPassword());
                jsonObject.put("usermode",user.getUsermode());
                jsonObject.put("delete","delete");
                JSONObject activityData=new JSONObject();
                activityData.put("activity_id",activity.activityId);
                Log.d(TAG, "updateActivity: activityData "+activityData.getString("activity_id"));
                activityData.put("activity_description",activity.activityDescription);
                activityData.put("activity_name",activity.activityName);
                activityData.put("activity_issued_date",activity.issued_date);
                activityData.put("activity_machine_id",activity.machineId);
                activityData.put("activity_component_id",activity.componentId);
                activityData.put("activity_schedule_id",activity.componentId);
                activityData.put("activity_status_id",activity.activity_satuts_id);
                activityData.put("activity_assigned_to",activity.assigned_to);
                activityData.put("assigned_to_user",activity.assigned_to_user);
                activityData.put("change","delete");
                jsonObject.put("activity",activityData);
                webSocket.send(jsonObject.toString());
            }else{
                Log.d(TAG, "getSchedules: User not defined yet ");
            }
        } catch (JSONException e) {
            Log.d(TAG, "Auth: ",e);;
        }
    }
    public void updateActivity(Activity activity){
        User user=UserPreferences.getUser(context.getApplicationContext());
        JSONObject jsonObject=new JSONObject();
        try {
            if (user != null) {
                Log.d(TAG, "updateActivity: activity id before sending is "+activity.activityId);
                jsonObject.put("username",user.getUsername());
                jsonObject.put("password",user.getPassword());
                jsonObject.put("usermode",user.getUsermode());
                jsonObject.put("update","update");
                JSONObject activityData=new JSONObject();
                activityData.put("activity_id",activity.activityId);
                Log.d(TAG, "updateActivity: activityData "+activityData.getString("activity_id"));
                activityData.put("activity_description",activity.activityDescription);
                activityData.put("activity_name",activity.activityName);
                activityData.put("activity_issued_date",activity.issued_date);
                activityData.put("activity_machine_id",activity.machineId);
                activityData.put("activity_component_id",activity.componentId);
                activityData.put("activity_schedule_id",activity.componentId);
                activityData.put("activity_status_id",activity.activity_satuts_id);
                activityData.put("activity_assigned_to",activity.assigned_to);
                activityData.put("assigned_to_user",activity.assigned_to_user);
                jsonObject.put("activity",activityData);
                webSocket.send(jsonObject.toString());
            }else{
                Log.d(TAG, "getSchedules: User not defined yet ");
            }
        } catch (JSONException e) {
            Log.d(TAG, "Auth: ",e);;
        }
    }

    public void getComponents(){
        User user=UserPreferences.getUser(context.getApplicationContext());
        JSONObject jsonObject=new JSONObject();
        try {
            if (user != null) {
                jsonObject.put("username",user.getUsername());
                jsonObject.put("password",user.getPassword());
                jsonObject.put("usermode",user.getUsermode());
                jsonObject.put("component","component");
                webSocket.send(jsonObject.toString());
            }else{
                Log.d(TAG, "getSchedules: User not defined yet ");
            }
        } catch (JSONException e) {
            Log.d(TAG, "Auth: ",e);;
        }
    }

    public void getMachines(){
        Log.d(TAG, "getMachines: sending request from machine ");
        User user=UserPreferences.getUser(context.getApplicationContext());
        JSONObject jsonObject=new JSONObject();
        try {
            if (user != null) {
                jsonObject.put("username",user.getUsername());
                jsonObject.put("password",user.getPassword());
                jsonObject.put("usermode",user.getUsermode());
                jsonObject.put("machine","machine");
                webSocket.send(jsonObject.toString());
            }else{
                Log.d(TAG, "getSchedules: User not defined yet ");
            }
        } catch (JSONException e) {
            Log.d(TAG, "Auth: ",e);;
        }
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        User user=UserPreferences.getUser(context.getApplicationContext());
        if(user!=null){
            Log.d(TAG, "onOpen: no need for authentication already signed in ");
                getExistingActivity();
        }else{
            if(foregroundService!=null) {
                Log.d(TAG, "onOpen: no need for authentication stopping service ");
                foregroundService.stopForeground(true);
                foregroundService.stopSelf();
            }
        }
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
        Log.d(TAG, "onMessage: message on the bytes "+bytes.base64());
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        Log.d(TAG, "onMessage: "+text);
        if(text.contains("username")&&text.contains("password")){
            Log.d(TAG, "onMessage: edited "+text);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(text);
                String key1 = jsonObject.getString("username");
                String key2 = jsonObject.getString("password");
                int key3 = jsonObject.getInt("user_id");
                String usermode=jsonObject.getString("usermode");
                Log.d(TAG, "onMessage: the converted values are "+key1+" "+key2+" "+usermode);
                User user=new User(key1,key2,usermode);
                user.setUser_id(key3);
                Log.d(TAG, "onMessage: functioning ");
                LoginActivity loginActivity=(LoginActivity)context;
                loginActivity.NextPage(user);
                Log.d(TAG, "onMessage: updating " );
            } catch (JSONException e) {
                Log.d(TAG, "onMessage: exception occuered while converting to json ");
                e.printStackTrace();
            }
        }else if(text.contains("activity_id")&&!text.contains("message")){
            if(text.contains("task_id")){
                Log.d(TAG, "onMessage: edited " + text);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(text);
                    int activityId = jsonObject.getInt("activity_id");
                    int task_id = jsonObject.getInt("task_id");
                    String assignedBy = jsonObject.getString("activity_assigned_by");
                    String activityName = jsonObject.getString("activity_name");
                    String activityDescription = jsonObject.getString("activity_description");
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date=sdf.parse(jsonObject.getString("activity_issued"));
                    Task task=new Task();
                    task.setActivity_id(activityId);
                    task.setTask_id(task_id);
                    task.setActivityCreator(assignedBy);
                    task.setActivityName(activityName);
                    task.setIssuedDate(date);
                    task.setActivityDescrption(activityDescription);
                    foregroundService.setTask(task);
                } catch (JSONException e) {
                    Log.d(TAG, "onMessage: exception occuered while converting to json ");
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else {
                Log.d(TAG, "onMessage: edited " + text);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(text);
                    int activityId = jsonObject.getInt("activity_id");
                    String activityDescrption = jsonObject.getString("activity_descrption");
                    int actvity_status_id = jsonObject.getInt("actvity_status_id");
                    int componentId = jsonObject.getInt("activity_component_id");
                    int machineId = jsonObject.getInt("activity_machine_id");
                    int scheduleId = jsonObject.getInt("activity_schedule_id");
                    String activityName = jsonObject.getString("activity_name");
                    String issued_date = jsonObject.getString("activity_issued_date");
                    int activityAssignedId = jsonObject.getInt("assigned_to");
                    String activityAssignUser = jsonObject.getString("assigned_to_user");
                    String ui_activityChange = jsonObject.getString("ui_change");
                    String activityChange = jsonObject.getString("change");
                    String activityCreator = jsonObject.getString("activity_creator");
                    Activity activity = new Activity();
                    activity.activityId = activityId;
                    activity.activityDescription = activityDescrption;
                    activity.activity_satuts_id = actvity_status_id;
                    activity.componentId = componentId;
                    activity.machineId = machineId;
                    activity.scheduleId = scheduleId;
                    activity.activityName = activityName;
                    activity.issued_date = issued_date;
                    activity.assigned_to = activityAssignedId;
                    activity.assigned_to_user = activityAssignUser;
                    activity.activityCreator = activityCreator;
                    activity.change = activityChange;
                    activity.uiChange = ui_activityChange;
                    Log.d(TAG, "onMessage: functioning activity received " + activityName + " " + activityAssignedId);
                    Log.d(TAG, "onMessage: activity changes " + ui_activityChange + " " + activityChange);
                    Log.d(TAG, "onMessage: activity changes " + ui_activityChange + " " + activityChange);
                    foregroundService.setExistingActivity(activity);
                    Log.d(TAG, "onMessage: updating ");
                } catch (JSONException e) {
                    Log.d(TAG, "onMessage: exception occuered while converting to json ");
                    e.printStackTrace();
                }
            }
        }else if(text.contains("message") && text.contains("activity_id") && text.contains("create")||text.contains("update")){
            try{
            // Parse the JSON string into a JSONObject
                JSONObject jsonObject = new JSONObject(text);
                // Extract values from the JSONObject
                String type = jsonObject.getString("type");
                String message = jsonObject.getString("message");
                Log.d(TAG, "onMessage: message part of activity create"+message);
                // Parse the message JSON string into another JSONObject
                JSONObject messageObject = new JSONObject(message);
                // Extract values from the message JSONObject
                int activityId = messageObject.getInt("activity_id");
                String activityDescription = messageObject.getString("activity_descrption");
                int activityStatusId = messageObject.getInt("actvity_status_id");
                int activityComponentId = messageObject.getInt("activity_component_id");
                int activityMachineId = messageObject.getInt("activity_machine_id");
                int activityScheduleId = messageObject.getInt("activity_schedule_id");
                String activityName = messageObject.getString("activity_name");
                String activityIssuedDate = messageObject.getString("activity_issued_date");
                String activityChange = messageObject.getString("change");
                String activityAssigned= messageObject.getString("assigned_to_user");
                String ui_activityChange = messageObject.getString("ui_change");
                int activityAssignedId = messageObject.getInt("assigned_to_id");
                String activityCreator = messageObject.getString("activity_creator");
                Activity activity=new Activity();
                activity.activityName=activityName;
                activity.activityDescription=activityDescription;
                activity.activity_satuts_id=activityStatusId;
                activity.componentId=activityComponentId;
                activity.machineId=activityMachineId;
                activity.scheduleId=activityScheduleId;
                activity.issued_date=activityIssuedDate;
                activity.assigned_to=activityAssignedId;
                activity.assigned_to_user=activityAssigned;
                activity.activityCreator=activityCreator;
                activity.change=activityChange;
                activity.uiChange=ui_activityChange;
                activity.activityId=activityId;
                Log.d(TAG, "onMessage: activity changes "+activityAssignedId+" "+activityChange+" "+activity.activityId);
                foregroundService.setExistingActivity(activity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (text.contains("type")) {
            try {
                JSONObject jsonObject=new JSONObject(text);
                int id=jsonObject.getInt("id");
                String type=jsonObject.getString("type");
                String name=jsonObject.getString("name");
                if(type.equals("machine")){
                    Machine machine=new Machine();
                    machine.setName(name);
                    machine.setId(id);
                    foregroundService.setMachine(machine,false);
                } else if (type.equals("component")) {
                    Machine component=new Machine();
                    component.setId(id);
                    component.setName(name);
                    foregroundService.setComponent(component,false);
                } else if (type.equals("schedule")) {
                    Log.d(TAG, "onMessage: schedule object got from the server ");
                    Machine schedule=new Machine();
                    schedule.setId(id);
                    schedule.setName(name);
                    schedule.setValue(jsonObject.getInt("value"));
                    schedule.setCurrentvalue(jsonObject.getInt("current"));
                    foregroundService.setSchedule(schedule,false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (text.contains("username")&&text.contains("userid")) {
            try {
                JSONObject jsonObject=new JSONObject(text);
                String username=jsonObject.getString("username");
                String key=jsonObject.getString("key");
                int user_id=jsonObject.getInt("userid");
                User user=new User(username,"password","B");
                user.setUser_id(user_id);
                Log.d(TAG, "onMessage: "+key+" "+user.getUsername());
                foregroundService.setUsers(user,key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (text.contains("callback")&& text.contains("model")) {
            try {
                JSONObject jsonObject=new JSONObject(text);
                String callback=jsonObject.getString("callback");
                String model=jsonObject.getString("model");
                Log.d(TAG, "onMessage: callback and model "+callback+" "+model);
                if(callback.equals("created")){
                    Log.d(TAG, "onMessage: callback is "+callback);
                    foregroundService.updateUi(callback,model);
                } else if (callback.equals("update")) {
                    foregroundService.updateUi(callback,model);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    public void getUsers(CharSequence sequence){
        User user=UserPreferences.getUser(context.getApplicationContext());
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("username",user.getUsername());
            jsonObject.put("password",user.getPassword());
            jsonObject.put("usermode",user.getUsermode());
            jsonObject.put("users",sequence);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webSocket.send(jsonObject.toString());
    }
    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        Log.d(TAG, "onClosed: websocket closed connection ");
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
        Log.e(TAG, "onFailure: ", t);
        scheduleReconnect();
    }
    private void scheduleReconnect() {
        if (retryCount < maxRetries) {
            retryCount++;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(TAG, "Retrying connection...");
                    connect();
                }
            }, 5000); // Retry after 5 seconds
        } else {
            Log.e(TAG, "Max retries reached. Unable to connect to server.Stopping service so it may initialed on restart of app");
            // Perform any actions needed after reaching maximum retries
            foregroundService.stopForeground(true);
            foregroundService.stopSelf();
        }
    }

    public void getTask() {
        Log.d(TAG, "getTask: getting task from the server...");
        User user=UserPreferences.getUser(context.getApplicationContext());
        JSONObject jsonObject=new JSONObject();
        try {
            if (user != null) {
                jsonObject.put("username",user.getUsername());
                jsonObject.put("password",user.getPassword());
                jsonObject.put("usermode",user.getUsermode());
                jsonObject.put("user_id",user.getUser_id());
                jsonObject.put("task","task");
                webSocket.send(jsonObject.toString());
            }else{
                Log.d(TAG, "getSchedules: User not defined yet ");
            }
        } catch (JSONException e) {
            Log.d(TAG, "Auth: ",e);;
        }
    }

    public void sendReport(ArrayList<SubmitHolder> submitHolders) {
        if(submitHolders.size()>0){
            for (SubmitHolder s :
                    submitHolders) {
                Log.d(TAG, "sendReport: sending text ....");
                if(!s.getTextView().equals(""))
                    webSocket.send(s.getTextView());
                Log.d(TAG, "sendReport: sending audio ....");
                if(s.getFile()!=null){
                    try {
                        FileInputStream fileInputStream=new FileInputStream(s.getFile());
                        byte[] buf =new byte[1024];
                        int len=0;
                        while ((len=fileInputStream.read(buf))!=-1){
                            ByteBuffer byteBuffer=ByteBuffer.wrap(buf);
                            webSocket.send(new ByteString(buf));
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "sendReport: sending image ....");
                if(s.getImageFile()!=null){
                    try {
                        FileInputStream fileInputStream=new FileInputStream(s.getImageFile().getPath());
                        byte[] buf =new byte[1024];
                        int len=0;
                        while ((len=fileInputStream.read(buf))!=-1){
                            ByteBuffer byteBuffer=ByteBuffer.wrap(buf);
                            webSocket.send(new ByteString(buf));
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
