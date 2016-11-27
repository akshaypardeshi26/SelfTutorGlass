package com.example.jack.helloworldActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.example.jack.helloworld.R;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

/**
 * The initial splash screen activity in the game that displays a "Get ready" prompt and allows
 * the user to tap to access the instructions.
 */
public class StartActivity extends Activity {

    /**
     * Handler used to post requests to start new activities so that the menu closing animation
     * works properly.
     */
    private final Handler mHandler = new Handler();

    /**
     * Listener that displays the options menu when the touchpad is tapped.
     */


    //gesture detector for phone
//    private GestureDetector createGestureDetector(Context context) {
//        GestureDetector gestureDetector = new GestureDetector(context);
//        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
//            @Override
//            public boolean onGesture(Gesture gesture) {
//                // Make sure view is initiated
//                // Tap with a single finger for photo
//                if (gesture == Gesture.TAP) {
//                    mAudioManager.playSoundEffect(Sounds.TAP);
//                    openOptionsMenu();
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        return gestureDetector;
//    }


    //original gesture detector
    private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
            if (gesture == Gesture.TAP) {
                mAudioManager.playSoundEffect(Sounds.TAP);
                openOptionsMenu();
                return true;
            } else {
                return false;
            }
        }
    };


    /**
     * Audio manager used to play system sound effects.
     */
    private AudioManager mAudioManager;

    /**
     * Gesture detector used to present the options menu.
     */
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_app);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);
//        mGestureDetector = createGestureDetector(this);

    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.start_app, menu);
        return true;
    }

    /**
     * The act of starting an activity here is wrapped inside a posted {@code Runnable} to avoid
     * animation problems between the closing menu and the new activity. The post ensures that the
     * menu gets the chance to slide down off the screen before the activity is started.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The startXXX() methods start a new activity, and if we call them directly here then
        // the new activity will start without giving the menu a chance to slide back down first.
        // By posting the calls to a handler instead, they will be processed on an upcoming pass
        // through the message queue, after the animation has completed, which results in a
        // smoother transition between activities.
        switch (item.getItemId()) {
            case R.id.tutorial:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startTutorial();
                    }
                });
                return true;

            case R.id.select_from_library:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startLibrary();
                    }
                });
                return true;


            case R.id.take_picture:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        startCamera();
                    }
                });
                return true;



            default:
                return false;
        }
    }

    /**
     * Starts the main game activity, and finishes this activity so that the user is not returned
     * to the splash screen when they exit.
     */
    private void startLibrary() {
        //startActivity(new Intent(this, MainActivity.class));

        Intent intent = new Intent(this, LibraryActivity.class);
        startActivity(intent);
        finish();
    }


    private void startCamera() {
        //startActivity(new Intent(this, MainActivity.class));

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isFrom", "camera");
        startActivity(intent);
        finish();
    }

    /**
     * Starts the tutorial activity, but does not finish this activity so that the splash screen
     * reappears when the tutorial is over.
     */
    private void startTutorial() {
        //startActivity(new Intent(this, MainActivity.class));
    }

}