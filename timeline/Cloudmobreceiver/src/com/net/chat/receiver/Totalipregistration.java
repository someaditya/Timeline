package com.net.chat.receiver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;


import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Totalipregistration  extends Activity{
	          
	EditText mIpet,name; 
	Button mSubmit,mStart;   
	static String mip="";
	ListView lv;   
	ArrayList<String> ipArrays;
	String[] items;
	
	 @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.ipreg);    
	             
	    ReceiveDataforchar();
	    ipArrays = new ArrayList<String>();
	    mIpet = (EditText )findViewById(R.id.editText1);
	    mSubmit = (Button)findViewById(R.id.button1);
	    mStart= (Button)findViewById(R.id.button2);
	    name = (EditText )findViewById(R.id.editText2);           
	    lv = (ListView)findViewById(R.id.lv);
	    mStart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Totalipregistration.this,PlayerViewReceiver.class);
				startActivity(intent);
			}  
		});
	    mSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				
				
				mip = mIpet.getText().toString();
				ipArrays.add(mip+"\n"+name.getText().toString());
				items = ipArrays.toArray(new String[ipArrays.size()]);
				 
				ArrayAdapter<String> aa = new ArrayAdapter<String>(Totalipregistration.this, android.R.layout.simple_list_item_1,items);
				lv.setAdapter(aa);
				
//				Intent intent = new Intent(Totalipregistration.this,PlayerViewReceiver.class);
//				startActivity(intent);
			}
		});
	    
	       
	    lv.setOnItemClickListener(new OnItemClickListener() {

	 			@Override
	 			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
	 					long arg3) {    
	 				// TODO Auto-generated method stub
	 				
	 				Toast.makeText(getApplicationContext(), ""+ipArrays.get(arg2), 600000).show();
	 				
	 				final String[] temp = ipArrays.get(arg2).split("\n");
	 				
	 				new Thread(){
	 					@Override  
	 					public void run(){
	 						Connectsocket(temp[0]);
	 					}
	 				}.start();
	 				
	 			}   
	 		});
	    
	       
	 }
	    
	 public String getLocalIpAddress1() {
		    try {
		        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
		            NetworkInterface intf = en.nextElement();
		            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
		                InetAddress inetAddress = enumIpAddr.nextElement();
		                if (!inetAddress.isLoopbackAddress()) {
		                    String ip = Formatter.formatIpAddress(inetAddress.hashCode());
		                    Log.i("ip", "***** IP="+ ip);
		                    return ip;
		                }
		            }   
		        }
		    } catch (SocketException ex) {
		        Log.e("ip", ex.toString());
		    }
		    return null;
		}
	 
	       
	  public void Connectsocket(String ip){
          
			try {        
				
				Socket socket = new Socket(ip, 7777);
				DisplayToast("Socket connected");
				//ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
				DataOutputStream o1 = new DataOutputStream(socket.getOutputStream());
				DisplayToast("Writing data");                         
				o1.writeUTF("OPENCHAT");
				
				
				WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
				String ipaddr = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());   
				o1.writeUTF(ipaddr);
				DisplayToast("Writen data");                        
				             
				o1.close();        
				socket.close();    
				
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			
			                
		} 
	  
	 
	 String chatdata="";
	  
	  ServerSocket sschat;
	  DataInputStream oinchat;
	  Socket socketchat;
	     
	  public void ReceiveDataforchar(){
		        
	      new Thread(){
	    	  
		      @Override
		      public void run()
		  	  {      
		  		try   
		  		{              
		  		           
//		  			sschat=new ServerSocket(4444);
//		  			socketchat=sschat.accept();   
//		  			DisplayToast("Socket connected");
//		  			oinchat=new DataInputStream(socketchat.getInputStream());
		  			while(true)       
		  			{
		  				       
		  				       
		  				sschat=new ServerSocket(7777);
			  			socketchat=sschat.accept();   
			  			DisplayToast("Socket connected");
			  			oinchat=new DataInputStream(socketchat.getInputStream());
			  			
		  			System.out.println("1");
		  			//soc=ss.accept();
		  			System.out.println("2");
		     			//oos=new ObjectInputStream(soc.getInputStream());
		  		    System.out.println("3");    
		  		                                                   
		  		                                                                              
		  		    try{   
		  		    	DisplayToast("Reading data");            
		  		    	chatdata = oinchat.readUTF();
		  		    	String ip = oinchat.readUTF();
		  		    	Totalipregistration.mip = ip;     
		  		    	if(chatdata.equalsIgnoreCase("OPENCHAT")){
		  		    		
		  		    		    
		  		    		  Intent intent = new Intent(Totalipregistration.this,PlayerViewReceiver.class);
		  		    		  startActivity(intent);
		  		    	}       
		  		    	System.out.println("Received data:: "+chatdata); 
		  		    	DisplayToast("Received data:: "+chatdata);
		  		    	
		  		    	              
		  		    }catch (Exception e) {
							// TODO: handle exception
		  		    	System.out.println("problem in read");
		  		    	e.printStackTrace();
		  		    	break;
					}     

		  		  sschat.close();
		  		  socketchat.close();
		  		  oinchat.close();
		                         
		             System.out.println("55555555555");
		  		        
		  			}    
		  		                                                    
		  			}catch(Exception e){        
		  				System.out.println("EEEEEEEEEEEEE"+e.getMessage());
		  				e.printStackTrace();
		  			}
		  		        
		  		}   
	      }.start();
	  }
	  
	  
	  public void DisplayToast(final String message){
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), message, 600000000).show();
				}
			});
		}      
	       

}
