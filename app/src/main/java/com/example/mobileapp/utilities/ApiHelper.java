package com.example.mobileapp.utilities;

import android.os.StrictMode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
public class ApiHelper {
    /** This class is designed for call web api and get the result,
     *  The hard code URL is our server api address
     *  15 function correspond to our 15 apis
     */

    private static final String BASE_URL = "http://104.207.132.11/api/mobile/";

    public static String generalCall(String urlStr, String[] keys, Object[] values){
        try{
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            URL url = new URL(urlStr);HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(2000);
            connection.setRequestMethod("POST");

            StringBuilder dataBuilder = new StringBuilder();
            for(int i = 0; i< keys.length; i++){
                dataBuilder.append(keys[i]);
                dataBuilder.append("=");
                dataBuilder.append(values[i]);
                if(i<keys.length-1){
                    dataBuilder.append("&");
                }
            }
            String data = dataBuilder.toString();
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", data.length()+"");

            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(data.getBytes());

            int responseCode = connection.getResponseCode();
            if(responseCode ==200){
                System.out.println("success");

                InputStream is = new BufferedInputStream(connection.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String inputLine = "";
                StringBuffer sb = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                String str = sb.toString();
                return str;

            }else {
                System.out.println("failed");
                return null;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    // 1
    public static String register(String loginId, String password, String name, int avatar, String info){
        String urlStr = BASE_URL + "register";
        String[] keys = {"loginId","password","name","avatar","info"};
        Object[] values = {loginId, password, name, avatar, info};
        return generalCall(urlStr, keys, values);
    }
    // 2
    public static String login(String loginId, String password){
        String urlStr = BASE_URL + "login";
        String[] keys = {"loginId","password"};
        Object[] values = {loginId, password};
        return generalCall(urlStr, keys, values);
    }
    // 3
    public static String self(String sessionKey){
        String urlStr = BASE_URL + "self";
        String[] keys = {"sessionKey"};
        Object[] values = {sessionKey};
        return generalCall(urlStr, keys, values);
    }
    // 4
    public static String logout(String sessionKey){
        String urlStr = BASE_URL + "logout";
        String[] keys = {"sessionKey"};
        Object[] values = {sessionKey};
        return generalCall(urlStr, keys, values);
    }
    // 5
    public static String friends(String sessionKey){
        String urlStr = BASE_URL + "friends";
        String[] keys = {"sessionKey"};
        Object[] values = {sessionKey};
        return generalCall(urlStr, keys, values);
    }
    // 6
    public static String friendStates(String sessionKey){
        String urlStr = BASE_URL + "friendStates";
        String[] keys = {"sessionKey"};
        Object[] values = {sessionKey};
        return generalCall(urlStr, keys, values);
    }
    // 7
    public static String search(String sessionKey, String query){
        String urlStr = BASE_URL + "search";
        String[] keys = {"sessionKey", "query"};
        Object[] values = {sessionKey, query};
        return generalCall(urlStr, keys, values);
    }
    // 8
    public static String updateProfile(String sessionKey, String name, int avatar, String info){
        String urlStr = BASE_URL + "updateProfile";
        String[] keys = {"sessionKey","name","avatar","info"};
        Object[] values = {sessionKey, name, avatar, info};
        return generalCall(urlStr, keys, values);
    }
    // 9
    public static String updateState(String sessionKey, double lat, double lng, int state){
        String urlStr = BASE_URL + "updateState";
        String[] keys = {"sessionKey","lat","lng","state"};
        Object[] values = {sessionKey, lat, lng, state};
        return generalCall(urlStr, keys, values);
    }
    // 10
    public static String addFriend(String sessionKey, String toId, String message){
        String urlStr = BASE_URL + "addFriend";
        String[] keys = {"sessionKey","toId","message"};
        Object[] values = {sessionKey, toId, message};
        return generalCall(urlStr, keys, values);
    }
    // 11
    public static  String processRequest(String sessionKey, String toId, boolean process) {
        String urlStr = BASE_URL + "processRequest";
        String[] keys = {"sessionKey","toId","process"};
        Object[] values = {sessionKey, toId, process};
        return generalCall(urlStr, keys, values);
    }
    // 12
    public static String sendMessage(String sessionKey, String toId, String message) {
        String urlStr = BASE_URL + "sendMessage";
        String[] keys = {"sessionKey", "toId", "message"};
        Object[] values = {sessionKey, toId, message};
        return generalCall(urlStr, keys, values);
    }
    // 13
    public static String getMessages(String sessionKey) {
        String urlStr = BASE_URL + "getMessages";
        String[] keys = {"sessionKey"};
        Object[] values = {sessionKey};
        return generalCall(urlStr, keys, values);
    }
    // 14
    public static String getRequests(String sessionKey) {
        String urlStr = BASE_URL + "getRequests";
        String[] keys = {"sessionKey"};
        Object[] values = {sessionKey};
        return generalCall(urlStr, keys, values);
    }
    // 15
    public static String changePassword(String sessionKey, String newPassword){
        String urlStr = BASE_URL + "changePassword";
        String[] keys = {"sessionKey", "newPassword"};
        Object[] values = {sessionKey, newPassword};
        return generalCall(urlStr, keys, values);
    }

}
