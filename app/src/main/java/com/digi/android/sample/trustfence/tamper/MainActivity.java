package com.digi.android.sample.trustfence.tamper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.digi.android.trustfence.ITamperEventListener;
import com.digi.android.trustfence.TamperInterface;
import com.digi.android.trustfence.TrustfenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements ITamperEventListener {

    // Constants.
    private final static String TAG = "TrustfenceTamperSample";

    // Variables.
    private TextView textInterfaceStatus;
    private TextView textEventStatus;
    private TextView textListenerStatus;

    private Button buttonRefresh;
    private Button buttonRegisterListener;
    private Button buttonUnregisterListener;
    private Button buttonACKEvent;
    private Button buttonClearEvent;

    private Spinner interfaceSelector;

    private TrustfenceManager trustfenceManager;

    private TamperInterface tamperInterface;

    private boolean listenerRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the Trustfence manager.
        trustfenceManager = new TrustfenceManager(this);

        // Initialize the UI components.
        initializeUI();
    }

    /**
     * Instantiates and initializes all the UI components.
     */
    private void initializeUI() {
        this.interfaceSelector = findViewById(R.id.interface_selector);
        this.textInterfaceStatus = findViewById(R.id.text_interface_status);
        this.textEventStatus = findViewById(R.id.text_event_status);
        this.textListenerStatus = findViewById(R.id.text_listener_status);
        this.buttonRefresh = findViewById(R.id.button_refresh);
        this.buttonRegisterListener = findViewById(R.id.button_register);
        this.buttonUnregisterListener = findViewById(R.id.button_unregister);
        this.buttonACKEvent = findViewById(R.id.button_ack);
        this.buttonClearEvent = findViewById(R.id.button_clear);

        resetUI();

        interfaceSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                handleInterfaceSelected((int)interfaceSelector.getSelectedItem());
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                tamperInterface = null;
                listenerRegistered = false;
                resetUI();
            }
        });

        // Fill the tamper interfaces list.
        ArrayList<Integer> interfaces = new ArrayList<>();
        int numInterfaces = trustfenceManager.getNumTamperInterfaces();
        for (int i = 0; i < numInterfaces; i++) {
            interfaces.add(i);
        }
        interfaceSelector.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                interfaces.toArray(new Integer[0])));
        if (!interfaces.isEmpty() && interfaceSelector.getItemAtPosition(0) != null) {
            interfaceSelector.setSelection(0);
        }
    }

    /**
     * Handles what happens when the "Register Listener" button is clicked.
     */
    public void onRegisterListenerButtonClick(View view) {
        if (listenerRegistered)
            return;

        try {
            tamperInterface.registerListener(this);
            listenerRegistered = true;
            refreshListenerStatus();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Error registering tamper event listener: " + e.getMessage(), e);
            Toast.makeText(this, "Error registering tamper event listener: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handles what happens when the "Unregister Listener" button is clicked.
     */
    public void onUnregisterListenerButtonClick(View view) {
        if (!listenerRegistered)
            return;

        try {
            tamperInterface.removeListener(this);
            listenerRegistered = false;
            refreshListenerStatus();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Error unregistering tamper event listener: " + e.getMessage(), e);
            Toast.makeText(this, "Error unregistering tamper event listener: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handles what happens when the "Refresh" button is clicked.
     */
    public void onRefreshButtonClick(View view) {
        refreshEventInformation();
    }

    /**
     * Handles what happens when the "ACK Event" button is clicked.
     */
    public void onACKEventClick(View view) {
        try {
            tamperInterface.ackEvent();
            // Tamper sysfs is not very quick refreshing values, give it some time.
            refreshEventInformationDelayed(200);
        } catch (IOException e) {
            Log.e(TAG, "Error acknowledging tamper event: " + e.getMessage(), e);
            Toast.makeText(this, "Error acknowledging tamper event: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handles what happens when the "Clear Event" button is clicked.
     */
    public void onClearEventButtonClick(View view) {
        try {
            tamperInterface.clearEvent();
            // Tamper sysfs is not very quick refreshing values, give it some time.
            refreshEventInformationDelayed(200);
        } catch (IOException e) {
            Log.e(TAG, "Error clearing tamper event: " + e.getMessage(), e);
            Toast.makeText(this, "Error clearing tamper event: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Resets all UI components status.
     */
    private void resetUI() {
        buttonRefresh.setEnabled(false);
        buttonRegisterListener.setEnabled(false);
        buttonUnregisterListener.setEnabled(false);
        buttonACKEvent.setEnabled(false);
        buttonClearEvent.setEnabled(false);
        textListenerStatus.setText("");
        textInterfaceStatus.setText("");
        textEventStatus.setText("");
    }

    /**
     * Handles what happens when a new Tamper interface index is selected.
     *
     * @param interfaceIndex The new tamper interface index.
     */
    private void handleInterfaceSelected(int interfaceIndex) {
        Log.i(TAG, "New Tamper interface selected: " + interfaceIndex);
        // Unregister listener.
        if (listenerRegistered)
            onUnregisterListenerButtonClick(null);
        // Instantiate and refresh the new interface.
        try {
            tamperInterface = trustfenceManager.getTamperInterface(interfaceIndex);
            refreshInterfaceInformation();
            refreshListenerStatus();
            buttonRefresh.setEnabled(true);
        } catch (IOException e) {
            Log.e(TAG, "Error retrieving tamper interface: " + e.getMessage(), e);
            Toast.makeText(this, "Error retrieving tamper interface: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Refresh the selected tamper interface information.
     */
    private void refreshInterfaceInformation() {
        textInterfaceStatus.setText(tamperInterface.isActive() ? R.string.active: R.string.inactive);
    }

    /**
     * Refresh the event information with delay.
     *
     * @param delay The delay in milliseconds before refreshing the information.
     */
    private void refreshEventInformationDelayed(long delay) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::refreshEventInformation, delay);
    }

    /**
     * Refresh the event information.
     */
    private void refreshEventInformation() {
        try {
            if (tamperInterface.isEventTriggered()) {
                textEventStatus.setText(R.string.event_triggered);
                buttonACKEvent.setEnabled(true);
                buttonClearEvent.setEnabled(true);
            } else if (tamperInterface.isEventAck()) {
                textEventStatus.setText(R.string.event_acknowledged);
                buttonACKEvent.setEnabled(false);
                buttonClearEvent.setEnabled(true);
            } else {
                if (listenerRegistered) {
                    textEventStatus.setText(R.string.event_waiting);
                } else {
                    textEventStatus.setText(R.string.event_none);
                }
                buttonACKEvent.setEnabled(false);
                buttonClearEvent.setEnabled(false);
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "Error refreshing event information: " + e.getMessage(), e);
            Toast.makeText(this, "Error refreshing event information: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Refresh the listener status.
     */
    private void refreshListenerStatus() {
        textListenerStatus.setText(listenerRegistered ? getString(R.string.registered): getString(R.string.not_registered));
        buttonRegisterListener.setEnabled(!listenerRegistered);
        buttonUnregisterListener.setEnabled(listenerRegistered);
        refreshEventInformation();
    }

    @Override
    public void eventTriggered(int i) {
        refreshEventInformation();
    }
}
