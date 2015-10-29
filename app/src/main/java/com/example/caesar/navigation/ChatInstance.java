package com.example.caesar.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.goebl.david.Webb;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatInstance extends Activity {


    List<String> roomList;
    ImageButton Home;
    ListView listview;
    TextView testmsg;
    EditText chat_text;
    String roomnum;
    JSONArray data;
    private boolean mTyping = false;
    static final String TAG = "GCMDemo";
    static final int REQUEST_CODE = 1;
    Button SEND;
    Button exitbtn;
    Button picbtn;
    TextView latestMsg;
    ImageButton great;
    boolean position = false;
    ChatAdapter adapter;
    Context ctx = this;
    public Socket mSocket;
    private Context mContext;
    private Handler mTypingHandler = new Handler();
    String regid;
    ImageView newImage;
    String mCurrentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;



    {
        try {
            mSocket = IO.socket("https://great-sarodh.c9.io/");
        }
        catch (URISyntaxException e){}
    }



    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);

        roomList = new ArrayList<String>();
        picbtn = (Button) findViewById(R.id.bn);
        final String senderID = "1048700326431";
        mContext = this;
        newImage = (ImageView) findViewById(R.id.imageView2);

        SharedPreferences appPrefs = mContext.getSharedPreferences("com.example.gcmclient_preferences", Context.MODE_PRIVATE);
        String token = appPrefs.getString("token", "");
        Log.i(TAG, "Token: " + token);
        //   MultipartEntityBuilder builder = MultipartEntityBuilder.create();


        //assert token != null;
        if (token.isEmpty()) {
            try {
                getGCMToken(senderID); // Project ID: xxxx Project Number: 0123456789 at https://console.developers.google.com/project/...
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        listview = (ListView) findViewById(R.id.chat_list_view);
        chat_text = (EditText) findViewById(R.id.chatTxt);
        latestMsg = (TextView) findViewById(R.id.messageTV);

     //   testmsg = (TextView) findViewById(R.id.livestreamTV);
        SEND = (Button) findViewById(R.id.send_button);
      //  adapter = new ChatAdapter(ctx,R.layout.activity_chat);





        mSocket.connect();
        mSocket.on("Chatroom", myMessage);
        mSocket.on("typing",onTyping);
        mSocket.on("new picture",onPictureReceived);

        listview.setAdapter(adapter);


        chat_text.addTextChangedListener(new TextWatcher(){
@Override
public void beforeTextChanged(CharSequence s,int start,int count,int after){

        }

@Override
public void onTextChanged(CharSequence s,int start,int before,int count){
        if(!mTyping){
        mTyping=true;
        mSocket.emit("typing",true);
        }
        mTypingHandler.removeCallbacks(onTypingTimeout);
        mTypingHandler.postDelayed(onTypingTimeout,600);
        }

@Override
public void afterTextChanged(Editable s){

        }
        });
        listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        adapter.registerDataSetObserver(new DataSetObserver(){
@Override
public void onChanged(){
        super.onChanged();
        listview.setSelection(adapter.getCount()-1);
        }
        });



        SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    roomnum = extras.getString("roomnum");
                    roomList.add(roomnum);
                }
                String mymessage = chat_text.getText().toString().trim();
                chat_text.setText(" ");
                mTyping = false;

                int i = adapter.getCount() - 1;

                Log.i(TAG, "THE ADAPTER COUNT: " + i);

                if (i > 0) {
                    DataProvider d = adapter.getItem(i);
                    if (d.getType() == DataProvider.TYPE_ACTION) {
                        Log.i(TAG, "its a typing msg");
                        adapter.chat_list.remove(i);
                        adapter.notifyDataSetChanged();
                        mSocket.emit("Chatroom", mymessage);
                        adapter.add(new DataProvider(position, mymessage, DataProvider.TYPE_MESSAGE, false));
                        adapter.notifyDataSetChanged();
                        adapter.add(new DataProvider(position, "Typing...", DataProvider.TYPE_ACTION, true));
                    } else {
                        mSocket.emit("Chatroom", mymessage);
                        adapter.add(new DataProvider(position, mymessage, DataProvider.TYPE_MESSAGE, false));
                    }
                    adapter.notifyDataSetChanged();
                    scrolltoBottom();
                } else {
                    mSocket.emit("Chatroom", mymessage);
                    new CLDataProvider(mymessage);
                    adapter.add(new DataProvider(position, mymessage, DataProvider.TYPE_MESSAGE, false));
                    adapter.notifyDataSetChanged();
                }


            }
        });
        picbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });



    }

    // Find GCM Token for first time users
    private void getGCMToken(final String senderId) throws Exception {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String token = "";
                try {
                    InstanceID instanceID = InstanceID.getInstance(mContext);
                    token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    if (token != null && !token.isEmpty()) {
                        SharedPreferences appPrefs = mContext.getSharedPreferences("com.example.gcmclient_preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = appPrefs.edit();
                        prefsEditor.putString("token", token);
                        prefsEditor.commit();
                        Log.i(TAG, "GCM Registration Token: " + token);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return token;
            }
        }.execute();
    }

    //  Listens to the other client typing
    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    boolean TypingStatus;
                    try {
                        TypingStatus = data.getBoolean("status");
                    } catch (JSONException e) {
                        return;
                    }
                    if(TypingStatus) {
                        adapter.add(new DataProvider(position, "Typing...", DataProvider.TYPE_ACTION, true));
                        adapter.notifyDataSetChanged();
                    } else removeTyping();
                }

            });
        }
    };


    //  Once an image is saved to a file deal with it here.
    public void onActivityResult(int requestcode, int resultcode, Intent data){
        if(requestcode == REQUEST_CODE && resultcode == RESULT_OK)

        {

            try {
                setPic();
                PictureRequest();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
    //   Get full size Image
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // File image = File.createTempFile(
        File image = new File(storageDir, imageFileName);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i(TAG,"URL FROM CREATE FILE: " + mCurrentPhotoPath);
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.i(TAG, "photofile url" +Uri.fromFile(photoFile));

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
    //  Resize image and compress quality
    private void setPic() throws IOException {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        File file = new File(mCurrentPhotoPath);
        bmOptions.inJustDecodeBounds = true;

        Bitmap original=BitmapFactory.decodeFile(mCurrentPhotoPath);

        file.createNewFile();
        FileOutputStream ostream = new FileOutputStream(file);

        Bitmap newBm = Bitmap.createScaledBitmap(original, 900, 1280, true);
        newBm.compress(Bitmap.CompressFormat.JPEG, 95, ostream);

        ostream.close();
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        adapter.add(new DataProvider(position, newBm, DataProvider.TYPE_IMG, false));
        adapter.notifyDataSetChanged();
    }


    //  Post request to upload image to server
    private void PictureRequest() throws Exception {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    final Webb webb = Webb.create();
                    Log.i(TAG, "PHOTOPATH IN PICTURE REQUEST: " + mCurrentPhotoPath);
                    webb.setBaseUri("https://great-sarodh.c9.io");
                    JSONObject response = webb
                            .post("/upload")
                            .body(new File(mCurrentPhotoPath))
                            .connectTimeout(10 * 1000)
                            .asJsonObject()
                            .getBody();
                    String url = null;
                    Log.i(TAG, "RESULT: " + response);
                    url = response.getString("link");
                    Log.i(TAG, "RESULT url : " + url);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

        }.execute();
    }
    //  Listens to incoming messages
    public Emitter.Listener myMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;

                    try {

                        message = data.getString("message");

                        Log.d("RECEIVED MESSAGE", message);

                        adapter.add(new DataProvider(position, message, DataProvider.TYPE_MESSAGE, true));//.toString();
                        removeTyping();
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    // Listens to incoming image URLs
    public Emitter.Listener onPictureReceived = new Emitter.Listener() {

        @Override

        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    JSONObject data = (JSONObject) args[0];

                    String transfer = null;
                    try {
                        transfer = data.getString("image");

                        Log.i(transfer,"sdf");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Bitmap bMap;
                    //   bMap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);


                    // byte[] decodedString  = Base64.decode(transfer, Base64.DEFAULT);
                    //   Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    //   adapter.add(new DataProvider(position, decodedByte, DataProvider.TYPE_IMG, true));
                    adapter.add(new DataProvider(position, "NO PICTURE YET NERDS", DataProvider.TYPE_MESSAGE, true));
                    adapter.notifyDataSetChanged();

                }
            });
        }
    };
    //  Remove typing log from the chat listview
    private void removeTyping() {
        for(int i = adapter.getCount() -1 ; i >= 0 ; i-- ) {

            DataProvider d = adapter.getItem(i);
            Log.i(TAG, "IN REMOVE TYPING MSG" + d.message);

            if(d.getType() == DataProvider.TYPE_ACTION) {
                Log.i(TAG, "its a typing msg");
                adapter.chat_list.remove(i);
                adapter.notifyDataSetChanged();
            }
        }
    }
    //  When the user stops typing emit to the server
    private Runnable onTypingTimeout =  new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;
            Log.i(TAG, "IN ON TYPING TIMEOUT");
            mTyping = false;
            mSocket.emit("typing", false);
        }
    };

    private void scrolltoBottom(){
        listview.setSelection(adapter.getCount()-1);
    }


    /*@Override
    public void onResume(){
        super.onResume();
        mSocket.on("Chatroom", myMessage);
        mSocket.emit("CreateRoom",roomnum);

    }*/
}
