package br.com.oliweira.cfin;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class TipoContaFixaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipo_conta_fixa);

        SQLiteDatabase db = openOrCreateDatabase("db_cfin",MODE_PRIVATE,null);
        SQLiteCursor csrTipoContaFixa = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_tipocontafixa",null);

        ListView lvTipoContaFixa = (ListView) findViewById(R.id.lvTipoContaFixa);

        String[] from = {"_id","no_tipocontafixa","tp_operador"};
        int[] to = {R.id.tvIdTipoContaFixaLv, R.id.tvTipoContaFixaLv, R.id.tvTipoOperadorLv};
        SimpleCursorAdapter adptTipoContaFixa = new SimpleCursorAdapter(
                getBaseContext(),R.layout.model_lv_tipo_conta_fixa,csrTipoContaFixa,from, to, 0);

        lvTipoContaFixa.setAdapter(adptTipoContaFixa);
    }
}
