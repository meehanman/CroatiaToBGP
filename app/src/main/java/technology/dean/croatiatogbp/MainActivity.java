package technology.dean.croatiatogbp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = preferences.edit();

        final EditText editText = (EditText) findViewById(R.id.editText);
        final TextView textView = (TextView) findViewById(R.id.textView);
        final TextView prefView = (TextView) findViewById(R.id.prefView);
        //editor.putString("History","").commit();
        prefView.setText(preferences.getString("History",""));
        textView.setText(preferences.getString("Value",""));
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);

        //Focus and Keyboard
        if(editText.requestFocus()){
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        if (editText != null) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (textView != null) {
                        String text = editText.getText().toString();
                        //Convert
                        textView.setText(convert(text));
                        editor.putString("Value",textView.getText().toString()).commit();
                    }
                }
            });

            editText.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    editText.setText("");
                }
            });
        }

        if (textView != null) {
            textView.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {
                    //Save String to Editor
                    final TextView textView = (TextView) findViewById(R.id.textView);

                    //Check if last saved string is the same
                    String lastSaved = preferences.getString("History","").split("\n")[0];

                    if(lastSaved.contentEquals(textView.getText().toString())){
                        Log.v("Dean","Same as Previous");
                        return false;
                    }

                    //If set to default
                    if(textView.getText().toString().contentEquals("")||textView.getText().toString().contentEquals("£0.00")){
                        Log.v("Dean","Blank");
                        return false;
                    }

                    //The Actual Save
                    editor.putString("History",textView.getText().toString()+"\n"+preferences.getString("History",""));

                    //Commit
                    if(editor.commit()){
                        //Load Results
                        final TextView prefView = (TextView) findViewById(R.id.prefView);
                        prefView.setText(preferences.getString("History",""));
                        Toast.makeText(getApplicationContext(), "Saved "+textView.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });
        }

        prefView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                prefView.setText("");
                editor.putString("History","").commit();
                Toast.makeText(getApplicationContext(), "History Removed", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public String convert(String s){
        //Turn String to Double
        Double money;
        try{
            money = Double.parseDouble(s);
        }catch(NumberFormatException e){
            return "";
        }
        //Do Calculation
        Double calc = money / 8.6703;
        //Make it look nice
        DecimalFormat df = new DecimalFormat("#.00");
        //Return String
        return String.valueOf(s+" HRK = "+String.format("£%.2f", calc));
    }


}
