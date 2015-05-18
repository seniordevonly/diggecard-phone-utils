package com.realtap.phone.utils;


import com.realtap.phone.beans.PhoneNumberHolder;
import com.realtap.phone.exceptions.PhoneNumberParsingException;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PhoneNumberUtils {

    private static Logger log = LoggerFactory.getLogger(PhoneNumberUtils.class);

    private static final String EMPTY_COUNTRY_CODE = "null";
    private static final String UNKNOWN_REGION = "ZZ";
    private static final String JUST_NUMBERS = "[^\\w\\s\\.]";

    static com.google.i18n.phonenumbers.PhoneNumberUtil phoneUtil;

    static {
        phoneUtil =
                com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance();
    }

    public static Phonenumber.PhoneNumber parsePhoneByGoogle(String phone, String country) {

        try {
            return phoneUtil.parse(phone, country);

        } catch (NumberParseException e) {
            log.warn(e.getMessage() + " country: " + country + " phone: " + phone);
        }
        return null;
    }

    public static String getCountryCodeFromFullPhoneNumber(String fullPhoneNumber) {
        try {

            PhoneNumber phoneNumber = phoneUtil.parse(fullPhoneNumber, UNKNOWN_REGION);
            return String.valueOf(phoneNumber.getCountryCode());
        } catch (NumberParseException e) {
            throw new PhoneNumberParsingException(e);
        }
    }

    public static boolean isItalianOrUnknownNumber(String fullPhoneNumber) {
        PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneUtil.parse(fullPhoneNumber, UNKNOWN_REGION);
        } catch (NumberParseException e) {
            return true;
        }
        return phoneNumber.isItalianLeadingZero();
    }

    public static String getCountryCodeWithPlusSignFromFullPhoneNumber(String fullPhoneNumber) {
        try {
            PhoneNumber phoneNumber = phoneUtil.parse(fullPhoneNumber, UNKNOWN_REGION);
            return "+" + String.valueOf(phoneNumber.getCountryCode());
        } catch (NumberParseException e) {
            throw new PhoneNumberParsingException(e);
        }
    }

    public static String getPhoneNumberWithoutCountryCodeFromFullPhoneNumber(String fullPhoneNumber) {
        try {
            PhoneNumber phoneNumber = phoneUtil.parse(fullPhoneNumber, UNKNOWN_REGION);
            StringBuilder nationalNumber = new StringBuilder();
            if (phoneNumber.isItalianLeadingZero()){
                nationalNumber.append("0");
            }
            nationalNumber.append(Long.toString(phoneNumber.getNationalNumber()));
            return nationalNumber.toString();
        } catch (NumberParseException e) {
            throw new PhoneNumberParsingException(e);
        }
    }

    public static PhoneNumber getPhoneNumberObjFromFullPhoneNumber(String fullPhoneNumber) {

        try {
            return  phoneUtil.parse(fullPhoneNumber, UNKNOWN_REGION);
        } catch (NumberParseException e) {
            throw new PhoneNumberParsingException(e);
        }
    }

    public static boolean areNationalNumbersSame(String phone1, String phone2){

        if(phone1 == null || phone1.isEmpty() || phone2 == null || phone2.isEmpty()){
            return false;
        }

        try{

            Long number1 = getNationalNumber(phone1);
            Long number2 = getNationalNumber(phone2);

            return !(number1 == null || number2 == null) && number1.equals(number2);

        }catch(PhoneNumberParsingException e){
            return false;
        }
    }

    /**
     * Input can be with or without country code, and with or without country prefix at all.
     * We will do our best in the method. If all other fails we will simply return the raw phone
     * number given as input, and with all non numeric characters removed.
     * @param phone
     * @return
     */
    public static Long getNationalNumber(String phone){
        if(phone == null || phone.trim().isEmpty()){
            return null;
        }
        try{

            PhoneNumber phoneObj = getPhoneNumberObjFromFullPhoneNumberAddPlusPrefixIfNotExist(phone);

            return phoneObj.getNationalNumber();

        }catch(PhoneNumberParsingException e){
            return removeAllNonNumeric(phone);
        }
    }

    /**
     * Be aware that this method is kind of hack.
     *
     * https://groups.google.com/forum/#!topic/libphonenumber-discuss/IqP4cC8udn0
     * @param fullPhoneNumber
     * @return
     */
    public static PhoneNumber getPhoneNumberObjFromFullPhoneNumberAddPlusPrefixIfNotExist(String fullPhoneNumber) {

        try {

            return getPhoneNumberObjFromFullPhoneNumber(fullPhoneNumber);

        } catch (PhoneNumberParsingException e) {

            // if doesnt exist, lets add plus prefix and
            if(fullPhoneNumber == null || fullPhoneNumber.isEmpty()){
                throw new PhoneNumberParsingException("Phone number is null or empty: "+fullPhoneNumber);
            }
            // a full phone number requires plus prefix, add if it doesn't exist
            if(!fullPhoneNumber.startsWith("+") && fullPhoneNumber.startsWith("1")){
                fullPhoneNumber = "+"+fullPhoneNumber;
            }

            if(isValidFullPhoneNumberHelper(fullPhoneNumber)){
                return getPhoneNumberObjFromFullPhoneNumber(fullPhoneNumber);
            }

            throw e;
        }
    }

    public static boolean isPossiblePhoneNumber(String defaultPhonePrefix, String inputPhoneNumber) {
        return isPossibleFullPhoneNumber(
                generateFullPhoneNumber(defaultPhonePrefix, inputPhoneNumber)
        );
    }

    public static List<String> validatePhoneNumbers(List<String> numbers) {
        if(numbers == null) {
            return new ArrayList<>();
        }
        return numbers.stream()
                .filter(n -> n != null)
                .filter(n -> !n.isEmpty())
                .filter(n -> PhoneNumberUtils.isPossiblePhoneNumber("+47", n))
                .distinct()
                .map(n -> PhoneNumberUtils.generateFullPhoneNumber("+47", n))
                .collect(Collectors.toList());
    }

    public static String generateFullPhoneNumber(String defaultPhonePrefix, String inputPhoneNumber) {

        if(inputPhoneNumber == null) {
            return null;
        }

        // else, remove all eventual invalid characters
        inputPhoneNumber = removeNonInteger(inputPhoneNumber);

        // first check if already valid number
        if(isValidFullPhoneNumberHelper(inputPhoneNumber)) {
            return inputPhoneNumber;
        }

        // check if then valid
        if(isValidFullPhoneNumberHelper(inputPhoneNumber)) {
            return inputPhoneNumber;
        }

        try {

            String region = phoneUtil.getRegionCodeForCountryCode(Integer.parseInt(defaultPhonePrefix));
            PhoneNumber phoneNumber = phoneUtil.parse(inputPhoneNumber, region);
            long phonePrefix = phoneNumber.getCountryCode();
            long nationalNumber = phoneNumber.getNationalNumber();

            return "+"+phonePrefix + nationalNumber;

        } catch (NumberParseException | NumberFormatException e) {
            log.error(e.getMessage(), e);
        }

        // we give up
        return inputPhoneNumber;
    }

    public static boolean isValidFullPhoneNumberHelper(String fullPhoneNumber){
        try {
            PhoneNumber phoneNumber = phoneUtil.parse(fullPhoneNumber, UNKNOWN_REGION);
            return phoneUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            return false;
        }
    }

    public static boolean isPossibleFullPhoneNumber(String fullPhoneNumber) {
        if (null == fullPhoneNumber) {
            return false;
        }

        String regexp = "((?:[a-z][a-z]+))";
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(fullPhoneNumber);

        if (matcher.find()) {
            return false;
        }

        try {
            PhoneNumber phoneNumber = phoneUtil.parse(fullPhoneNumber, UNKNOWN_REGION);
            return phoneUtil.isPossibleNumber(phoneNumber);
        } catch (NumberParseException e) {
            return false;
        }
    }

    public static String normalizePhoneNumber(String phoneNumber) {

        PhoneNumber pNumber = null;
        try {
            pNumber = phoneUtil.parse(phoneNumber, UNKNOWN_REGION);
        } catch (NumberParseException e) {
            log.debug("bad  number:" + phoneNumber);
            throw new PhoneNumberParsingException("phone number invalid: " + phoneNumber);
        }
        phoneNumber = phoneUtil.format(pNumber, PhoneNumberFormat.E164);

        if (!phoneUtil.isPossibleNumber(pNumber)) {
            throw new PhoneNumberParsingException("phone number invalid: " + phoneNumber);
        }

        return phoneNumber;
    }

    public static String replaceInternationalCallingPrefixWithPlus(String phoneNumber) {
        String changedPhoneNumber = phoneNumber;
        /*if (StringUtils.startsWith(changedPhoneNumber, "00")) {
            changedPhoneNumber = "+" + StringUtils.substring(phoneNumber, 2);
        }*/
        if (changedPhoneNumber != null && changedPhoneNumber.startsWith("00")) { //StringUtils.startsWith(changedPhoneNumber, "00")) {
            changedPhoneNumber = "+" + phoneNumber.substring(2);
        }
        return changedPhoneNumber;
    }

    public static boolean hasCountryCode(String phoneNumber) {
        String changedPhoneNumber = replaceInternationalCallingPrefixWithPlus(phoneNumber);
        String countryCode = null;
        try {
            countryCode = getCountryCodeFromFullPhoneNumber(changedPhoneNumber);
        } catch (Exception e) {
            return false;
        }
        //return !StringUtils.isEmpty(countryCode);
        return countryCode != null && !countryCode.isEmpty();
    }

    public static String appendCountryCodeIfMissingAndNormalize(String phoneNumber, String phoneCountryCode) {

        if(phoneNumber != null && phoneNumber.startsWith("00")) {
            phoneNumber = "+"+phoneNumber.substring(2);
        }

        String countryCodeNum;
        String region = null;
        if(phoneCountryCode != null && !phoneCountryCode.isEmpty()){
            countryCodeNum = phoneCountryCode.replaceAll(JUST_NUMBERS, "");
            region = phoneUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCodeNum));
        }

        PhoneNumber pNumber = null;
        try {
            pNumber = phoneUtil.parse(phoneNumber, region);
        } catch (NumberParseException e) {
            log.debug("bad  region: " + region + ",  or number:" + phoneNumber);
            throw new PhoneNumberParsingException("phone number invalid: " + phoneNumber);
            //return phoneCountryCode.charAt(0) == '+' ? phoneCountryCode + phoneNumber : "+" + phoneCountryCode + phoneNumber;
        }

        if (!phoneUtil.isPossibleNumber(pNumber)) {
            throw new PhoneNumberParsingException("phone number invalid: " + phoneNumber);
        }

        phoneNumber = phoneUtil.format(pNumber, PhoneNumberFormat.E164);
        return phoneNumber;
    }

    public static String getPhoneWithoutCountryCode(String phoneNumber, String countryCode) {
        String countryCodeNum = countryCode.replaceAll(JUST_NUMBERS, "");
        String region = phoneUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCodeNum));
        PhoneNumber pNumber = null;
        try {
            pNumber = phoneUtil.parse(phoneNumber, region);
        } catch (NumberParseException e) {
            throw new PhoneNumberParsingException("phone number error: " + phoneNumber);
        }
        // countryCode = countryCode.replaceAll("\\+", "\\\\+"); - what is it?
        return pNumber.isItalianLeadingZero() ? "0" + pNumber.getNationalNumber() : "" + pNumber.getNationalNumber();
    }

    private static String normalizeGuardianPhoneNumber(String phoneNumber, String userPhoneNumber) {
        String userPhoneCountryCode = getCountryCodeFromFullPhoneNumber(userPhoneNumber);
        return appendCountryCodeIfMissingAndNormalize(phoneNumber, userPhoneCountryCode);
    }


    /**
     * Will remove leading zero:
     * 1)
     * @param phoneNumber
     * @return
     */
    public static String removeNationalLeadingZero(String phoneNumber){
        //
        if(phoneNumber == null || phoneNumber.isEmpty()){
            return phoneNumber;
        }

        try{
            // if normalization succeeds, its a perfect valid number with country code, lets just normalize it
            return normalizePhoneNumber(phoneNumber);

        }catch (PhoneNumberParsingException e){
            // else its probably without country code, lets check if it starts with leading zero
            if(phoneNumber.startsWith("00")){
                return phoneNumber.substring(2);
            }
            if(phoneNumber.startsWith("0")){
                return phoneNumber.substring(1);
            }
            // else just return original string
            return phoneNumber;
        }
    }

    /**
     * Country code is WITHOUT leading "+" or "00". In phone strings no spaces are allowed.
     * In other words phone numbers consist only from digits, as first digit could not be "0".
     * @param phoneNumber
     * @return
     */
    public static String formatPhoneNumber(String phoneCode, String phoneNumber) {
        if (phoneCode == null || phoneNumber == null || phoneNumber.equals("")) {
            return "";
        } else {
            return getCleanCountryPhoneCode(phoneCode) + getCleanPhoneNumber(phoneNumber);
        }
    }

    private static String getCleanCountryPhoneCode(String countryPhoneCode) {
        String phoneCode = countryPhoneCode.replace(" ", "");
        Pattern pattern = Pattern.compile("(\\+|00)(\\d+)");
        Matcher matcher = pattern.matcher(phoneCode);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return "";
    }

    private static String getCleanPhoneNumber(String phoneNumber) {
        String number = phoneNumber.replace(" ", "");
        if (number.startsWith("0")) {
            number = number.substring(1);
        }
        return number;
    }

    /**
     * Will parse the given phone number.
     *
     * If number starts with null, strip it away. If no "null" prefix and number is not a valid phone number,
     * return null country code and the phone number with what every value the phoneNumber parameters has.
     *
     * @param phoneNumber
     * @return
     * @throws PhoneNumberParsingException
     */
    public static PhoneNumberHolder parsePhoneNumberWhichAcceptNonNumbers(String phoneNumber) throws PhoneNumberParsingException {
        String countryCode;
        String shortPhoneNumber;
        try {
            countryCode = getCountryCodeWithPlusSignFromFullPhoneNumber(phoneNumber);
            shortPhoneNumber = getPhoneNumberWithoutCountryCodeFromFullPhoneNumber(phoneNumber);

            return new PhoneNumberHolder(countryCode, shortPhoneNumber);
        } catch (PhoneNumberParsingException e) {
            if(phoneNumber.trim().equalsIgnoreCase(EMPTY_COUNTRY_CODE)){
                return new PhoneNumberHolder(null, "");
            }
            if (phoneNumber.startsWith(EMPTY_COUNTRY_CODE)) {
                shortPhoneNumber = phoneNumber.split(EMPTY_COUNTRY_CODE)[1];
                return new PhoneNumberHolder(null, shortPhoneNumber);
            }
            else{
                return new PhoneNumberHolder(null, phoneNumber);
            }
            //throw new PhoneNumberParsingException(phoneNumber+" is not valid phone number");
        }
    }

    /**
     * Returns a nicely printed string representing the list give as input
     * @param numbers a list of numbers
     * @return nicely printed string like: +4745037118, +90630185
     */
    public static String prettyPrintNumbers(List<String> numbers) {
        if(numbers == null || numbers.isEmpty()) {
            return "";
        }

        String str = "";
        for(int i=0; i < numbers.size(); i++) {
            str += numbers.get(i);
            if(i != numbers.size()-1) {
                str += ", ";
            }
        }
        return str;
    }

    /**
     * Replace all non int characters with "" except for +
     * @param myStr
     * @return
     */
    public static String removeNonInteger(String myStr){
        if(myStr == null){
            return null;
        }
        return myStr.replaceAll( "[^+0-9]", "" );
    }

    public static Long removeAllNonNumeric(String myStr){
        String s = myStr.replaceAll( "[^\\d]", "" );
        if(s.trim().isEmpty()){
            return null;
        }

        try{
            return new Long(s);
        }catch (NumberFormatException e){
            log.error(e.getMessage());
            return null;
        }
    }
}
