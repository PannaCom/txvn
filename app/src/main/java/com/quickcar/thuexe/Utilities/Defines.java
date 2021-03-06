package com.quickcar.thuexe.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.quickcar.thuexe.Models.BusInfor;
import com.quickcar.thuexe.Models.CarInforObject;

/**
 * Created by DatNT on 6/29/2016.
 */
public class Defines {
    public static  final String     HOSTNAME                    = "http://thuexevn.com/";
    public static  final String     URL_LIST_ONL_VEHICLE        = HOSTNAME + "Api/getlistonline";
    public static  final String     URL_GET_AROUND              = HOSTNAME + "Api/getaround";
    public static  final String     URL_ACTIVE                  = HOSTNAME + "Api/acitive";
    public static  final String     URL_RENEW                   = HOSTNAME + "Api/activecode";
    public static  final String     URL_RESEND                  = HOSTNAME + "Api/resendactive";
    public static  final String     URL_GET_ALL_CAR_MADE        = HOSTNAME + "Api/getAllCarMadeList";
    public static  final String     URL_LOCATE                  = HOSTNAME + "Api/locate";
    public static  final String     URL_GET_CAR_NAME            = HOSTNAME + "Api/getCarModelListFromMade";
    public static  final String     URL_GET_CAR_MADE            = HOSTNAME + "Api/getCarMadeList";
    public static  final String     URL_GET_CAR_TYPE            = HOSTNAME + "Api/getAllCarTypeList";
    public static  final String     URL_REGISTER_VEHICLE        = HOSTNAME + "Api/register";
    public static  final String     URL_REGISTER_TOKEN          = HOSTNAME + "Api/regIdUser";
    public static  final String     URL_CALL                    = HOSTNAME + "Api/call";
    public static  final String     URL_BOOKING                 = HOSTNAME + "Api/booking";
    public static  final String     URL_GET_HIRE_TYPE           = HOSTNAME + "Api/getCarHireType";
    public static  final String     URL_GET_CAR_SIZE            = HOSTNAME + "Api/getCarSize";
    public static  final String     URL_GET_BOOKING             = HOSTNAME + "Api/getBooking";
    public static  final String     URL_GET_BOOKING_BY_PHONE    = HOSTNAME + "Api/getbookingbyphone";
    public static  final String     URL_LOG_DRIVER              = HOSTNAME + "Api/logDriver";
    public static  final String     URL_UPDATE_BOOKING          = HOSTNAME + "Api/updateBooking";
    public static  final String     URL_BOOKING_FOR_CUSTOMER    = HOSTNAME + "Api/getBookingForCustomer";

    public static  final String     URL_LOGIN                   = HOSTNAME + "Api/loginDriver";
    public static  final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 123;
    public static  final int REQUEST_CODE_LOCATION_PERMISSIONS = 234;
    public static  final int REQUEST_CODE_TELEPHONE_PERMISSIONS = 124;
    public static  final int REQUEST_CODE_CALL_PERMISSIONS = 100;

    public static  final String     VEHICLE_PASS_ACTION         = "1";
    public static  final String     CAR_TYPE_FROM_ACTION        = "2";
    public static  final String     CAR_MADE_TO_ACTION          = "3";
    public static  final String     CAR_MODEL_ACTION            = "4";
    public static  final String     CAR_SIZE_ACTION             = "7";
    public static  final String     VEHICLE_ID_ACTION           = "5";
    public static  final String     OWNER_ID_ACTION             = "6";
    public static  final String     CAR_NAME_ACTION             = "8";
    public static CarInforObject    FilterInfor                 = null;
    public static  int              FILTER_ORDER                = 0;
    public static CarInforObject    SpinnerSelect               = new CarInforObject();
    public static String token                 = "";


    public static  final int        LOOP_TIME                   = 5*1000;

