package com.practice.wpsactivity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<ScanResult> items;
    private List<android.net.wifi.WifiNetworkSuggestion> wifiNetworkSuggestionList;

    //add
    private Context mcontext;

    //아래의 코드 주석을 풀면 앱이 중단된다.
    /*

    // 현제 SSID 를 받는 코드
    //아래 코드는 굳이 쓸 필요가 없다. 앱 실행시 오류를 발생시키는 원인인거같음
    public String getWiFiSSID() {
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        String sSSID = connectionInfo.getSSID();
        String s = sSSID.substring(1, sSSID.length() - 1);
        return s;
    }
    //근데 위에 처럼 굳이 와이파이를 저렇게 받아와야 하나!!

    * */



    public MyAdapter(List<ScanResult> items){
        this.items=items;
    }
    //생성자가 아이템들을 나열을 한다.

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mcontext = parent.getContext();
        //mcontext = mcontext.getApplicationContext();

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item , parent, false);
        return new MyViewHolder(itemView);

        //여기엔 버튼이 없어도 되나,,!??
    }


    //버튼을 만들어서 바인딩 해주었다~
    //이제 어떤 동작을 실행할 것인지 일단은 토스트로 만들어볼까??!
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        ScanResult item=items.get(position);
        holder.setItem(items.get(position));

        /**추가한 부분**/
        holder.btncon.findViewById(R.id.btn_connect);
    }



    //아이템리스트 개수 가져옴
    @Override
    public int getItemCount() {
        return items.size();
    }


    //여기서 동작이 가능한가?
    //사실상 메인 클래스
    //inner class
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvWifiName;

        public Button btncon;
        public Button btndiscon;
        public String wifipassword;

        //NetworkCallback

        public MyViewHolder(View itemView) {

            super(itemView);
            tvWifiName=itemView.findViewById(R.id.tv_wifiName);

            /**추가구문**/
            btncon = itemView.findViewById(R.id.btn_connect);


            //itemview는 전체 아이템들을 의미 (리사이클러뷰의 아이템들)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentpos = getAdapterPosition();
                    //list배열의 인덱스넘버
                    ScanResult scanResult = items.get(currentpos);
                    /**이부분에 동적인 부분을 넣어주기??!**/
                    Toast.makeText(mcontext,scanResult.SSID,Toast.LENGTH_SHORT).show();

                }
            });


            /**아래도 추가해준 구문이다.**/
            //btncon 에 연결하면 튕기는 현상 발생

            btncon.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onClick(View view) {
                    int currentpos = getAdapterPosition();
                    ScanResult scanResult = items.get(currentpos);

                    Toast.makeText(mcontext,scanResult.SSID,Toast.LENGTH_SHORT).show();
                    Toast.makeText(mcontext.getApplicationContext(), "connect complete\n"+currentpos+1,Toast.LENGTH_SHORT).show();

                    /**--------------------------------------------------------------------------**/
                    // 또 실패한 이유는 뭘까
                        //앱 튕김현상이 발생한다.

                    //여기는 WPA2방식의 암호화에 접근하는 방식!

                    //wifi 연결에 뭔가 추가적인게 필요한거같음
                    //SSID, password
                    WifiNetworkSuggestion wifiNetworkSuggestion =
                            new WifiNetworkSuggestion.Builder()
                                    .setSsid(scanResult.SSID)
                                    //.setSsid(scanResult.SSID) //SSID 이름
                                    .setWpa2Passphrase(wifipassword)
                                    .build();
                        /*

                        * */

                    /**

                     openWiFi connect solution.

                     WifiNetworkSuggestion suggestionOpen = new WifiNetworkSuggestion.Builder()
                     .setSsid(getWiFiSSID()) //SSID 이름
                     .build();

                     * **/

                    /**--------------------------------------------------------------------------**/

                    //추가된 구문!   BroadcastReceiver

                    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION.equals(intent.getAction())) {
                                //제안된 Wi-Fi netwrok 연결 후 처리
                            }
                        }
                    };

                    IntentFilter intentFilter = new IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION);
                    mcontext.getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);

                    WifiManager wifiManager = (WifiManager)
                            mcontext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

                    wifiManager.addNetworkSuggestions(wifiNetworkSuggestionList);
                    int status = wifiManager.addNetworkSuggestions(wifiNetworkSuggestionList);


                    /*

                        * */

                    /**아래의 2개의 문장은 어디로 넣어야 할지 모르겠다.**/
                    //이걸 왜 쓰는지도 찾아보자
                    //wifiManager.addNetworkSuggestions(items);    //networkSuggestions 대신 리스트가 들어가야 한다.

                    //int status = wifiManager.addNetworkSuggestions(items);   //networkSuggestions

                    /**

                    //주석은 p2p 네트워크 연결시에 필요하다!
                    WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                            .setSsid(getWiFiSSID())
                                    //.setSsid(scanResult.SSID) //SSID 이름
                            .setWpa2Passphrase("2019wjswk") //비밀번호, 보안설정 WPA2
                                    //영진이 방 비밀번호
                                    //이부분을 최종 버전에서는 수정하자
                            .setIsEnhancedOpen(true)
                            .build();
                    * */


                    /**
                    구글 닥스에서 가져옴

                     .setSsidPattern(new PatternMatcher("test", PatternMatcher.PATTERN_PREFIX))
                     .setBssidPattern(MacAddress.fromString("10:03:23:00:00:00"), MacAddress.fromString("ff:ff:ff:00:00:00"))
                     * **/

                    /**

                     wifiNetworkSpecifier 관련부분

                     NetworkRequest networkRequest = new NetworkRequest.Builder()
                     .addTransportType(NetworkCapabilities.TRANSPORT_WIFI) //연결 Type
                     .setNetworkSpecifier(wifiNetworkSpecifier)
                     .build();
                     * **/



                    /*
                    ConnectivityManager connectivityManager =
                            (ConnectivityManager) mcontext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(@NonNull Network network) {
                            //Log.d(tag, "onAvailable");
                        }

                        @Override
                        public void onUnavailable() {
                            //Log.d(tag, "onUnavailable");
                        }
                    };
                    connectivityManager.unregisterNetworkCallback(networkCallback);

                    * */

                    //connectivityManager.requestNetwork(networkRequest, networkCallback);

                    //unregisterNetworkCallback



                    /**
                     * 이제 connect되면 웹서버로 넘어가서 짜여진 웹 페이지 상의 키오스크에 접근하게 할 수 있다..
                     * 아니면, 해당 웹 서버의 로컬 호스트 주소를 intent한 페이지에 넣어서 접속 시키게 할 수 도 있다..
                     *
                     * 위의 두가지 방식을 통해서 웹 서버에 접속하면 끝.
                     *
                     * 이제 매장서버에 접속하게 할 수 있는 방법은
                     * 와이파이를 전체 공개로 해놓거나
                     * 해당 매장에 수신동의를 하면, 접속할수 있게 하는 스타벅스 방식으로 진행하면 될 것.
                     * **/

                }

            });

            btndiscon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /**그냥 disconnect버튼을 안쓰는 방법으로 갈까 고민중! **/
                    //Disconnect();
                }
            });


            //이제 와이파이 연결하기 위해서 오픈소스로 가져온것 변형을 잘 시켜보자구~
        }
        public void setItem(ScanResult item){

            tvWifiName.setText(item.SSID);

        }



