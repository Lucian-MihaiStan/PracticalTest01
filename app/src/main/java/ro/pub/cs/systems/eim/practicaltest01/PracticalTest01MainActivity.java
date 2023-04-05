package ro.pub.cs.systems.eim.practicaltest01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PracticalTest01MainActivity extends AppCompatActivity {

    Button add;
    Button compute;
    EditText nextTerm;
    EditText allTerms;
    private int lastSum;
    private String lastAllTerms;

    private PracticalBroadcastReceiver broadcastReceiver = new PracticalBroadcastReceiver();
    private IntentFilter intentFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test01_main);

        add = findViewById(R.id.add);
        compute = findViewById(R.id.compute);
        nextTerm = findViewById(R.id.nextTerm);
        allTerms = findViewById(R.id.allTerms);

        add.setOnClickListener(new ButtonClickListener());
        compute.setOnClickListener(new ButtonClickListener());
        intentFilter.addAction(Constants.BROADCAST_ACTION);
    }

    class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.add:
                    if (nextTerm.getText().toString().equals("")) {
                        return;
                    }

                    String nextTermString = nextTerm.getText().toString();
                    if (allTerms.getText().toString().equals("")) {
                        allTerms.setText(nextTermString);
                    } else {
                        allTerms.setText(allTerms.getText().toString() + "+" + nextTermString);
                    }
                    break;
                case R.id.compute:
                    if (allTerms.getText().toString().equals("")) {
                        return;
                    }

                    if (allTerms.getText().toString().equals(lastAllTerms)) {
                        Toast.makeText(getApplicationContext(), String.valueOf(lastSum), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), PracticalTest01SecondaryActivity.class);
                    intent.putExtra("allTerms", allTerms.getText().toString());
                    startActivityForResult(intent, Constants.REQUEST_CODE);
                    break;
            }
        }
    }

    class PracticalBroadcastReceiver extends android.content.BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, intent.getStringExtra(Constants.BROADCAST_RECEIVER_EXTRA), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    lastSum = data.getExtras().getInt(Constants.SUM);
                    if (lastSum > 10) {
                        Intent intent = new Intent(getApplicationContext(), PracticalTest01Service.class);
                        getApplicationContext().startService(intent);
                    }
                    lastAllTerms = allTerms.getText().toString();
                    Toast.makeText(this, String.valueOf(lastSum), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (allTerms.getText().toString().equals(lastAllTerms))
            outState.putInt("lastsum", lastSum);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("lastsum")) {
            allTerms.setText(String.valueOf(savedInstanceState.getInt("lastsum")));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().stopService(new Intent(getApplicationContext(), PracticalTest01Service.class));
    }
}