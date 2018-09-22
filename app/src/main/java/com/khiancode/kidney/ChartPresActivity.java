package com.khiancode.kidney;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.khiancode.kidney.model.BmiModel;
import com.khiancode.kidney.model.PresModel;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class ChartPresActivity extends AppCompatActivity {
    private Realm realm;
    private RealmChangeListener realmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object o) {
            invalidateView();
        }
    };
    RealmResults<PresModel> results;
    String TAG = "ChartPresActivity";
    LineChart mChart;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_bmi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChart = findViewById(R.id.chart);
        new Components().set_Chart3(mChart);

        realm = Realm.getDefaultInstance();
        realm.addChangeListener(realmListener);
        invalidateView();
    }

    private void addEntry2(float over,float lower) {
        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set_1 = data.getDataSetByIndex(0);
            ILineDataSet set_2 = data.getDataSetByIndex(1);

            if (set_1 == null) {
                set_1 = new Components().createSetPres1();
                set_2 = new Components().createSetPres2();
                data.addDataSet(set_1);
                data.addDataSet(set_2);
            }

            data.addEntry(new Entry(set_1.getEntryCount(), over), 0);
            data.addEntry(new Entry(set_2.getEntryCount(), lower), 1);
            data.notifyDataChanged();

            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(50);
            mChart.moveViewToX(data.getEntryCount());

        }
    }

    private void invalidateView() {
//        results = realm.where(BmiModel.class).sort("dt", Sort.ASCENDING).findAll();
        results = realm.where(PresModel.class).sort("dt", Sort.ASCENDING).findAll();

        for (PresModel model:results) {
            Log.d(TAG, "id = " + model.getDt());
//            feedMultiple(model.getValue());
            addEntry2(model.getOver(),model.getLower());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

}
