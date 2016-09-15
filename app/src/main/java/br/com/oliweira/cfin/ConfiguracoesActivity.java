package br.com.oliweira.cfin;

import android.content.ContentValues;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class ConfiguracoesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        //Criando instancia do banco
        SQLiteDatabase db = openOrCreateDatabase("db_cfin", MODE_PRIVATE, null);

        SQLiteCursor csrContas = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_config;", null);

        Switch swtTipoContaFixa = (Switch) findViewById(R.id.swtTipoContaFixa);
        Switch swtSalarioFixo = (Switch) findViewById(R.id.swtSalarioFixo);
        Switch swtBackupAutomatico = (Switch) findViewById(R.id.swtBackupAutomatico);
        Switch swtCartao = (Switch) findViewById(R.id.swtCartao);

        if(csrContas.moveToFirst()){
            if(csrContas.getInt(csrContas.getColumnIndex("tp_contafixa")) == 2){
                swtTipoContaFixa.setChecked(true);
            }else{
                swtTipoContaFixa.setChecked(false);
            }

            if(csrContas.getInt(csrContas.getColumnIndex("tp_salariofixo")) == 2){
                swtSalarioFixo.setChecked(true);
            }else{
                swtSalarioFixo.setChecked(false);
            }

            if(csrContas.getInt(csrContas.getColumnIndex("tp_backupauto")) == 2){
                swtBackupAutomatico.setChecked(true);
            }else{
                swtBackupAutomatico.setChecked(false);
            }

            if(csrContas.getInt(csrContas.getColumnIndex("tp_cartao")) == 2){
                swtCartao.setChecked(true);
            }else{
                swtCartao.setChecked(false);
            }
        }


    }
}
