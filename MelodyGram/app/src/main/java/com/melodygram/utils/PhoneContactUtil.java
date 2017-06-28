package com.melodygram.utils;
/**
 * Created by LALIT on 15-06-2016.
 */

public class PhoneContactUtil
{

	public static String returnOnlyNumber(String phoneNumber)
	{
		phoneNumber = PhoneContactUtil.removeNonDigits(phoneNumber);
		return (phoneNumber.length() > 10) ? PhoneContactUtil.removeCountryCode(phoneNumber) : phoneNumber;
	}

	public static String nonDigitsPhNum;

	public static String removeNonDigits(final String phoneNumber)
	{
		try
		{
			if (phoneNumber == null || phoneNumber.length() == 0)
			{
				return "";
			}
			nonDigitsPhNum = phoneNumber.replaceAll("\\D+", "");
			return (nonDigitsPhNum.charAt(0) == '0') ? removeZeroDigits(nonDigitsPhNum.substring(1)) : nonDigitsPhNum;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return phoneNumber;
	}

	public static String removeZeroDigits(String zeroAtIndexZeroNum)
	{
		try
		{
			return (zeroAtIndexZeroNum.charAt(0) == '0') ? zeroAtIndexZeroNum.substring(1) : zeroAtIndexZeroNum;
		}
		catch (Exception e)
		{
             e.printStackTrace();
		}
		return zeroAtIndexZeroNum;
	}

	static String countryCode;

	public static String removeCountryCode(String phoneNumber) {

		try {
			if (JsonUtil.countryList.size() > 0) {
				for (int position = 0; position < JsonUtil.countryList.size(); position++) {
					countryCode = JsonUtil.countryList.get(position).getCountryCode();
					countryCode = removeNonDigits(countryCode);

					if (countryCode.equals(phoneNumber.substring(0, countryCode.length()))) {
						phoneNumber = phoneNumber.substring(countryCode.length());
						break;
					}
				}

			} else {
				return phoneNumber;
			}
		}catch (Exception e)
		{
			e.printStackTrace();
		}
			return phoneNumber;
		}

}