/**
 *
 void connectWifi() {
 try {
 if (!wifiManager.isWifiEnabled()) {
 wifiManager.setWifiEnabled(true);
 }

 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
 WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
 builder.setSsid("WIFI 이름"); // 연결하고자 하는 SSID
 builder.setWpa2Passphrase("비밀번호"); // 비밀번호

 WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();

 final NetworkRequest.Builder networkRequestBuilder1 = new NetworkRequest.Builder();
 networkRequestBuilder1.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
 networkRequestBuilder1.setNetworkSpecifier(wifiNetworkSpecifier);

 NetworkRequest networkRequest = networkRequestBuilder1.build();
 networkCallback = new ConnectivityManager.NetworkCallback() {
@Override
public void onAvailable(@NonNull Network network) {
super.onAvailable(network);
connectivityManager.bindProcessToNetwork(network);
Toast.makeText(mcontext.getApplicationContext(), "연결됨", Toast.LENGTH_SHORT).show();
}
};

 connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
 connectivityManager.requestNetwork(networkRequest, networkCallback);

 } else {
 WifiConfiguration wifiConfiguration = new WifiConfiguration();
 wifiConfiguration.SSID = String.format("\"%s\"", "wifi 이름"); // 연결하고자 하는 SSID
 wifiConfiguration.preSharedKey = String.format("\"%s\"", "비밂번호"); // 비밀번호
 int wifiId = wifiManager.addNetwork(wifiConfiguration);
 wifiManager.enableNetwork(wifiId, true);
 Toast.makeText(mcontext.getApplicationContext(), "연결됨", Toast.LENGTH_SHORT).show();
 }

 } catch (Exception e) {
 e.printStackTrace();
 Toast.makeText(mcontext.getApplicationContext(), "연결 예외 : " + e.toString(), Toast.LENGTH_SHORT).show();
 }

 }
 void Disconnect() {
 try {
 if (wifiManager.isWifiEnabled()) {

 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
 connectivityManager.unregisterNetworkCallback(networkCallback);
 Toast.makeText(mcontext.getApplicationContext(), "연결 끊김", Toast.LENGTH_SHORT).show();

 } else {
 if (wifiManager.getConnectionInfo().getNetworkId() == -1) {
 Toast.makeText(mcontext.getApplicationContext(), "연결", Toast.LENGTH_SHORT).show();

 } else {
 int networkId = wifiManager.getConnectionInfo().getNetworkId();
 wifiManager.removeNetwork(networkId);
 wifiManager.saveConfiguration();
 wifiManager.disconnect();
 Toast.makeText(mcontext.getApplicationContext(), "연결 끊김", Toast.LENGTH_SHORT).show();
 }
 }

 } else
 Toast.makeText(mcontext.getApplicationContext(), "Wifi 꺼짐", Toast.LENGTH_SHORT).show();

 } catch (Exception e) {
 e.printStackTrace();
 Toast.makeText(mcontext.getApplicationContext(), "연결 해제 예외 : " + e.toString(), Toast.LENGTH_SHORT).show();
 }
 }
 * **/
    }
}

    /**
     * add part _for connect wifi for conntecting button
     * 08_18
     *
     * 일단 permission에 관한것은 무시해줬다
     * setting을 통한 permission allow 해주도록하자.
     *
     * 추가적으로
     *
     * failed
     * 1. 연결이 잘못됐거나
     * 2. adapter에연결하는게 아니거나
     * 위의 2가지 경우중 하나이다.
     *
     * **/

