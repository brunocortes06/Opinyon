package opinyon.com.bruno.opinyon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.sql.Date;
import java.text.SimpleDateFormat;

import opinyon.com.bruno.opinyon.util.CNP;
import opinyon.com.bruno.opinyon.util.Mask;

public class MainActivity extends AppCompatActivity {

    private EditText cpf;
    private TextWatcher cpfMask;
    private Button enter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, "ca-app-pub-9461541042807906~4104560001");

        cpf = (EditText) findViewById(R.id.editCpf);
        // Armazene seus TextWatcher para posterior uso
        cpfMask = Mask.insert("###.###.###-##", cpf);
        cpf.addTextChangedListener(cpfMask);

        enter = (Button) findViewById(R.id.enter);


    }

    public void enter (View view)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDateandTime = sdf.format(new Date(System.currentTimeMillis()));
        if(CNP.isValidCPF(cpf.getText().toString())){
            try {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://project-3054629283855362897.firebaseio.com");
                //login
                String cpfNoMask = cpf.getText().toString().replaceAll("[^0-9]", "");
                ref.child("users").child(cpfNoMask).child("date").setValue(currentDateandTime);

                Intent i = new Intent(getApplicationContext(), Votations.class);
                i.putExtra("cpf",cpfNoMask);
                startActivity(i);

                //Votations.start(this);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
