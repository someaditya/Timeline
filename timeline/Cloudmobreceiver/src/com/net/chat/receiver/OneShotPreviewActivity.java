package com.net.chat.receiver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class OneShotPreviewActivity extends Activity {
    /** Called when the activity is first created. */
    private Camera _camera;
    private Activity _activity;
    byte[] mydata;
    int mPreviewImageFormat=ImageFormat.NV21;
    static OneShotPreviewActivity os;
    SharedPreferences sp;
    int count=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	_activity = this;
        super.onCreate(savedInstanceState);
               
        os = this;
        
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new CameraPreview(this));
    }
    private Camera.PreviewCallback mPreviewListener = new Camera.PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {

			Camera.Parameters parameters = _camera.getParameters(); 
	        Size size = parameters.getPreviewSize(); 
	        YuvImage image = new YuvImage(data, parameters.getPreviewFormat(), 
	                size.width, size.height, null); 
	        
	        ByteArrayOutputStream bos=new ByteArrayOutputStream();
	        
	        image.compressToJpeg( 
	                new Rect(0, 0, image.getWidth(), image.getHeight()), 90, 
	                bos); 
	        final byte[] myimage=bos.toByteArray();
	    	FileOutputStream outStream = null;   
		        
			try {      
				 
				System.out.println("the data is to be "+data);
			    FileOutputStream outStream1 = new FileOutputStream("/sdcard/daya.jpg");//String.format("/sdcard/daya.jpg", System.currentTimeMillis()));	
				outStream1.write(myimage);
				outStream1.close();
				Log.d("Camera", "onPictureTaken - wrote bytes: " + data.length);
				      
			} catch (FileNotFoundException e) {   
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
				          
				                   
			} finally {
				
			}                  
			new Thread(){
				@Override
				public void run(){
					Connectsocket(myimage);
				}
			}.start();
			
			 
				   
				if(_camera!=null){
	        		
	        		_camera.stopPreview();
	                _camera.release();   
	                _camera=null;
	        	}
			
			finish();      
			//takepicture();
			   
	}        
             
    };
    
    public void DisplayToast(final String message){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), message, 600000000).show();
			}   
		});
	}     
    
    
    public void Connectsocket(byte[] data){
    		    
  		try {     
  			
  			Socket socket = new Socket(Totalipregistration.mip, 8888);
  			DisplayToast("Socket connected");
  			//ObjectInputStream oin = new ObjectInputStream(socket.getInputStream());
  			ObjectOutputStream o1 = new ObjectOutputStream(socket.getOutputStream());
  			DisplayToast("Writing data");
  			o1.writeObject(data);             
  			DisplayToast("Writen");
  			//oin.close();                  
  			o1.close();     
  			socket.close();         
  		} catch (UnknownHostException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  			DisplayToast(e.getMessage());
  		} catch (IOException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  			DisplayToast(e.getMessage());
  		} 
  		                        
  	}      
    
	public void takepicture(){
		_camera.setOneShotPreviewCallback(mPreviewListener);
	} 
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if (event.getAction() == MotionEvent.ACTION_DOWN) {
    	//	_camera.setOneShotPreviewCallback(mPreviewListener);
    	}
    	
		return super.onTouchEvent(event); 
	}
    
    public void setCameraDisplayOrientation(android.hardware.Camera camera, int cameraId) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        
        android.hardware.Camera.getCameraInfo(cameraId, info);
        
        int rotation = _activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;     
        switch (rotation) {   
            case Surface.ROTATION_0: degrees = 0; break;    
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break; 
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
        	
            result = (info.orientation - degrees + 360) % 360;
        }
        
		Log.d("camera", "result= " + result);
		camera.setDisplayOrientation(result);
    }
    
    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
            Camera.getCameraInfo( camIdx, cameraInfo );
            if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
                try {
                    cam = Camera.open( camIdx );
                } catch (RuntimeException e) {
                    Log.e("camera", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }   
    
        return cam;
    }
    
    
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    	
        private SurfaceHolder holder;
     
        CameraPreview(Context context) {
            super(context);
            holder = getHolder();
            holder.addCallback(this);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
     
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    		configure( format,  width, height) ;
    		
    		try {
				Thread.sleep(1000);
				_camera.setOneShotPreviewCallback(mPreviewListener);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		
        }
        
        public void surfaceDestroyed(SurfaceHolder holder) {
        	if(_camera!=null){
        		
        		_camera.stopPreview();
                _camera.release();
                
                _camera=null;
        	}
                        
        }

		public void surfaceCreated(SurfaceHolder holder) {
			       
			//_camera = Camera.open();
			_camera = openFrontFacingCameraGingerbread();
			try {
				_camera.setPreviewDisplay(holder);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			setCameraDisplayOrientation(_camera, 0);
			_camera.startPreview();
			
			 
		}
		protected void setPictureFormat(int format) {
			Camera.Parameters params = _camera.getParameters();
			List<Integer> supported = params.getSupportedPictureFormats();
			if (supported != null) {
				for (int f : supported) {
					if (f == format) {
						params.setPreviewFormat(format);
						_camera.setParameters(params);
						break;
					}
				}
			}
		}
		
		protected void setPreviewSize(int width, int height) {
			Camera.Parameters params = _camera.getParameters();
			List<Camera.Size> supported = params.getSupportedPreviewSizes();
			if (supported != null) {
				for (Camera.Size size : supported) {
					if (size.width <= width && size.height <= height) {
						params.setPreviewSize(size.width, size.height);
						_camera.setParameters(params);
						
						break;
					}   
				}
			}
		}
		public void configure(int format, int width, int height) {
			_camera.stopPreview();
			setPictureFormat(format);
			setPreviewSize(width, height);
			_camera.startPreview();
		}
    }    
    
    
    
    

}