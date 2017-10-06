package com.bilal.mqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnForward, btnBackward, btnLeft, btnRight, btnStop;


    String serverUri = "tcp://192.168.0.102:1883";
    String clientId = "testAndroidApp";
    String topicToPublish = "smartCar/move";

    private MQTTHistoryAdapter mMqttHistoryAdapter;
    private MqttAndroidClient mqttAndroidClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitUI();
        connectToMQTTBroker();
    }

    private void InitUI() {

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Smart Car (MQTT)");

        btnForward = (Button) findViewById(R.id.btnForward);
        btnForward.setOnClickListener(this);

        btnBackward = (Button) findViewById(R.id.btnBackward);
        btnBackward.setOnClickListener(this);

        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnLeft.setOnClickListener(this);

        btnRight = (Button) findViewById(R.id.btnRight);
        btnRight.setOnClickListener(this);

        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewHistoryLogs);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mMqttHistoryAdapter = new MQTTHistoryAdapter(new ArrayList<String>());
        mRecyclerView.setAdapter(mMqttHistoryAdapter);

    }

    void connectToMQTTBroker() {

        clientId = clientId + System.currentTimeMillis();

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    addToHistory("Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                } else {
                    addToHistory("Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                addToHistory("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                addToHistory("Incoming message: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);


        try {
            //addToHistory("Connecting to " + serverUri);
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    addToHistory("Failed to connect to: " + serverUri + "");
                }
            });

        } catch (MqttException ex) {
            ex.printStackTrace();
        }

    }

    public void publishMessage(int move) {

        try {

            MqttMessage message = new MqttMessage();
            message.setPayload(String.valueOf(move).getBytes());
            mqttAndroidClient.publish(topicToPublish, message);

            addToHistory("Message Published");

            if (!mqttAndroidClient.isConnected()) {
                addToHistory("messages in buffer");
            }

        } catch (MqttException e) {
            addToHistory("Error Publishing");
            System.err.println("Error Publishing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addToHistory(String mainText) {
        mMqttHistoryAdapter.add(mainText);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnForward:
                publishMessage(AppConstants.MOVE_FORWARD);
                break;
            case R.id.btnBackward:
                publishMessage(AppConstants.MOVE_BACKWARD);
                break;
            case R.id.btnLeft:
                publishMessage(AppConstants.MOVE_LEFT);
                break;
            case R.id.btnRight:
                publishMessage(AppConstants.MOVE_RIGHT);
                break;
            case R.id.btnStop:
                publishMessage(AppConstants.MOVE_STOP);
                break;
            default:
                break;
        }
    }
}
