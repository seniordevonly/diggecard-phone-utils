package com.realtap.phone.beans;

/**
 * User: tha022
 * Date: 1/28/13
 * Time: 11:38 AM
 */
public class PhoneNumberHolder {

    private String countryCode;
    private String shortPhoneNumber;

    public PhoneNumberHolder(String countryCode, String shortPhoneNumber){
        this.countryCode = countryCode;
        this.shortPhoneNumber = shortPhoneNumber;
    }


    public String getCountryCode() {
        return countryCode;
    }
    public String getShortPhoneNumber() {

        return shortPhoneNumber;
    }

}