    public static String[] carModel = {};
    public static ArrayList<String> provinceFrom;
    public static int TIME_BEFORE_AUCTION_SHORT                 = 1*60*60*1000 - 1000*60;
    public static final String BUNDLER_DRIVER_PHONE                 = "driver phone";
    public static boolean startThread = false;
    public static boolean isDriver = false;
    public static String[] Audi = {"Audi A3","Audi A4","Audi A5","Audi A6","Audi A7","Audi A8L 3.0","Audi A8L 4.0","Audi TT","Audi Q1","Audi Q3","Audi Q5","Audi Q7 2.0","Audi Q7 3.0"};
    public static String[] BMW = {"BMW 118i","BMW 218i Active Tourer","BMW 218i Gran Tourer","BMW M2","BMW 320i","BMW 330i","BMW 320i GT","BMW 328i GT","BMW M3","BMW 420i","BMW 420i Cabriolet","BMW 428i Grand Coupé","BMW M4 Coupé","BMW M4 Cabriolet","BMW 520i","BMW 528i","BMW 528i GT","BMW 535i","BMW 640i Grand Coupé","BMW 730Li","BMW 740Li","BMW 750Li","BMW X1 sDrive 18i","BMW X1 sDrive 20i","BMW X3 xDrive 20i","BMW X3 xDrive 28i","BMW X4 xDrive 28i","BMW X5 xDrive 30i","BMW X5 xDrive 35i","BMW X6 xDrive 35i","BMW X6 xDrive 30d","BMW Z4 sDrive 20i"};
    public static String[] Chevrolet = {"Aveo LT","Aveo LTZ","Spark Dou (van)","Spark Dou LS","Spark Dou LT","Orlando LTZ","Cruze LT","Cruze LTZ","Captiva LTZ","Colorado LT 4x2","Colorado LT 4x4","Colorado LTZ","Colorado Hight Country"};
    public static String[] Ford = {"Ecosport Trend","Ecosport Trend+","Ecosport Titanium","Everest 4x2 Trend","Everest 4x2 Titanium","Everest 4x4 Titanium","Fiesta Sport","Fiesta Titanium","Fiesta Fox Sport","Focus Ecoboost","Ranger XL 4x4","Ranger XLS 4x2","Ranger XLT 4x4","Ranger 4x2 Wildtrak","Ranger 4x4 Wildtrak"};
    public static String[] Honda = {"City","City Modulo","Civic","Civic Modulo","Accord","CR-V","CR-V TG","Odyssey"};
    public static String[] Hyundai = {"Grand i10 Base","Grand i10","i20 Active","Accent","Elantra","Sonata","Creta","Tucson 2WD","Tucson 2WD Special","SantaFe xăng 4x2","SantaFe xăng 4x4","SantaFe dầu 4x2","SantaFe dầu 4x4","Starex xăng","Starex diesel"};
    public static String[] KIA = {"Morning","Morning EX","Morning LX","Morning Si","Rio 4 cửa","Rio 5 cửa","Cerato 4 cửa","Cerato 5 cửa","Optima","Qouris","Soul","Soul sunroof","Rondo","Rondo Premium","Rondo diesel","Sorento 4x2 diesel","Sorento 4x2 xăng","Sorento 4x2 xăng hight","Sedona diesel","Sedona diesel hight","Sedona xăng","Sedona xăng hight"};
    public static String[] Land_Rover = {"Rover Evoque","Range Rover","Range Rover Sport","Discovery Sport"};
    public static String[] Lexus = {"ES 250","ES 350","GS 350","LS 460L","NX 200t","RX 200t","RX 350","GX 460","LX 570"};
    public static String[] Mazda = {"Mazda 2","Mazda 2 hatchback","Mazda 3 sedan","Mazda 3 hatchback","Mazda 6 2.0","Mazda 6 2.5","CX-5 4x2 2.0","CX-5 4x2 2.5","CX-5 AWD","BT-50 4x2","BT-50 4x4"};
    public static String[] Mercedes_Benz = {"A200","A250","A45 AMG","C200","C250 Exclusive","C300 AMG","C300 Coupé","C63S AMG","E200","E200 Edition E","E250 AMG","S400L","S500L","S500 4 Matic Coupé","S600 Maybach","S63 AMG 4 Matic","CLA 200","CLA 250 4 Matic","CLA 45 AMG 4 Matic","CLS 400","CLS 500 4 Matic","GLA 200","GLA 250 4 Matic","GLA 45 AMG","GLC 250 4 Matic","GLC 300 AMG 4 Matic","GLE 400 4 Matic","GLE 400 4 Matic","GLE 400 4 Matic Exculsive","GLE 400 4 Matic Coupé"};
    public static String[] Mini_Cooper = {"Cooper 3 cửa","Cooper S 3 cửa","Cooper 5 cửa","Cooper S 5 cửa","Cooper Cabriolet","Cooper S Cabriolet","Cooper Countryman","Cooper S Countryman","Cooper Clubman","Cooper S Clubman"};
    public static String[] Mitsubishi_motors = {"Attrage MT Std","Attrage MT","Attrage CVT","Mirage","Outlander 2.0","Outlander 2.4","Outlander Sport","Outlander Sport Premium","Pajero Sport G 4x4","Pajero Sport G 4x2","Pajero Sport D 4x2","Pajero 3.0","Pajero 3.8","Triton 4x2","Triton 4x4"};
    public static String[] Nissan = {"Sunny XL","Sunny XV","Sunny SE","Teana","Teana SL","Juke","Navara E 4x2","Navara EL 4x2","Navara SL 4x4","Navara VL 4x4"};
    public static String[] Peugeot = {"Peugeot 208","Peugeot 308 Allure","Peugeot 308 GT Line","Peugeot 508","Peugeot 2008","Peugeot 3008"};
    public static String[] Porsche = {"Boxster","Boxster Black E","Boxster S","Boxster GTS","718 Boxster","718 Boxster S","Cayman","Cayman Black E","Cayman S","Cayman GTS","718 Cayman","718 Cayman S","911 Carrera","911 Carrera S","911 Carrera C","911 Carrera SC","911 Carrera 4","911 Carrera 4S","911 Carrera 4C","911 Carrera 4SC","911 Targa","911 Targa 4","911 Turbo","911 Turbo C","911 Turbo S","911 Turbo SC","911 GT3","911 GT3 RS","Panamera","Panamera 4","Panamera 4S","Panamera 4S Executive","Macan","Macan S","Macan Turbo","Macan GTS","Cayenne","Cayenne S","Cayenne GTS","Cayenne Turbo","Cayenne Turbo S"};
    public static String[] Renault = {"Logan","Sandero","Duster","Mégane","Latitude 2.0","Latitude 2.5","Koleos 4x2","Koleos 4x4"};
    public static String[] Subaru = {"Forester XT","Forester i-L","XV","Legacy 2.5","Legacy 3.6","Levorg GT-S","Outback 2.5","Outback 3.6","BR-Z","WRX STI"};
    public static String[] Suzuki = {"Swift","Swift Special","Ertiga","Vitara"};
    public static String[] Toyota = {"Yaris E","Yaris G","Vios E","Vios G CVT","Vios G 6MT","Altis G","Altis V","Camry E","Camry G","Camry Q","Innova E","Innova G","Innova V","Fortuner G","Fortuner V 4x2","Fortuner V 4x4","Fortuner TRD 4x2","Fortuner TRD 4x4","Land Cruiser Prado","Land Cruiser","Hilux E","Hilux G 4x4"};
    public static String[] Das_Auto = {"Polo sedan 2014","Polo sedan 2015","Polo sedan GP 2015","Polo hatchback 2015","Polo hatchback 2016","Passat S 2016","Passat E 2016","Passat GP 2016","Tiguan 2015","Touareg 2015","Touareg GP 2015"};

    public static String[][] category  = {Audi, BMW, Chevrolet, Ford,Honda,Hyundai,KIA,Land_Rover,Lexus,Mazda,Mercedes_Benz,Mini_Cooper,Mitsubishi_motors,Nissan,Peugeot,Porsche,Renault,Subaru,Suzuki,Toyota,Das_Auto};
    public static String[] CarMade  = {"Audi", "BMW", "Chevrolet", "Ford","Honda","Hyundai","KIA","Land Rover","Lexus","Mazda","Mercedes_Benz","Mini Cooper","Mitsubishi motors","Nissan","Peugeot","Porsche","Renault","Subaru","Suzuki","Toyota","Das Auto"};
}
