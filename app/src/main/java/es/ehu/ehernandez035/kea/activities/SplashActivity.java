package es.ehu.ehernandez035.kea.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import es.ehu.ehernandez035.kea.R;

public class SplashActivity extends AppCompatActivity {
    private TextView tv;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv = findViewById(R.id.tv);
        iv = findViewById(R.id.iv);
        //Animation splashAnimation = AnimationUtils.loadAnimation(this, R.anim.splash);

        //tv.startAnimation(splashAnimation);
        //iv.startAnimation(splashAnimation);

        Animator fade = ObjectAnimator.ofFloat(iv, View.ALPHA, 0.0f, 1.0f).setDuration(500);
        ObjectAnimator smallX = ObjectAnimator.ofFloat(iv, View.SCALE_X, 1.0f, 0.9f).setDuration(500);
        ObjectAnimator smallY = ObjectAnimator.ofFloat(iv, View.SCALE_Y, 1.0f, 0.9f).setDuration(500);

        smallX.setRepeatCount(ObjectAnimator.INFINITE);
        smallX.setRepeatMode(ObjectAnimator.REVERSE);

        smallY.setRepeatCount(ObjectAnimator.INFINITE);
        smallY.setRepeatMode(ObjectAnimator.REVERSE);

        AnimatorSet small = new AnimatorSet();
        small.playTogether(smallX, smallY);

        AnimatorSet all = new AnimatorSet();
        all.playSequentially(fade, small);

        all.start();


        final Intent i = new Intent(this, LoginActivity.class);
        Thread timer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                }
            }
        });
        timer.start();

    }
}
