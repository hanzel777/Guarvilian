package com.example.sample1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ToggleButton tb;
    TextView userData;
    TextView logout;

    private String res;

    private static String Glevel;
    private static String Gposition;
    private static String Gdisease;

    private static double Greporter_longitude;
    private static double Greporter_latitude;
    private static double Guser_longitude;
    private static double Guser_latitude;

    private AlertDialog dialog;

    Geocoder mGeocoder;
    List<Address> mListAddress;
    Address mAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userData = (TextView)findViewById(R.id.userData);
        userData.setText(LoginActivity.GuserID_Get()+" 님/구조 레벨: "+LoginActivity.GuserRescueGrade_Get());

        logout = (TextView) findViewById(R.id.logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.GuserID_Clear();
                LoginActivity.GuserRescueGrade_Clear();
                finish();
            }
        });

        tb = (ToggleButton) findViewById(R.id.toggle1);

        // LocationManager 객체를 얻어온다
        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        tb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    if (tb.isChecked()) {
                        // GPS 제공자의 정보가 바뀌면 콜백하도록 리스너 등록하기~!!!
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                                100, // 통지사이의 최소 시간간격 (miliSecond)
                                1, // 통지사이의 최소 변경거리 (m)
                                LocationListener);
                        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                                100, // 통지사이의 최소 시간간격 (miliSecond)
                                1, // 통지사이의 최소 변경거리 (m)
                                LocationListener);
                    } else {
                        dialog = builder.setMessage("알림을 받아오지 않습니다.")
                                .setNegativeButton("확인", null)
                                .create();
                        dialog.show();
                        removeNotification();
                        lm.removeUpdates(LocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
                    }
                } catch (SecurityException ex) {
                }
            }
        });
    } // end of onCreate

    private final LocationListener LocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.d("test", "onLocationChanged, location:" + location);
            Guser_longitude = location.getLongitude(); //경도
            Guser_latitude = location.getLatitude();   //위도
            if(tb.isChecked()) {
                new JSONTask().execute("http://192.168.0.11:3008/infor");
            }
        }


        public void onProviderDisabled(String provider) {
            // Disable시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enable시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    public class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                while(true) {
                    //JSONObject를 만들고 key value 형식으로 값을 저장해준다.
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("level", "level");
                    jsonObject.accumulate("position", "position");
                    jsonObject.accumulate("disease", "disease");


                    HttpURLConnection con = null;
                    BufferedReader reader = null;

                    try {
                        //URL url = new URL("http://192.168.25.16:3000/users");
                        URL url = new URL(urls[0]);//url을 가져온다.
                        con = (HttpURLConnection) url.openConnection();
                        con.setConnectTimeout(10000);
                        con.setReadTimeout(10000);

                        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            //입력 스트림 생성
                            InputStream stream = con.getInputStream();

                            //속도를 향상시키고 부하를 줄이기 위한 버퍼를 선언한다.
                            reader = new BufferedReader(new InputStreamReader(stream));

                            //실제 데이터를 받는곳
                            StringBuffer buffer = new StringBuffer();

                            //line별 스트링을 받기 위한 temp 변수
                            String line = "";

                            //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                            while ((line = reader.readLine()) != null) {
                                buffer.append(line);
                            }

                            //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String... urls) 니까
                            return buffer.toString();
                        }



                        //아래는 예외처리 부분이다.
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        //종료가 되면 disconnect메소드를 호출한다.
                        if (con != null) {
                            con.disconnect();
                        }
                        try {
                            //버퍼를 닫아준다.
                            if (reader != null) {
                                reader.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }//finally 부분

                    Thread.sleep(10000);

                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            int level_start = result.indexOf("\"level\":") + 8;
            int position_start = result.indexOf("\"position\":\"") + 12;
            int disease_start = result.indexOf("\"disease\":\"") + 11;

            int level_end = result.indexOf(",\"p");
            int position_end = result.indexOf("\",\"d");
            int disease_end = result.indexOf("\"}");

            Glevel = result.substring(level_start, level_end);
            Gposition = result.substring(position_start, position_end);
            String disease = result.substring(disease_start, disease_end);

            String[] diseaselist = disease.split("],\\[");

            String disease_arr1 = diseaselist[0];
            String disease_arr2 = diseaselist[1];
            String disease_arr3 = diseaselist[2];
            String disease_level = diseaselist[3];

            String[] arr = disease_level.split(",");
            int num = arr.length;

            disease_arr1 = disease_arr1.substring(4) + "e";
            disease_arr2 = disease_arr2.substring(2) + "e";
            disease_arr3 = disease_arr3.substring(2) + "e";

            int disease_arr1_end = disease_arr1.indexOf("\"e");
            int disease_arr2_end = disease_arr2.indexOf("\"e");
            int disease_arr3_end = disease_arr3.indexOf("\"e");

            disease_arr1 = disease_arr1.substring(0, disease_arr1_end);
            disease_arr2 = disease_arr2.substring(0, disease_arr2_end);
            disease_arr3 = disease_arr3.substring(0, disease_arr3_end);

            String[] disease1 = disease_arr1.split("\\\\\",\\\\\"");
            String[] disease2 = disease_arr2.split("\\\\\",\\\\\"");
            String[] disease3 = disease_arr3.split("\\\\\",\\\\\"");

            Gdisease = "";
            for(int i = 0; i < num; i++){
                if(i==num-1){
                    disease1[i] = disease1[i].substring(0,disease1[i].length()-1);
                    disease2[i] = disease2[i].substring(0,disease2[i].length()-1);
                    disease3[i] = disease3[i].substring(0,disease3[i].length()-1);
                    Gdisease = Gdisease + disease1[i] + " / " + disease2[i] + " / " + disease3[i];
                    break;
                }
                Gdisease = Gdisease + disease1[i] + " / " + disease2[i] + " / " + disease3[i] + "\n";
            }

            reporter_position();

            String[] report_pos = res.split(",");
            Greporter_latitude = Double.parseDouble(report_pos[0]);
            Greporter_longitude = Double.parseDouble(report_pos[1]);

            //위도 경도를 통한 거리 구하기
            double theta = Guser_longitude - Greporter_longitude;
            double distance = Math.sin(deg2rad(Guser_latitude)) * Math.sin(deg2rad(Greporter_latitude)) + Math.cos(deg2rad(Guser_latitude)) * Math.cos(deg2rad(Greporter_latitude)) * Math.cos(deg2rad(theta));
            distance = Math.acos(distance);
            distance = rad2deg(distance);
            distance = distance * 60 * 1.1515 * 1609.344;

            //1000m이하일 때
            if (distance<=1000){
                if(Integer.parseInt(LoginActivity.GuserRescueGrade_Get())<=Integer.parseInt(Glevel)){
                    createNotification();
                    tb.setChecked(false);
                }
            }
        }
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private static double rad2deg(double rad) {
        return (rad * 180/Math.PI);
    }

    private void createNotification() {

        NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(this, "default");
        final Intent intent = new Intent(MainActivity.this.getApplicationContext(),PushMessage.class);
        PendingIntent pendnoti = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mbuilder.setSmallIcon(R.drawable.logo)
                .setContentTitle("응급 상활 발생")
                .setContentText("1Km내에 도움이 필요합니다.")
                .setContentIntent(pendnoti)
                .setAutoCancel(true);

        // 알림 표시
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        notificationManager.notify(1, mbuilder.build());
    }

    private void removeNotification() {
        // Notification 제거
        NotificationManagerCompat.from(this).cancel(1);
    }

    public void reporter_position() {
        String et_GeoInput = Gposition;
        mGeocoder = new Geocoder(this);

        res = SearchLocation(String.valueOf(et_GeoInput));
    }


    public String SearchLocation(String location) {
        String result = "";
        try{
            mListAddress = mGeocoder.getFromLocationName(location, 5);
            if(mListAddress.size() > 0)
            {
                mAddress = mListAddress.get(0);
                result = mAddress.getLatitude() + "," + mAddress.getLongitude();
            }else  {
                Toast.makeText(this, "위치 검색 실패", Toast.LENGTH_SHORT).show();
            }
        }catch(IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }


    public static String Glevel_Get(){return Glevel;}
    public static void Glevel_Clear(){Glevel = "";}
    public static String Gposition_Get(){return Gposition;}
    public static void Gposition_Clear(){Gposition = "";}
    public static String Gdisease_Get(){return Gdisease;}
    public static void Gdisease_Clear(){Gdisease = "";}

    public static double Greporter_longitude_Get(){return Greporter_longitude;}
    public static double Greporter_latitude_Get(){return Greporter_latitude;}
    public static double Guser_lonigtude_Get(){return Guser_longitude;}
    public static double Guser_latitude_Get(){return Guser_latitude;}

}