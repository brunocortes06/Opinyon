package opinyon.com.bruno.opinyon;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Votations extends AppCompatActivity {

    private Button votation;
    private String cpf;
    private String votationOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voatations);

        votation = (Button) findViewById(R.id.votationBut);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cpf = extras.getString("cpf");
        }

        getVotations();
    }

    private void getVotations(){
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://project-3054629283855362897.firebaseio.com/votations");
            ChildEventListener votationListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    votationOptions = dataSnapshot.getKey().toString();
                    votation.setText(votationOptions);
                    votation.setEnabled(true);
                    votation.setVisibility(View.VISIBLE);
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
                    // Getting Post failed, log a message
                    Log.w("loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };
            ref.addChildEventListener(votationListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void vote (View view){
        Intent i = new Intent(getApplicationContext(), Options.class);
        i.putExtra("votationOptions",votationOptions);
        i.putExtra("cpf",cpf);
        startActivity(i);
    }
}
