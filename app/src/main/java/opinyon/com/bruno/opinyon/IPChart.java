package opinyon.com.bruno.opinyon;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import opinyon.com.bruno.opinyon.util.VoteModelIMP;

public class IPChart extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String votationOptions;
    private String cpf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipchart);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            votationOptions = extras.getString("votationOptions");
            cpf = extras.getString("cpf");
        }
        getData();
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(getApplicationContext(),Votations.class);
        in.putExtra("cpf", cpf);
        startActivity(in);
        finish();
    }

    private void getData(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("votations").child(votationOptions);

        ValueEventListener votationListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                VoteModelIMP vm = dataSnapshot.getValue(VoteModelIMP.class);

                float nao = vm.nao;
                float sim = vm.sim;
                float total = nao+sim;
                nao = (nao/total)*100;
                sim = (sim/total)*100;
                ArrayList<BarEntry> entries = new ArrayList<>();
                entries.add(new BarEntry(nao, 0));
                entries.add(new BarEntry(sim, 1));

                BarDataSet dataset = new BarDataSet(entries, "");
                dataset.setColors(ColorTemplate.JOYFUL_COLORS);

                ArrayList<String> labels = new ArrayList<String>();
                labels.add("Contra");
                labels.add("A favor");

                BarChart chart = new BarChart(getApplicationContext());
                chart.animateXY(2000, 2000);
                chart.setYRange(0,100,true);
                setContentView(chart);

                BarData data = new BarData(labels, dataset);
                chart.setData(data);

                chart.setDescription("% de votos");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(votationListener);
    }
}
