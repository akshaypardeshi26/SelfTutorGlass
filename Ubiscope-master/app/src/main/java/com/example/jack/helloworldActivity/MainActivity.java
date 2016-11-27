package com.example.jack.helloworldActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.jack.helloworld.R;
import com.example.jack.helloworldView.CameraView;
import com.google.android.glass.content.Intents;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.Status;


public class MainActivity extends Activity {

    /**
     * {@link CardScrollView} to use as the main content view.
     */
    private CameraView cameraView;
    private GestureDetector mGestureDetector = null;
    private static final int TAKE_PICTURE_REQUEST = 1;
    String drawableString=null;
    boolean isFromCamera=false;
    boolean isFromBoy=false;
    boolean isFromApple=false;
    boolean isFromDance=false;
    boolean isFromGuitar=false;
    boolean isFromFrog=false;
    boolean isCompleted=true;




    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            Log.i("TAG", "OpenCVLoader Failed");
        } else {
            Log.i("TAG", "OpenCVLoader Succeeded");

            Log.i("TAG", "Loading opencv_java3...");
            java.lang.System.loadLibrary("opencv_java3");
            Log.i("TAG", "Finished Loading opencv_java3...");
        }
        //System.loadLibrary("opencv_java3"); //load opencv_java lib
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Initiate CameraView

        drawableString=getIntent().getStringExtra("isFrom");
        isFromCamera=(drawableString.equals("camera"));
        System.out.println("isFromCamera"+isFromCamera);
        isFromApple=(drawableString.equals("apple"));
        System.out.println("isFromApple"+isFromApple);
        isFromBoy =(drawableString.equals("boy"));
        isFromGuitar=(drawableString.equals("guitar"));
        isFromDance=(drawableString.equals("dance"));
        isFromFrog=(drawableString.equals("frog"));
        //isCompleted=false;

        contours = new ArrayList<MatOfPoint>();
        bitMapImage = null;
        destinationImageColor = null;
        bitmapList = null;
        iv1 = null;
        count = 0;
        csize = 0;
        index = 0;
        breakdown = 1;
        remainder = 0;
        toastCount=0;
        isCompleted=true;

        // Turn on Gestures
        mGestureDetector = createGestureDetector(this);
        if(isFromCamera) {
            cameraView = new CameraView(this);
            setContentView(cameraView);
        }
        else
        {
            View view=null;
            switch (drawableString) {
                case "apple":
                     view = new CardBuilder(getApplicationContext(), CardBuilder.Layout.COLUMNS_FIXED)
                            .setText("You have selected " + getIntent().getStringExtra("isFrom"))
                            .setFootnote("Tap to start drawing")
                            .addImage(R.drawable.apple)
                            .getView();
                    setContentView(view);
                    break;

                case "boy":
                     view = new CardBuilder(getApplicationContext(), CardBuilder.Layout.COLUMNS_FIXED)
                            .setText("You have selected " + getIntent().getStringExtra("isFrom"))
                            .setFootnote("Tap to start drawing")
                            .addImage(R.drawable.boy)
                            .getView();
                    setContentView(view);
                    break;
                case "dance":
                    view = new CardBuilder(getApplicationContext(), CardBuilder.Layout.COLUMNS_FIXED)
                            .setText("You have selected " + getIntent().getStringExtra("isFrom"))
                            .setFootnote("Tap to start drawing it")
                            .addImage(R.drawable.dance)
                            .getView();
                    setContentView(view);
                    break;
                case "frog":
                    view = new CardBuilder(getApplicationContext(), CardBuilder.Layout.COLUMNS_FIXED)
                            .setText("You have selected " + getIntent().getStringExtra("isFrom"))
                            .setFootnote("Tap to start drawing it")
                            .addImage(R.drawable.frog)
                            .getView();
                    setContentView(view);
                    break;
                case "guitar":
                    view = new CardBuilder(getApplicationContext(), CardBuilder.Layout.COLUMNS_FIXED)
                            .setText("You have selected " + getIntent().getStringExtra("isFrom"))
                            .setFootnote("Tap to start drawing it")
                            .addImage(R.drawable.guitar)
                            .getView();
                    setContentView(view);
                    break;
            }
        }

        //Log.d("current function: ", getIntent().getStringExtra("curFunc"));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            // Stop the preview and release the camera.
            // Execute your logic as quickly as possible
            // so the capture happens quickly.
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }



    static List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    static Bitmap bitMapImage = null;
    static Mat destinationImageColor = null;
    static List<Bitmap> bitmapList = null;
    ImageView iv1 = null;
    static TextView tv=null;
    static int count = 0;
    static int csize = 0;
    static int index = 0;
    static int breakdown = 1;
    static int remainder = 0;
    static int toastCount=0;


    private GestureDetector createGestureDetector(Context context) {
        GestureDetector gestureDetector = new GestureDetector(context);
        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {

                // Make sure view is initiated

                    // Tap with a single finger for photo
                    if (gesture == Gesture.TAP && isCompleted) {

                        if (null != cameraView) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, TAKE_PICTURE_REQUEST);
                        } else {
                            processPictureWhenReady("library");
                        }

                        return true;
                    }
                    else if(gesture == Gesture.SWIPE_DOWN && isCompleted)
                    {
                            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
                            startActivity(intent);
                            finish();
                            return true;
                    }



                return false;
            }
        });

        return gestureDetector;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isFromCamera&&cameraView != null) {
            cameraView.releaseCamera();
        }

        //setContentView(cameraView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFromCamera&&cameraView != null) {
            cameraView.releaseCamera();
        }
    }


    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        if (mGestureDetector != null)
        {
            return mGestureDetector.onMotionEvent(event);
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            // Handle photos
            if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK)
            {
                String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);

                processPictureWhenReady(picturePath);


            }

            super.onActivityResult(requestCode, resultCode, data);


    }

    private void processPictureWhenReady(final String picturePath) {


        final File pictureFile = new File(picturePath);

        isCompleted = false;

        if (pictureFile.exists() && isFromCamera) {

            System.out.println("picture exists");
            bitMapImage = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());

            drawContours();

        }
        else if (isFromCamera){
            // The file does not exist yet. Before starting the file observer, you
            // can update your UI to let the user know that the application is
            // waiting for the picture (for example, by displaying the thumbnail
            // image and a progress indicator).

            final File parentDirectory = pictureFile.getParentFile();
            FileObserver observer = new FileObserver(parentDirectory.getPath(),
                    FileObserver.CLOSE_WRITE | FileObserver
                            .MOVED_TO) {
                // Protect against additional pending events after CLOSE_WRITE
                // or MOVED_TO is handled.
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        // For safety, make sure that the file that was created in
                        // the directory is actually the one that we're expecting.
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = affectedFile.equals(pictureFile);

                        if (isFileWritten) {
                            stopWatching();

                            // Now that the file is ready, recursively call
                            // processPictureWhenReady again (on the UI thread).
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processPictureWhenReady(picturePath);

                                }
                            });
                        }
                    }
                }
            };
            observer.startWatching();
        }


        else if (isFromApple) {
            bitMapImage = BitmapFactory.decodeResource(getResources(), R.drawable.apple);
            drawContours();

        }  else if (isFromBoy) {
            bitMapImage = BitmapFactory.decodeResource(getResources(), R.drawable.boy);
            drawContours();

        } else if (isFromDance) {
            bitMapImage = BitmapFactory.decodeResource(getResources(), R.drawable.dance);
            drawContours();

        } else if (isFromFrog) {
            bitMapImage = BitmapFactory.decodeResource(getResources(), R.drawable.frog);
            drawContours();

        } else if (isFromGuitar) {
            bitMapImage = BitmapFactory.decodeResource(getResources(), R.drawable.guitar);
            drawContours();

        }
        else {
        //    Toast.makeText(getBaseContext(), "Image does not exist", Toast.LENGTH_SHORT);
        }

        //drawContours();

    }

    private void drawContours() {
        /*int iCannyLowerThreshold = 60, iCannyUpperThreshold = 100;
        Mat matrixFromBitmap = new Mat(bitMapImage.getWidth(), bitMapImage.getHeight(), CvType.CV_8UC4);
        Utils.bitmapToMat(bitMapImage, matrixFromBitmap);
        if(isFromCamera)
        {
            Imgproc.pyrDown(matrixFromBitmap,matrixFromBitmap);
            Imgproc.pyrDown(matrixFromBitmap,matrixFromBitmap);
        }
        Mat threshold = new Mat(matrixFromBitmap.rows(), matrixFromBitmap.cols(), CvType.CV_8UC1);
        Mat destinationImageGrayScale = new Mat(matrixFromBitmap.rows(), matrixFromBitmap.cols(), CvType.CV_8UC1);
        Imgproc.cvtColor(matrixFromBitmap, threshold, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(threshold, threshold, 100, 255, Imgproc.THRESH_BINARY);
        Imgproc.Canny(threshold, threshold, iCannyLowerThreshold, iCannyUpperThreshold);
        //contours=null;
        Imgproc.findContours(threshold, contours, new Mat(), Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        destinationImageColor = new Mat(matrixFromBitmap.rows(), matrixFromBitmap.cols(), CvType.CV_8UC4);
        bitmapList = new ArrayList<Bitmap>();
        Imgproc.cvtColor(destinationImageGrayScale, destinationImageColor, Imgproc.COLOR_GRAY2BGR);
        setContentView(R.layout.main);
        iv1 = (ImageView) findViewById(R.id.imageView1);
        Imgproc.drawContours(destinationImageColor, contours, (0), new Scalar(255, 0, 0), 3);
        //Imgproc.putText(destinationImageColor, ((Integer)1).toString(), new Point(10, 400), 3, 2, new Scalar(255, 0, 0, 255), 3);
        Utils.matToBitmap(destinationImageColor, bitMapImage);
        bitmapList.add(0, bitMapImage);
        iv1.setImageBitmap(Bitmap.createScaledBitmap(bitmapList.get(0), 2000, 2000, false));
        Toast mytoast=Toast.makeText(getApplicationContext(), ((Integer) (1)).toString()+" Swipe right for next", Toast.LENGTH_SHORT);
        mytoast.setGravity(Gravity.BOTTOM|Gravity.RIGHT, 0,0);

        mytoast.show();
        count = 0;
        index = 0;
        */

    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        mImageDetails.setText(R.string.loading_message);

        // Do the real work in an async task, because we need to use the network anyway
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(new
                            VisionRequestInitializer(CLOUD_VISION_API_KEY));
                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

                        // Add the image
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);

                        // add the features we want
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature labelDetection = new Feature();
                            labelDetection.setType("TEXT_DETECTION");
                            labelDetection.setMaxResults(10);
                            add(labelDetection);
                        }});

                        // Add the list of one thing to the request
                        add(annotateImageRequest);
                    }});

                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " +
                            e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                mImageDetails.setText(result);
            }
        }.execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message += String.format("%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
            }
        } else {
            message += "nothing";
        }

        return message;
    }


}
