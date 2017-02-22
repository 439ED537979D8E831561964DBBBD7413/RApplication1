package com.rawalinfocom.rcontact.calllog;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by user on 08/02/17.
 */

public final class TelephonyInfo {

    private static TelephonyInfo telephonyInfo;
    private String imsiSIM1;
    private String imsiSIM2;
    private boolean isSIM1Ready;
    private boolean isSIM2Ready;
    private String sim1Number;
    private String sim2Number;


    public String getImsiSIM1() {
        return imsiSIM1;
    }

    /*public static void setImsiSIM1(String imsiSIM1) {
        TelephonyInfo.imsiSIM1 = imsiSIM1;
    }*/

    public String getImsiSIM2() {
        return imsiSIM2;
    }

    /*public static void setImsiSIM2(String imsiSIM2) {
        TelephonyInfo.imsiSIM2 = imsiSIM2;
    }*/

    public boolean isSIM1Ready() {
        return isSIM1Ready;
    }

    /*public static void setSIM1Ready(boolean isSIM1Ready) {
        TelephonyInfo.isSIM1Ready = isSIM1Ready;
    }*/

    public boolean isSIM2Ready() {
        return isSIM2Ready;
    }

    /*public static void setSIM2Ready(boolean isSIM2Ready) {
        TelephonyInfo.isSIM2Ready = isSIM2Ready;
    }*/

    public boolean isDualSIM() {
        return imsiSIM2 != null;
    }

