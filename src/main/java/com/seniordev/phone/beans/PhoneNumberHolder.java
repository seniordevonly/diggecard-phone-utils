package com.seniordev.phone.beans;

/**
 * User: tha022
 * Date: 1/28/13
 * Time: 11:38 AM
 */
public class PhoneNumberHolder {

    private String prefix;
    private String national;
    private String phoneNumber;


    public PhoneNumberHolder(String prefix, String national) {
        this.prefix = prefix;
        this.national = national;
    }

    public PhoneNumberHolder(int prefix, long national, String phoneNumber) {
        this.prefix = "+"+String.valueOf(prefix);
        this.national = String.valueOf(national);
        this.phoneNumber = phoneNumber;
    }

    public PhoneNumberHolder(String prefix, String national, String phoneNumber) {
        this.prefix = prefix;
        this.national = national;
        this.phoneNumber = phoneNumber;
    }


    public String getPrefix() {
        return prefix;
    }
    public String getNational() {
        return national;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
