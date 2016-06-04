package opinyon.com.bruno.opinyon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Options extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String votationOptions;
    private RadioButton vtOpt1;
    private RadioButton vtOpt2;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            votationOptions = extras.getString("votationOptions");
            System.out.println(votationOptions);
        }

        vtOpt1 = (RadioButton) findViewById(R.id.rd1);
        vtOpt2 = (RadioButton) findViewById(R.id.rd2);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("votations").child(votationOptions);
        getOptions();
    }

    private void getOptions() {
        ChildEventListener votationListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(count == 0)
                    vtOpt1.setText(dataSnapshot.getKey());

                if(count == 1)
                    vtOpt2.setText(dataSnapshot.getKey());

                count = count +1;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addChildEventListener(votationListener);
    }
}
