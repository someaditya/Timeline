

package com.net.chat.receiver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayerView;



    

  
public class PlayerViewReceiver extends YouTubeFailureRecoveryActivity {
       
  YouTubePlayer playerView;
  ServerSocket ss;       
  Socket soc;
  ObjectOutputStream oos1;
  ObjectInputStream oos;
  String mVideoid="";	     
  MyPlayerStateChangeListener playerStateChangeListener;
  TextView mStatus;
  YouTubePlayerView youTubeView;
  TextView mChatinfo,mSendertext;
  EditText textdata,mIP;    
  Button sendt;
  
  static Timer timer;  
  static MyTimerTask myTimerTask;   
  
  static LocationManager locationManager;
  static MyLocationListener locationListener;
  SharedPreferences sp;
  Button panic;
  
  static ArrayList<byte[]> imageData;
  ImageView iv1,iv2,iv3,iv4;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.playerview_demo);
          
    
    imageData = new ArrayList<byte[]>();
    
    imageData.add(null);
    imageData.add(null);
    imageData.add(null);
    imageData.add(null);
    
	sp = PreferenceManager.getDefaultSharedPreferences(this);
	
	panic = (Button) findViewById(R.id.panic);   
    mStatus = (TextView) findViewById(R.id.status);
    mChatinfo = (TextView) findViewById(R.id.chatinfo);
    mSendertext = (TextView) findViewById(R.id.senderchat);
    textdata = (EditText) findViewById(R.id.textdata);
    mIP = (EditText) findViewById(R.id.ip);  
    sendt = (Button) findViewById(R.id.button);   
              
    youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
            
    playerStateChangeListener = new MyPlayerStateChangeListener();
        
    
    timer = new Timer();   
    myTimerTask = new MyTimerTask();
    //timer.schedule(myTimerTask, 1000);
    
    timer.schedule(myTimerTask, 5000, 20000);
    
    Database dat = new Database(getBaseContext());
    SQLiteDatabase db = dat.getWritableDatabase();
    
    
    Cursor c1 = db.rawQuery("select * from USER where IP='"+Totalipregistration.mip+"'", null); 
          
    if(c1.getCount()!=0){
    	
    	c1.moveToFirst();
    	String sent = c1.getString(c1.getColumnIndex("SENDING"));
    	String receive = c1.getString(c1.getColumnIndex("RECEIVING"));
    	      
    	mChatinfo.setText(sent);
    	mSendertext.setText(receive);
    
    }
    c1.close();
    db.close();
    
    ReceiveDataforchar();
    ReceiveData();              
    
    panic.setOnClickListener(new OnClickListener() {
		    
		@Override    
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
	 	     
	 	    final Criteria criteria = new Criteria();
            
        	
        	locationManager = (LocationManager) PlayerViewReceiver.this.getSystemService(Context.LOCATION_SERVICE);
    		final String bestProvider = locationManager.getBestProvider(criteria, true);
    		    
    		    
        	         
			Toast.makeText(PlayerViewReceiver.this, ""+bestProvider, 600000).show();
                  
        	locationListener=new MyLocationListener();
            locationManager.requestLocationUpdates(
             		bestProvider, 
             		0, 
             		0,
             		locationListener);   
            
	 	     
		}
	});
    
    
    sendt.setOnClickListener(new OnClickListener() {
		   
		@Override   
		public void onClick(View v) {
			// TODO Auto-generated method stub   
			   
			//mSendertext.setText(mSendertext.getText().toString()+"\n"+textdata.getText().toString());
			new Thread(){
				@Override
				public void run(){
					ConnectsocketChat();
				}   
			}.start();
		}   
	});          
  }               
  
  public void onResume(){
	  super.onResume();
	  
	  DisplayImage();
  }
  
  public void DisplayImage(){
	  
	  Toast.makeText(getApplicationContext(), "displaying image", 60000000).show();
	  iv1 = (ImageView) findViewById(R.id.iv1);
	  iv2 = (ImageView) findViewById(R.id.iv2);
	  iv3 = (ImageView) findViewById(R.id.iv3);
	  iv4 = (ImageView) findViewById(R.id.iv4);
	  
	  for(int i =0;i<4;i++){
		  byte[] val = imageData.get(i);
		     
		  if(i==0){
			  if(val==null){
				  DisplayToast("iv1 is null");
				  iv1.setImageResource(R.drawable.ic_launcher);
			  }else{
				  Bitmap bitmap = BitmapFactory.decodeByteArray(val, 0, val.length);
				  iv1.setImageBitmap(bitmap);
			  }   
		  }else if(i==1){
			  if(val==null){
				  DisplayToast("iv2 is null");
				  iv2.setImageResource(R.drawable.ic_launcher);
			  }else{
				  Bitmap bitmap = BitmapFactory.decodeByteArray(val, 0, val.length);
				  iv2.setImageBitmap(bitmap);
			  }
		  }else if(i==2){
			  if(val==null){
				  DisplayToast("iv3 is null");
				  iv3.setImageResource(R.drawable.ic_launcher);
			  }else{
				  Bitmap bitmap = BitmapFactory.decodeByteArray(val, 0, val.length);
				  iv3.setImageBitmap(bitmap);
			     }   
		  	  
		  }else if(i==3){
			  if(val==null){
				  DisplayToast("iv4 is null");
				  iv4.setImageResource(R.drawable.ic_launcher);
			  }else{
				  Bitmap bitmap = BitmapFactory.decodeByteArray(val, 0, val.length);
				  iv4.setImageBitmap(bitmap);
			  }
		  }
		  
	  }
  }
  String[] temp;     
  @Override
  public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
      boolean wasRestored) {
	  this.playerView = player;      
	 // playerView.setPlayerStateChangeListener(playerStateChangeListener);
	                     
    if (!wasRestored) {     
    	//playerView.cueVideo("wKJ9KzGQq0w");
    //	temp = mVideoid.split(" ");
    //	playerView.cueVideo(temp[0]);   
    }     
                  
  }   
      
  @Override
  protected YouTubePlayer.Provider getYouTubePlayerProvider() {
    return (YouTubePlayerView) findViewById(R.id.youtube_view);
  }

  
  private final class MyPlayerStateChangeListener implements PlayerStateChangeListener {
	    String playerState = "UNINITIALIZED";

	    @Override   
	    public void onLoading() {
	      playerState = "LOADING";
	      Toast.makeText(getApplicationContext(), "on loading", 6000000).show();
	     
	    }
    
	    @Override
	    public void onLoaded(String videoId) {
	      playerState = String.format("LOADED %s", videoId);
	      
	      playerView.seekToMillis(Integer.parseInt(temp[1]));
	      Toast.makeText(getApplicationContext(), "on loaded "+temp[1], 6000000).show();
	    }   
              
	    @Override
	    public void onAdStarted() {
	      playerState = "AD_STARTED";
	      
	    }

	    @Override
	    public void onVideoStarted() {
	      playerState = "VIDEO_STARTED";
	      Toast.makeText(getApplicationContext(), "on video started", 6000000).show();
	      playerView.seekToMillis(10*1000);
	    }

	    @Override
	    public void onVideoEnded() {
	      playerState = "VIDEO_ENDED";
	      Toast.makeText(getApplicationContext(), "on video ended", 6000000).show();
	    }

	    @Override
	    public void onError(ErrorReason reason) {
	      playerState = "ERROR (" + reason + ")";
	      if (reason == ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) {
	        // When this error occurs the player is released and can no longer be used.
	    	  playerView = null;
	           
	      }   
	           
	    }

	  }
     
  
  public void insertImageData(byte[] imgraw){
	  
	  
	  for(int i=4;i>0;i--){
		
		  if(i==1){
			  imageData.add(0, imgraw);
		  }else{
			  byte[] pInex = imageData.get(i-1);
			  imageData.add(i, pInex);
		  }
		        
	  }
  }    
  public void insertorupdatedb(String ip, boolean isSender,String data){
		 
		Database dat=new Database(this);
		SQLiteDatabase db=dat.getWritableDatabase();
	    
		Cursor c = db.rawQuery("select * from USER where IP='"+ip+"'", null);
		if(c.getCount()==0){
		               	         
			ContentValues cv = new ContentValues();  
			cv.put("IP", ip);    
			if(isSender){   
				cv.put("SENDING", data);
				cv.put("RECEIVING", "");
			}else{
				cv.put("RECEIVING", data);
				cv.put("SENDING", "");
			}   
			
			db.insert("USER", null, cv);
		}else if(c.getCount()!=0){
			c.moveToFirst();
			String sent = c.getString(c.getColumnIndex("SENDING"));
			String received = c.getString(c.getColumnIndex("RECEIVING"));
			
			ContentValues cv = new ContentValues();  
			    
			if(isSender){   
				cv.put("SENDING", sent+"\n"+data);
				cv.put("RECEIVING", received+"");
			}else{   
				cv.put("RECEIVING", received+"\n"+data);
				cv.put("SENDING", sent+"");
			}   
			db.update("USER", cv, "IP='"+ip+"'", null);
			          
		}   
		
		c.close();
		db.close();
  }
  
  
  public void ReceiveData(){
	      
      new Thread(){
    	     
	      @Override
	      public void run()
	  	  {   
	  		try   
	  		{   
	  		     
	  	      ss=new ServerSocket(6666);
	  	      
	  			while(true)
	  			{
	  				    
	  		   soc=ss.accept();
	  	  	   DisplayToast("Socket connected");
	  	  	   oos=new ObjectInputStream(soc.getInputStream());
	  	  	      
	  			System.out.println("1");
	  			//soc=ss.accept();
	  			System.out.println("2");
	     			//oos=new ObjectInputStream(soc.getInputStream());
	  		    System.out.println("3");    
	  		                                             
	  		                                                                    
	  		    try{          
	  		    	            
	  		    	DisplayToast("Reading videoid");
	  		    	byte[] imagedata = (byte[])oos.readObject();
	  		    	DisplayToast("Read image");
	  		    	
	  		    	insertImageData(imagedata);
	  		    	
	  		    	runOnUiThread(new Runnable() {
						                             
						@Override      
						public void run() {
							// TODO Auto-generated method stub
							DisplayImage();
						}                     
					});  
	  		    	oos.close();  
	  		    	soc.close();  
	  		    	    
	  		    	
	  		    }catch (Exception e) {
						// TODO: handle exception
	  		    	e.printStackTrace();
	  		    	DisplayToast("Inwhile:; "+e.getMessage());
	  		    
	  		    	//System.out.println("problem in read ");
				}      
	  		   
	                         
	             
	  		
	  			}    
	  		                                                    
	  			}catch(Exception e){        
	  				System.out.println("EEEEEEEEEEEEE"+e.getMessage());
	  				e.printStackTrace();
	  				DisplayToast(e.getMessage());
	  			}
	  		        
	  		}   
      }.start();
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
	  		           
//	  			sschat=new ServerSocket(4444);
//	  			socketchat=sschat.accept();   
//	  			DisplayToast("Socket connected");
//	  			oinchat=new DataInputStream(socketchat.getInputStream());
	  			while(true)       
	  			{
	  				       
	  				
	  				sschat=new ServerSocket(4444);
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
	  		    	System.out.println("Received data"+chatdata); 
	  		    	DisplayToast("Received data:: "+chatdata);
	  		    	runOnUiThread(new Runnable() {
						                                  
						@Override      
						public void run() {
							// TODO Auto-generated method stub
						      
							insertorupdatedb(Totalipregistration.mip, false, chatdata);
							mChatinfo.setText(mChatinfo.getText().toString()+"\n"+chatdata);
							                                     
						}         
					});
	  		    	            
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
       
  public void ConnectsocketChat(){
            
		try {        
			
			Socket socket = new Socket(Totalipregistration.mip, 1234);
			DisplayToast("Socket connected");
			//ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream o1 = new ObjectOutputStream(socket.getOutputStream());
			DisplayToast("Writing data");                         
			o1.writeObject(textdata.getText().toString());         
			DisplayToast("Writen data");                        
			             
			o1.close();        
			socket.close();    
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub  
					   
					insertorupdatedb(Totalipregistration.mip,true,textdata.getText().toString());
					mSendertext.setText(mSendertext.getText().toString()+"\n"+textdata.getText().toString());
				}
			});
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		                
	} 
  
  
  private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			     
			
		 Toast.makeText(PlayerViewReceiver.this, "Got location", 600000).show();
		 SmsManager smanager = SmsManager.getDefault();    
		     
		 String FirstNo = sp.getString("First No",null);
         String SecondNo = sp.getString("Second No",null);
   	     String PoliceNo = sp.getString("Police No",null);
	        
   	     smanager.sendTextMessage(FirstNo, null, "I am in problem please help me, follow my location"+location.getLatitude()+","+location.getLongitude(), null, null);
   	     smanager.sendTextMessage(SecondNo, null, "I am in problem please help me, follow my location"+location.getLatitude()+","+location.getLongitude(), null, null);
   	     smanager.sendTextMessage(PoliceNo, null, "I am in problem please help me, follow my location"+location.getLatitude()+","+location.getLongitude(), null, null);
            
			
   	     locationManager.removeUpdates(locationListener);
   	     locationManager = null;
   	     locationListener=null;
		}

		public void onStatusChanged(String s, int i, Bundle b) {
		
			
					
		}

		public void onProviderDisabled(String s) {
			
			
		}

		public void onProviderEnabled(String s) {
			
			
		}

	}
  
  @Override
  public void onDestroy(){
	  super.onDestroy();   
	  if(timer!=null){
      	timer.cancel();
      	timer.purge();
      	timer = null;
      	myTimerTask=null;
      }
  }
  class MyTimerTask extends TimerTask {

	  @Override
	  public void run() {
	        
		 
		  Handler h = new Handler(PlayerViewReceiver.this.getMainLooper());
		  h.post(new Runnable() {
		        @Override
		        public void run() {
		        	     
				 	      Intent intent = new Intent(PlayerViewReceiver.this,OneShotPreviewActivity.class);
				 	      startActivity(intent);
		        	
		        }
		    });
		    
	  
	   
	  }
  }
	  
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
  	menu.add(Menu.NONE, 0, 0, "Show settings");
 // 	menu.add(Menu.NONE, 1, 1, "Facebook settings");
  	return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
  	switch (item.getItemId()) {
  		case 0:
  			startActivity(new Intent(this, Setting.class));
  			return true;
  		case 1:
  		//	startActivity(new Intent(this, TestConnect.class));
  	}
  	return false;
  }
  
}

