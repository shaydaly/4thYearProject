package com.carvis;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Seamus on 26/01/2017.
 */

public class Customer implements Parcelable {

    private String userID, username, firstName, surname, email, password, phoneNum, name;
    Date dateOfBirth;


    public Customer(String userID, String firstName, String surname, String email, String password, String phoneNum, Date dateOfBirth) {
        this.userID = userID;
        this.firstName = firstName;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.phoneNum = phoneNum;
        this.dateOfBirth = dateOfBirth;
    }

    public Customer(String givenName, String username, String email, String phone) {
        this.name = givenName;
        this.username = username;
        this.email = email;
        phoneNum = phone;
    }

    public Customer() {
        userID = "";
        firstName = "";
        surname = "";
        email = "";
        password = "";
        phoneNum = "";
        dateOfBirth = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void AddCustomerToDB(Context c) {
        try {
            System.out.println(("called add customer"));
            RequestQueue requestQueue = Volley.newRequestQueue(c);
            String URL = "https://8ssr60mlih.execute-api.us-east-1.amazonaws.com/Test/createcustomerobject";
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", name);
            jsonBody.put("username", username);
            jsonBody.put("email", email);
            jsonBody.put("phoneNumber", phoneNum);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //result = response;
                    Log.i("VOLLEY", response);
                    System.out.println("_________" + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //result = error.toString();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        // result = uee.toString();
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                        //result = (response.toString());
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
            //result = e.toString();
        }
    }

    private int mData;

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(username);
        out.writeString(email);
        out.writeString(phoneNum);

    }

    public static final Parcelable.Creator<Customer> CREATOR
            = new Parcelable.Creator<Customer>() {
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    private Customer(Parcel in) {
        name = in.readString();
        username=in.readString();
        email= in.readString();
        phoneNum = in.readString();
    }
}

