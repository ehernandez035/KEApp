package es.ehu.ehernandez035.kea;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraMailSender;

/**
 * Proba-fasean aplikazioa ixtarazten duten erroreak gertatzen badira, hauen informazioa bidaltzeko mezua
 */
@AcraCore(buildConfigClass = BuildConfig.class)
@AcraMailSender(mailTo = "keaaplikazioa@gmail.com")
public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ACRA.init(this);
    }
}
