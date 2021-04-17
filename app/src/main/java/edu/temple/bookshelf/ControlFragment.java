package edu.temple.bookshelf;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import edu.temple.audiobookplayer.AudiobookService;

public class ControlFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SERV = "param1";

    // Button Flags
    boolean pause;
    boolean play;
    boolean stop;


    public ControlFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ControlFragment newInstance() {
        ControlFragment fragment = new ControlFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String headerString = bundle.getString("Header");
            TextView header = getView().findViewById(R.id.header);
            header.setText("Now Playing: " + headerString);

        }
    };

    public void onStop(){
        super.onStop();
        if (receiver != null){
            try {
                getActivity().unregisterReceiver(receiver);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_control, container, false);

        IntentFilter filter = new IntentFilter("PLAYING_AUDIO");
        getActivity().registerReceiver(receiver, filter);




        // Buttons and Components
        TextView header = layout.findViewById(R.id.header);
        Button pauseButton = layout.findViewById(R.id.pause);
        Button playButton = layout.findViewById(R.id.play);
        Button stopButton = layout.findViewById(R.id.stop);
        SeekBar seekBar = layout.findViewById(R.id.seekBar);


        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("pause");
                passData(true, false, false);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("play");
                passData(false, true, false);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("stop");
                passData(false, false, true);
            }
        });

        // Inflate the layout for this fragment
        return layout;
    }


    // Pass data between activity and fragment
    onDataPass pressedButtons;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        pressedButtons = (onDataPass) context;
    }

    public interface onDataPass {
        public void onDataPass(boolean pause, boolean play, boolean stop);
    }

    public void passData(boolean pause, boolean play, boolean stop){
        pressedButtons.onDataPass(pause, play, stop);
    }

}

