package com.example.seamus.carvis;

import java.util.Date;

/**
 * Created by Seamus on 26/01/2017.
 */

public class Customer {

    private String userID,firstName,surname,email,password,phoneNum;
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

    public  Customer(){
        userID="";
        firstName ="";
        surname="";
        email="";
        password="";
        phoneNum="";
        dateOfBirth=null;
    }

    public String getUserID() {
        return userID;
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

    public void AddCustomerToDB(){

    }
}
