package br.com.oliweira.cfin;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class CompraCartaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compra_cartao);

        //estancia db_cfin
        SQLiteDatabase db = openOrCreateDatabase("db_cfin", MODE_PRIVATE, null);

        //consulta tba_cartao
        SQLiteCursor csrCartao = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_cartao;", null);

        Spinner spnCartao = (Spinner) findViewById(R.id.spnCartao);

        String[] from = {"_id","no_cartao"};
        int[] to = {R.id.tvIdCartaoMdl,R.id.tvNomeCartaoMdl};

        SimpleCursorAdapter adptCartao = new SimpleCursorAdapter(getBaseContext(),R.layout.model_spn_cartao,csrCartao,from,to,0);

        spnCartao.setAdapter(adptCartao);


    }
}
