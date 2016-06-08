package opinyon.com.bruno.opinyon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import opinyon.com.bruno.opinyon.util.EnumOpt;
import opinyon.com.bruno.opinyon.util.VoteModelIMP;

public class Votations extends AppCompatActivity {

    private Button votation;
    private String cpf;
    private String votationOptions;
    private DatabaseReference mDatabase;

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

    @Override
    public void onBackPressed() {
        Intent in = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(in);
        finish();
    }

    private void getVotations(){
        try {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://project-3054629283855362897.firebaseio.com/votations");
            ChildEventListener votationListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    votationOptions = dataSnapshot.getKey().toString();
                    if(votationOptions.equals(EnumOpt.impeachment.getRealName())){
                        votation.setText(EnumOpt.impeachment.getShortname());
                    }else {
                        votation.setText(votationOptions);
                    }
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

        checkVote();
//        Intent i = new Intent(getApplicationContext(), Options.class);
//        i.putExtra("votationOptions", votationOptions);
//        i.putExtra("cpf", cpf);
//        startActivity(i);
    }

    private void checkVote() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("votations").child(votationOptions);

        ValueEventListener votationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VoteModelIMP vm = dataSnapshot.getValue(VoteModelIMP.class);

                if (vm.voters.containsKey(cpf)) {
                    String voto = vm.voters.get(cpf);
                    createDialog(vm, voto, mDatabase);
                }else{
                    Intent i = new Intent(getApplicationContext(), Options.class);
                    i.putExtra("votationOptions", votationOptions);
                    i.putExtra("cpf", cpf);
                    startActivity(i);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(votationListener);
    }

    private void createDialog(final VoteModelIMP vm, final String voto, final DatabaseReference mDatabase){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
// Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Remover voto usando a key(cpf) para saber o voto que ele já havia feito
                if(vm.voters.get(cpf).equals("nao")){
                    vm.nao = vm.nao -1;
                }else if(vm.voters.get(cpf).equals("sim")){
                    vm.sim = vm.sim -1;
                }

                vm.voters.remove(cpf);

                mDatabase.setValue(vm);

                Intent i = new Intent(getApplicationContext(), Options.class);
                i.putExtra("votationOptions", votationOptions);
                i.putExtra("cpf", cpf);
                startActivity(i);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                Toast.makeText(Votations.this, "Voto já computado", Toast.LENGTH_LONG).show();
            }
        });
// Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.setTitle("Você já votou, deseja alterar seu voto?");
        dialog.show();
    }
}
