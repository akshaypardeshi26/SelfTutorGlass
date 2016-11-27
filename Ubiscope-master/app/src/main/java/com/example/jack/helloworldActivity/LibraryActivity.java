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
import android.view.View;

import com.example.jack.helloworld.R;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;

/**
 * The initial splash screen activity in the game that displays a "Get ready" prompt and allows
 * the user to tap to access the instructions.
 */
public class LibraryActivity extends Activity {

    String objectString=null;

    /**
     * Handler used to post requests to start new activities so that the menu closing animation
     * works properly.
     */
    private final Handler mHandler = new Handler();

    /**
     * Listener that displays the options menu when the touchpad is tapped.
     */

    //gesture detector for glass
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
        View view = new CardBuilder(getApplicationContext(), CardBuilder.Layout.COLUMNS_FIXED)
                .setText("Welcome little artist :)")
                .setFootnote("Please tap to select image")
                .addImage(R.drawable.artist)
                .getView();
        setContentView(view);
        //R.layout.select_image_app
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener); //for glass
//        mGestureDetector = createGestureDetector(this);  //for phone

    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.select_image, menu);
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
            case R.id.apple:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        objectString="apple";
                        startDrawing(objectString);

                    }
                });
                return true;

            case R.id.boy:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        objectString="boy";
                        startDrawing(objectString);
                    }
                });
                return true;

            case R.id.dance:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        objectString="dance";
                        startDrawing(objectString);
                    }
                });
                return true;

            case R.id.frog:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        objectString="frog";
                        startDrawing(objectString);
                    }
                });
                return true;

            case R.id.guitar:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        objectString="guitar";
                        startDrawing(objectString);
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

    private void startDrawing(String objectString)
    {
        Intent intent = new Intent(this, MainActivity.class);

            switch (objectString) {
                case "apple":
                    intent.putExtra("isFrom", objectString);
                    System.out.println("apple name :"+objectString);
                    break;
                case "boy":
                    intent.putExtra("isFrom", objectString);
                    break;
                case "dance":
                    intent.putExtra("isFrom", objectString);
                    break;
                case "frog":
                    intent.putExtra("isFrom", objectString);
                    break;
                case "guitar":
                    intent.putExtra("isFrom", objectString);
                    break;
            }


        startActivity(intent);
        finish();
    }






}