    private TelephonyInfo() {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static TelephonyInfo getInstance(Context context){

        if(telephonyInfo == null) {
            telephonyInfo = new TelephonyInfo();
            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
            telephonyInfo.imsiSIM1 = telephonyManager.getDeviceId();;
            telephonyInfo.imsiSIM2 = null;

            String simSerialNumber =  telephonyManager.getSimSerialNumber();
            if(!TextUtils.isEmpty(simSerialNumber)){
                Log.e("Sim Serial Number" , simSerialNumber + " NO " + telephonyManager.getNetworkOperator()
                        + " NON " + telephonyManager.getNetworkOperatorName() + " SO " + telephonyManager.getSimOperator() + " SON "
                        + telephonyManager.getSimOperatorName());
            }

            /*telephonyInfo.sim1Number = telephonyManager.getLine1Number();
            telephonyInfo.sim2Number = "";
            if(!TextUtils.isEmpty(telephonyInfo.sim1Number));
                Log.e("Sim Number", telephonyInfo.sim1Number);

            try {
                List<CellInfo> infoList = telephonyManager.getAllCellInfo();
                if(infoList!=null && infoList.size()>0)
                    Log.e("Cell Info ", infoList +"");

            }catch (SecurityException e){
                e.printStackTrace();
            }

            String simSerialNumber =  telephonyManager.getSimSerialNumber();
            if(!TextUtils.isEmpty(simSerialNumber)){
                Log.e("Sim Serial Number" , simSerialNumber + " NO " + telephonyManager.getNetworkOperator()
                + " NON " + telephonyManager.getNetworkOperatorName() + " SO " + telephonyManager.getSimOperator() + " SON "
                + telephonyManager.getSimOperatorName());
            }*/

            /*try {
                telephonyInfo.sim1Number = getSimNumberBySlot(context, "simNumberGemini", 0);
                telephonyInfo.sim2Number = getSimNumberBySlot(context, "simNumberGemini", 1);
                if(!TextUtils.isEmpty(telephonyInfo.sim1Number) )
                    Log.e("Sim Number 1", telephonyInfo.sim1Number);
                if(!TextUtils.isEmpty(telephonyInfo.sim2Number) )
                    Log.e("Sim Number 2", telephonyInfo.sim2Number);

            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();

                try {
                    telephonyInfo.sim1Number = getSimNumberBySlot(context, "simNumber", 0);
                    telephonyInfo.sim2Number = getSimNumberBySlot(context, "simNumber", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }*/




            try {
                telephonyInfo.imsiSIM1 = getDeviceIdBySlot(context, "getDeviceIdGemini", 0);
                telephonyInfo.imsiSIM2 = getDeviceIdBySlot(context, "getDeviceIdGemini", 1);
            } catch (GeminiMethodNotFoundException e) {
                e.printStackTrace();
                try {
                    telephonyInfo.imsiSIM1 = getDeviceIdBySlot(context, "getDeviceId", 0);
                    telephonyInfo.imsiSIM2 = getDeviceIdBySlot(context, "getDeviceId", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }

            telephonyInfo.isSIM1Ready = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
            telephonyInfo.isSIM2Ready = false;

            try {
                telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimStateGemini", 0);
                telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimStateGemini", 1);
            } catch (GeminiMethodNotFoundException e) {

                e.printStackTrace();

                try {
                    telephonyInfo.isSIM1Ready = getSIMStateBySlot(context, "getSimState", 0);
                    telephonyInfo.isSIM2Ready = getSIMStateBySlot(context, "getSimState", 1);
                } catch (GeminiMethodNotFoundException e1) {
                    //Call here for next manufacturer's predicted method name if you wish
                    e1.printStackTrace();
                }
            }
        }

        return telephonyInfo;
    }


   /* String telephonyClassName;
    String[] listofClass;
    private static String[] deviceIdMethods = {"getDeviceIdGemini", "getDeviceId", "getDeviceIdDs", "getDeviceIdExt"};
    private String SIM_VARINT = "";
    private static String[] networkOperatorNameMethods = {"getNetworkOperatorNameGemini", "getNetworkOperatorName", "getNetworkOperatorNameExt"};

    private String slotName_1 = "null";
    private String slotName_2 = "null";
    private int slotNumber_1 = 0;
    private int slotNumber_2 = 0;

    public boolean isTelephonyClassExists(String className) {

        boolean isClassExists = false;
        try {
            Class<?> telephonyClass = Class.forName(className);
            isClassExists = true;
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return isClassExists;
    }

    *//**
     * This method returns the class name in which we fetch dual sim details
     *//*
    public void fetchClassInfo() {
        try {
            telephonyClassName = "android.telephony.TelephonyManager";
            listofClass = new String[]{
                    "com.mediatek.telephony.TelephonyManagerEx",
                    "android.telephony.TelephonyManager",
                    "android.telephony.MSimTelephonyManager",
                    "com.android.internal.telephony.Phone",
                    "com.android.internal.telephony.PhoneFactory",
                    "com.lge.telephony.msim.LGMSimTelephonyManager",
                    "com.asus.telephony.AsusTelephonyManager",
                    "com.htc.telephony.HtcTelephonyManager"};

            for (int index = 0; index < listofClass.length; index++) {
                if (isTelephonyClassExists(listofClass[index])) {

                    for (String deviceIdMethod : deviceIdMethods) {
                        if (isMethodExists(listofClass[index], deviceIdMethod)) {
                            //System.out.println("getDeviceId method found");
                            if (!SIM_VARINT.equalsIgnoreCase("")) {
                                break;
                            }
                        }
                    }

                    for (String networkOperatorNameMethod : networkOperatorNameMethods) {
                        if (isMethodExists(listofClass[index], networkOperatorNameMethod)){
                            //System.out.println("getNetworkOperatorName method found");
                            if (!SIM_VARINT.equalsIgnoreCase("")) {
                                break;
                            }
                        }
                    }


//                        if (isMethodExists(listofClass[index], "getNetworkOperatorName") ||
//                                isMethodExists(listofClass[index], "getNetworkOperatorNameGemini") ||
//                                isMethodExists(listofClass[index], "getNetworkOperatorNameExt")) {
//                            *//*System.out
//									.println("getNetworkOperatorName method found");*//*
//                            break;
//                        } else if (isMethodExists(listofClass[index],
//                                "getSimOperatorName") || isMethodExists(listofClass[index],
//                                "getSimOperatorNameGemini")) {
//							*//*System.out.println("getSimOperatorName method found");*//*
//                            break;
//                        }
                }
            }
            for (int index = 0; index < listofClass.length; index++) {
                try {
                    if (slotName_1 == null || slotName_1.equalsIgnoreCase("")) {
//                        getValidSlotFields(listofClass[index]);
                        getSlotNumber(listofClass[index]);
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }

            *//*SharedPreferences.Editor edit = pref.edit();
            edit.putString("dualsim_telephonycls", telephonyClassName);
            edit.putString("SIM_VARINT", SIM_VARINT);
            edit.putString("SIM_SLOT_NAME_1", slotName_1);
            edit.putString("SIM_SLOT_NAME_2", slotName_2);
            edit.putInt("SIM_SLOT_NUMBER_1", slotNumber_1);
            edit.putInt("SIM_SLOT_NUMBER_2", slotNumber_2);
            edit.commit();*//*
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    *//**
     * Check method with sim variant
     *//*
    public boolean isMethodExists(String className, String compairMethod) {
        boolean isExists = false;
        try {
            Class<?> telephonyClass = Class.forName(className);
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            StringBuffer sbf = new StringBuffer();
            Method[] methodList = telephonyClass.getDeclaredMethods();
            for (int index = methodList.length - 1; index >= 0; index--) {
                sbf.append("\n\n" + methodList[index].getName());
                if (methodList[index].getReturnType().equals(String.class)) {
                    String methodName = methodList[index].getName();
                    if (methodName.contains(compairMethod)) {
                        Class<?>[] param = methodList[index]
                                .getParameterTypes();
                        if (param.length > 0) {
                            if (param[0].equals(int.class)) {
                                try {
                                    SIM_VARINT = methodName.substring(
                                            compairMethod.length(),
                                            methodName.length());
                                    telephonyClassName = className;
                                    isExists = true;
                                    break;
                                } catch (Exception e) {
                                    //e.printStackTrace();
                                }
                            } else {
                                telephonyClassName = className;
                                isExists = true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return isExists;
    }
*/
    /*private static String slotName_1 = "null";
    private static String slotName_2 = "null";
    private static int slotNumber_1 = 0;
    private static int slotNumber_2 = 0;

    private static void getSlotNumber(String className) {
        try {
            Class<?> c = Class.forName(className);
            Field fields1 = c.getField(slotName_1);
            fields1.setAccessible(true);
            slotNumber_1 = (Integer) fields1.get(null);
            Field fields2 = c.getField(slotName_2);
            fields2.setAccessible(true);
            slotNumber_2 = (Integer) fields2.get(null);
        } catch (Exception e) {
            slotNumber_1 = 0;
            slotNumber_2 = 1;
            // //e.printStackTrace();
        }
    }*/



    private static String getDeviceIdBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        String imsi = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{
            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimID = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimID.invoke(telephony, obParameter);

            if(ob_phone != null){
                imsi = ob_phone.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return imsi;
    }


    private static String getSimNumberBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        String simNum = null;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method simNumberGemini = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = simNumberGemini.invoke(telephony, obParameter);

            if(ob_phone != null){
                simNum = ob_phone.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return simNum;
    }

    private static  boolean getSIMStateBySlot(Context context, String predictedMethodName, int slotID) throws GeminiMethodNotFoundException {

        boolean isReady = false;

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try{

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = slotID;
            Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);

            if(ob_phone != null){
                int simState = Integer.parseInt(ob_phone.toString());
                if(simState == TelephonyManager.SIM_STATE_READY){
                    isReady = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeminiMethodNotFoundException(predictedMethodName);
        }

        return isReady;
    }


    private static class GeminiMethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        public GeminiMethodNotFoundException(String info) {
            super(info);
        }
    }
}
