package br.com.oliweira.cfin;

import android.content.ContentValues;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CartaoActivity extends AppCompatActivity {

    private List<String> lstTipoBandeiras = new ArrayList<String>();
    private List<String> lstTipoOperador = new ArrayList<String>();
    private Spinner spnTipoBandeira;
    private Spinner spnTipoOperador;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartao);

        spnTipoBandeira = (Spinner) findViewById(R.id.spnTipoBandeira);

        lstTipoBandeiras.add("Visa");
        lstTipoBandeiras.add("Master");
        lstTipoBandeiras.add("Elo");

        ArrayAdapter<String> aaTipoBandeira = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, lstTipoBandeiras);
        ArrayAdapter<String> spnaaTipoBandeira = aaTipoBandeira;
        spnTipoBandeira.setAdapter(spnaaTipoBandeira);

        spnTipoOperador = (Spinner) findViewById(R.id.spnTipoOperador);

        lstTipoOperador.add("Débito");
        lstTipoOperador.add("Crédito");
        lstTipoOperador.add("Debito/Crédito");

        ArrayAdapter<String> aaTipoOperador = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, lstTipoOperador);
        final ArrayAdapter<String> spnaaTipoOperador = aaTipoOperador;
        spnTipoOperador.setAdapter(spnaaTipoOperador);

        Button btnSalvarCartao = (Button) findViewById(R.id.btnSalvarCartao);
        btnSalvarCartao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db = openOrCreateDatabase("db_cfin", MODE_PRIVATE, null);

                //Recupera os valores
                EditText etNomeCartao = (EditText)findViewById(R.id.etNomeCartao);
                spnTipoBandeira = (Spinner) findViewById(R.id.spnTipoBandeira);
                int idSpnTipoBndeira = (int) spnTipoBandeira.getSelectedItemId();
                spnTipoOperador = (Spinner) findViewById(R.id.spnTipoOperador);
                int idSpnTipoOperador = (int) spnTipoOperador.getSelectedItemId();

                ContentValues ctvCartao = new ContentValues();
                ctvCartao.put("no_cartao", etNomeCartao.getText().toString());
                ctvCartao.put("tp_bandeira", idSpnTipoBndeira);
                ctvCartao.put("tp_operador", idSpnTipoOperador);

                try {
                    if(db.insert("tba_cartao","_id",ctvCartao) > 0){
                        Toast.makeText(getBaseContext(), "Cartão Salvo com sucesso!", Toast.LENGTH_SHORT).show();

                        ContentValues ctvCartaoSwitch = new ContentValues();
                        ctvCartaoSwitch.put("tp_cartao", 1);

                        //Marca o campo "Utilizar Cartão"
                        SQLiteCursor csrConfig = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_config;", null);
                        csrConfig.moveToFirst();
                        db.update("tba_config",ctvCartaoSwitch,"_id = ?", new String[]{String.valueOf(csrConfig.getInt(0))});

                        finish();
                    }else{
                        Toast.makeText(getBaseContext(), "Não foi possivel salvr o cartão!", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
