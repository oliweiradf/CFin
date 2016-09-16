package br.com.oliweira.cfin;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfiguracoesActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    final private static int DIALOG_LOGIN = 1;
    private AlertDialog alertBackupManual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        //Criando instancia do banco
        db = openOrCreateDatabase("db_cfin", MODE_PRIVATE, null);

        SQLiteCursor csrConfig = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_config;", null);

        Switch swtTipoContaFixa = (Switch) findViewById(R.id.swtTipoContaFixa);
        assert swtTipoContaFixa != null;
        Switch swtSalarioFixo = (Switch) findViewById(R.id.swtSalarioFixo);
        assert swtSalarioFixo != null;
        Switch swtBackupAutomatico = (Switch) findViewById(R.id.swtBackupAutomatico);
        assert swtBackupAutomatico != null;
        final Switch swtCartao = (Switch) findViewById(R.id.swtCartao);
        assert swtCartao != null;

        if(csrConfig.moveToFirst()){

            SQLiteCursor csrContas = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_tipoconta;", null);

            if(csrConfig.getInt(csrConfig.getColumnIndex("tp_contafixa")) == 1){
                swtTipoContaFixa.setChecked(true);
                if(csrContas.getCount() > 0 ){
                    swtTipoContaFixa.setEnabled(false);
                }
            }else{
                swtTipoContaFixa.setChecked(false);
            }

            if(csrConfig.getInt(csrConfig.getColumnIndex("tp_salariofixo")) == 1){
                swtSalarioFixo.setChecked(true);
            }else{
                swtSalarioFixo.setChecked(false);
            }

            if(csrConfig.getInt(csrConfig.getColumnIndex("tp_backupauto")) == 1){
                swtBackupAutomatico.setChecked(true);
            }else{
                swtBackupAutomatico.setChecked(false);
            }

            if(csrConfig.getInt(csrConfig.getColumnIndex("tp_cartao")) == 1){
                swtCartao.setChecked(true);
            }else{
                swtCartao.setChecked(false);
            }

        }

        swtBackupAutomatico.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    SQLiteCursor csrConfig = (SQLiteCursor) db.rawQuery("SELECT dt_backup FROM tba_config;", null);
                    //coloca os valores num container
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date();
                    dateFormat.format(date);

                    ContentValues ctvSalvaConfig = new ContentValues();
                    ctvSalvaConfig.put("dt_backup", dateFormat.format(date));
                    Toast.makeText(getBaseContext(), dateFormat.format(date), Toast.LENGTH_LONG).show();

                    if(csrConfig.getCount() > 0){

                        csrConfig.moveToFirst();

                        if(db.update("tba_config", ctvSalvaConfig, "_id = ?",  new String[]{String.valueOf(csrConfig.getInt(0))}) > 0){
                            Toast.makeText(getBaseContext(), "O Backup será realizado mensalmente a partir da data atual.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Não foi possivel atualizar as configurações!", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        if(db.insert("tba_config","_id",ctvSalvaConfig) > 0){
                            Toast.makeText(getBaseContext(), "O Backup será realizado mensalmente a partir de hoje.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Não foi possivel salvar as configurações!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        swtCartao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SQLiteCursor csrCartao = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_cartao;", null);
                    if(csrCartao.getCount() <= 0){
                        Toast.makeText(getBaseContext(), "Você não tem cartão cadastrado. Clicar em 'Cadastar Cartão' para adicionar um.", Toast.LENGTH_LONG).show();
                        swtCartao.setChecked(false);
                    }
                }
            }
        });

        TextView tvCadastrarContasFixas = (TextView) findViewById(R.id.tvCadastrarContasFixas);
        if(tvCadastrarContasFixas != null){
            tvCadastrarContasFixas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent inttTipoContaFixa = new Intent(getApplicationContext(),TipoContaFixaActivity.class);
                    startActivity(inttTipoContaFixa);
                }
            });
        }

        TextView tvCadastrarSalarioFixo = (TextView) findViewById(R.id.tvCadastrarSalarioFixo);
        if(tvCadastrarSalarioFixo != null){
            tvCadastrarSalarioFixo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DIALOG_LOGIN);
                }
            });
        }

        TextView tvBackupManual = (TextView) findViewById(R.id.tvBackupManual);
        if(tvBackupManual != null){
            tvBackupManual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder adBackupManual=  new AlertDialog.Builder(ConfiguracoesActivity.this);
                    adBackupManual.setMessage("Deseja fazer o backup manual agora?");
                    adBackupManual.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent inttBackupManual = new Intent(getApplicationContext(),BackupManualActivity.class);
                            startActivity(inttBackupManual);
                        }
                    });
                    adBackupManual.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    adBackupManual.show();
                }
            });
        }

        TextView tvCadastrarCartao = (TextView) findViewById(R.id.tvCadastrarCartao);
        if(tvCadastrarCartao != null){
            tvCadastrarCartao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent inttCartao = new Intent(getApplicationContext(),CartaoActivity.class);
                    startActivity(inttCartao);
                }
            });
        }
    }

    public void salvarConfig(View view){

        try{

            int swtIdTipoContaFixa;
            int swtIdSalarioFixo;
            int swtIdBackupAutomatico;
            int swtIdCartao;

            if(((Switch)(findViewById(R.id.swtTipoContaFixa))).isChecked()){
                swtIdTipoContaFixa = 1;
            }else{
                swtIdTipoContaFixa = 0;
            }

            if(((Switch)(findViewById(R.id.swtSalarioFixo))).isChecked()){
                swtIdSalarioFixo = 1;
            }else{
                swtIdSalarioFixo = 0;
            }

            if(((Switch)(findViewById(R.id.swtBackupAutomatico))).isChecked()){
                swtIdBackupAutomatico = 1;
            }else{
                swtIdBackupAutomatico = 0;
            }

            if(((Switch)(findViewById(R.id.swtCartao))).isChecked()){
                swtIdCartao = 1;
            }else{
                swtIdCartao = 0;
            }

            //Criando instancia do banco
            SQLiteDatabase db = openOrCreateDatabase("db_cfin", MODE_PRIVATE, null);

            //coloca os valores num container
            ContentValues ctvSalvaConfig = new ContentValues();
            ctvSalvaConfig.put("tp_contafixa", swtIdTipoContaFixa);
            ctvSalvaConfig.put("tp_salariofixo", swtIdSalarioFixo);
            ctvSalvaConfig.put("tp_backupauto", swtIdBackupAutomatico);
            ctvSalvaConfig.put("tp_cartao", swtIdCartao);

            SQLiteCursor csrConfig = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_config;", null);
            if(csrConfig.getCount() > 0){

                csrConfig.moveToFirst();

                if(db.update("tba_config", ctvSalvaConfig, "_id = ?",  new String[]{String.valueOf(csrConfig.getInt(0))}) > 0){

                    Toast.makeText(getBaseContext(), "Configurações atualizadas com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();

                } else {
                    Toast.makeText(getBaseContext(), "Não foi possivel atualizar as configurações!", Toast.LENGTH_LONG).show();
                }
            }else{
                if(db.insert("tba_config","_id",ctvSalvaConfig) > 0){
                    Toast.makeText(getBaseContext(), "Configurações salvas com sucesso!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getBaseContext(), "Não foi possivel salvar as configurações!", Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e){
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        AlertDialog dialogDetails = null;

        switch (id) {
            case DIALOG_LOGIN:
                LayoutInflater inflater = LayoutInflater.from(this);
                View dialogview = inflater.inflate(R.layout.dialog_salario, null);

                AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
                dialogbuilder.setTitle("Cadastra Salário Fixo");
                dialogbuilder.setView(dialogview);
                dialogDetails = dialogbuilder.create();

                break;
        }

        return dialogDetails;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {

        switch (id) {
            case DIALOG_LOGIN:
                final AlertDialog alertDialog = (AlertDialog) dialog;
                Button btnSalvarSalarioFixo = (Button) alertDialog.findViewById(R.id.btnSalvarSalarioFixo);
                final EditText etSalarioFixo = (EditText) alertDialog.findViewById(R.id.etSalarioFixo);

                btnSalvarSalarioFixo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();


                    }
                });
        }
    }
}
