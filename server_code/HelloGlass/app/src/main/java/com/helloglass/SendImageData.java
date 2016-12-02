package com.helloglass;

/**
 * Created by akshaypardeshi on 29/11/16.
 */
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.*;
import android.provider.MediaStore;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.AudioManager;
import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import org.json.*;
import org.json.simple.parser.*;
import org.apache.http.*;
import org.apache.http.message.*;
import java.util.*;


public class SendImageData extends AsyncTask<String, String, String> {

    public String url="http://192.168.43.110:5000/test";
    //public String url="http://httpbin.org/post";

    JSONParser jsonParser = new JSONParser();
    JSONObject obj =new JSONObject();
    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    public String doInBackground(String... picturePath) {
        ////////////////////////////////////////////
        //read image and convert it to string
        final File pictureFile = new File(picturePath[0]);
        System.out.println("Picturefile path is");
        System.out.println(picturePath[0]);
        Bitmap image_bitmap=null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        try {
            image_bitmap = BitmapFactory.decodeStream(new FileInputStream(pictureFile), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image_bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
        byte [] byte_arr = stream.toByteArray();
        String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);

        /////////////////////////////////////////////////////////

        System.out.println("inside dobackground");
        String name = "image_data";
        //String image_str = inputImg.getText().toString();

        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("name", image_str));
        //params.add(new BasicNameValuePair("img",image_str));
        //params.add(new BasicNameValuePair("image", image));
        System.out.println("inside dobackground1");
        // getting JSON Object
        // Note that create product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url,
                "POST", params);

        System.out.println("inside dobackground2");
        // check for success tag
       // try {
/*            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                System.out.println("inside dobackground3");
                // successfully created product
                // Intent i = new Intent(getApplicationContext(), AllProductsActivity.class);
                //startActivity(i);

                // closing this screen
                //finish();
            } else {
                System.out.println("inside dobackground4");
                // failed to create product
            }
            */
  //      } catch (JSONException e) {
    //        System.out.println("inside dobackground5");
      //      e.printStackTrace();
       // }

        return null;
    }

    protected void onPostExecute(String file_url) {
        // dismiss the dialog once done
        System.out.println("creation finished");
        //pDialog.dismiss();
    }

}
