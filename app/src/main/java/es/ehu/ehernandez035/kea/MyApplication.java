package es.ehu.ehernandez035.kea;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraMailSender;

@AcraCore(buildConfigClass = BuildConfig.class)
//@AcraHttpSender(uri="http://elenah.duckdns.org/crash.php", httpMethod = HttpSender.Method.POST)
@AcraMailSender(mailTo = "keaaplikazioa@gmail.com")
public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this);
//        builder.setBuildConfigClass(BuildConfig.class).setReportFormat(StringFormat.JSON);
//        builder.getPluginConfigurationBuilder(HttpSenderConfigurationBuilder.class).setUri("http://elenah.duckdns.org/crash.php").setEnabled(true).setHttpMethod(HttpSender.Method.POST);
        ACRA.init(this);
    }
}
