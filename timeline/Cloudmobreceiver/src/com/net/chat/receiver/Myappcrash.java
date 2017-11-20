package com.net.chat.receiver;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "", // will not be used
mailTo = "reports@yourdomain.com",
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.app_name)

public class Myappcrash extends Application {
   
	 @Override
	  public void onCreate() {
	    // The following line triggers the initialization of ACRA
	    super.onCreate();
	    ACRA.init(this);
	  }

}
