package com.coolweather.app.activity;

import com.coolweather.app.R;
import com.coolweather.app.R.id;
import com.coolweather.app.R.layout;
import com.coolweather.app.R.menu;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {
	
	private LinearLayout weatherInfoLayout;
	/**
	 *������ʾ������ 
	 */
	private TextView cityNameText;
	/**
	 *������ʾ����������Ϣ 
	 */
	private TextView weatherDespText;
	/**
	 * 
	 *������ʾ��������ʱ�� 
	 */
	private TextView publishText;
	/**
	 *������ʾ����1 
	 */
	private TextView temp1Text;
	/**
	 *������ʾ����2 
	 */
	private TextView temp2Text;
	/**
	 *������ʾ��ǰ���� 
	 */
	private TextView currentDateText;
	/**
	 *�л����а�ť 
	 */
	private Button switchCity;
	/**
	 *�������� ��ť
	 */
	private Button refreshWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		System.out.println("AAAA");
		setContentView(R.layout.weather_layout);
		System.out.println("AA");
		weatherInfoLayout=(LinearLayout)findViewById(R.id.weather_info_layout);
		publishText=(TextView)findViewById(R.id.publish_text);
		cityNameText=(TextView)findViewById(R.id.city_name);
		weatherDespText=(TextView)findViewById(R.id.weather_desp);
		temp1Text=(TextView)findViewById(R.id.temp1);
		temp2Text=(TextView)findViewById(R.id.temp2);
		currentDateText=(TextView)findViewById(R.id.current_date);
		String countryCode=getIntent().getStringExtra("country_code");
		if(!TextUtils.isEmpty(countryCode)){
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.VISIBLE);
			cityNameText.setVisibility(View.VISIBLE);
			queryWeatherCode(countryCode);
			System.out.println("country is"+countryCode);
		}else{
			showWeather();
		}
		switchCity=(Button)findViewById(R.id.switch_city);
		refreshWeather=(Button)findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}
	/**
	 *��ѯ�ؼ���������Ӧ���������� 
	 */
	private void queryWeatherCode(String countryCode){
		String address="http://m.weather.com.cn/data5/city"+countryCode+".xml";
		queryFromServer(address,"countryCode");
		System.out.println("------");
	}
	/**
	 *��ѯ������������Ӧ������ 
	 */
	private void queryWeatherInfo(String weatherCode){
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		System.out.println(weatherCode);
		queryFromServer(address,"weatherCode");
	}
	/**
	 *���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż�������Ϣ 
	 */
	private void queryFromServer(final String address,final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				if("countryCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						String[] array=response.split("\\|");
						if(array!=null&&array.length==2){
							String weatherCode=array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					System.out.println("weatherCode=type");
					Utility.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
							System.out.println("dfsdfsdfsdfsefs");
						}
						
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("ͬ��ʧ��");
					}
					
				});
			}
			
		});
	}
	private void showWeather(){
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));
		temp1Text.setText(prefs.getString("temp1", ""));
		temp2Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		publishText.setText("����"+prefs.getString("publish_time","")+"����");
		currentDateText.setText(prefs.getString("current_date",""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent=new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("ͬ����...");
			SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode=prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}
}
