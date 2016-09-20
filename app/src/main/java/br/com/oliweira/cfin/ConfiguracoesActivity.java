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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ConfiguracoesActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    final private static int DIALOG_LOGIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //Cria instancia do banco
        db = openOrCreateDatabase("db_cfin", MODE_PRIVATE, null);

        //Recupera os dados de Configuração
        final SQLiteCursor csrConfig = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_config;", null);

        //Recupera os objetos do Switchs e afirma que os mesmos não são nulos
        final Switch swtTipoContaFixa = (Switch) findViewById(R.id.swtTipoContaFixa);
        assert swtTipoContaFixa != null;
        final Switch swtSalarioFixo = (Switch) findViewById(R.id.swtSalarioFixo);
        assert swtSalarioFixo != null;
        final Switch swtBackupAutomatico = (Switch) findViewById(R.id.swtBackupAutomatico);
        assert swtBackupAutomatico != null;
        final Switch swtCartao = (Switch) findViewById(R.id.swtCartao);
        assert swtCartao != null;

        //Recupera os objetos dos TextView
        TextView tvCadastrarContasFixas = (TextView) findViewById(R.id.tvCadastrarContasFixas);
        TextView tvCadastrarSalarioFixo = (TextView) findViewById(R.id.tvCadastrarSalarioFixo);
        TextView tvBackupManual = (TextView) findViewById(R.id.tvBackupManual);
        TextView tvCadastrarCartao = (TextView) findViewById(R.id.tvCadastrarCartao);
        final TextView tvValorSalarioFixo = (TextView) findViewById(R.id.tvValorSalarioFixo);
        final TextView tvDataBackupManual = (TextView) findViewById(R.id.tvDataBackupManual);

        //Move o cursor para a primeira linha
        if(csrConfig.moveToFirst()){

            //Recupera os dados do tipo de conta fixa
            SQLiteCursor csrContas = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_tipocontafixa;", null);

            //Preenche os Switchs ao abrir a tela
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

            //Preenche os campos "Salario Fixo" e "Data Backup"
            if(csrConfig.getDouble(csrConfig.getColumnIndex("vl_salariofixo")) != 0){
                double vlSalarioFixoFormat = csrConfig.getDouble(csrConfig.getColumnIndex("vl_salariofixo"));
                tvValorSalarioFixo.setText("("+NumberFormat.getCurrencyInstance().format(vlSalarioFixoFormat)+")");
            }else{
                tvValorSalarioFixo.setText("");
            }

                Toast.makeText(getBaseContext(), "diferente de vazio: "+csrConfig.getString(csrConfig.getColumnIndex("dt_backup")), Toast.LENGTH_SHORT).show();
            if(csrConfig.getString(csrConfig.getColumnIndex("dt_backup")) == null){
                tvDataBackupManual.setText("");
                Toast.makeText(getBaseContext(), "Aplicando vazio para o campo tvDataBackupManual", Toast.LENGTH_SHORT).show();
            }else{
                tvDataBackupManual.setText("("+csrConfig.getString(csrConfig.getColumnIndex("dt_backup"))+")");
            }

        }

        //Se o Switch do Salario fixo for checado/deschecado verifica se tem tipos de conta fixa cadastradas,
        //  se não não deixa checar.
        swtTipoContaFixa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SQLiteCursor csrContas = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_tipocontafixa;", null);
                    if(csrContas.getCount() <= 0){
                        Toast.makeText(getBaseContext(), "Você não tem tipos de conta fixa cadastradas. Clicar em 'Cadastar Contas Fixas' para adicionar uma.", Toast.LENGTH_LONG).show();
                        swtTipoContaFixa.setChecked(false);
                    }
                }
            }
        });

        //Se o Switch do Salario fixo for checado/deschecado verifica se tem salario fixo cadastrado,
        //   se não tiver, não deixa checar e não mostra o valor na tela, se tiver, recupera o salario
        //   formata como moeda BR e mostra na tela.
        swtSalarioFixo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SQLiteCursor csrConfigSalarioFixo = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_config;", null);
                    csrConfigSalarioFixo.moveToFirst();
                    if(csrConfigSalarioFixo.getInt(csrConfigSalarioFixo.getColumnIndex("vl_salariofixo")) == 0){
                        Toast.makeText(getBaseContext(), "Você não tem salário fixo cadastrado. Clicar em 'Cadastar Salario Fixo' para adicionar um.", Toast.LENGTH_LONG).show();
                        swtSalarioFixo.setChecked(false);

                        //preenche o valor do salaraio fixo na tela
                        tvValorSalarioFixo.setText("");

                    }else{

                        SQLiteCursor csrConfigSalario = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_config;", null);
                        csrConfigSalario.moveToFirst();

                        //preenche po valor do salaraio fixo na tela
                        double vlSalarioFixoFormat = csrConfigSalario.getDouble(csrConfigSalario.getColumnIndex("vl_salariofixo"));
                        tvValorSalarioFixo.setText("("+NumberFormat.getCurrencyInstance().format(vlSalarioFixoFormat)+")");


                    }
                }else{

                    //preenche po valor do salaraio fixo na tela
                    tvValorSalarioFixo.setText("");
                }
            }
        });
        
        //Se o Switch do Backup Automatico for checado/deschecado insere a data atual e mostra a data na tela
        //   se não tiver mostra a data vazia na tela.
        swtBackupAutomatico.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    SQLiteCursor csrConfigBackup = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_config;", null);
                    
                    //coloca os valores num container
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date();
                    dateFormat.format(date);

                    ContentValues ctvSalvaConfig = new ContentValues();
                    ctvSalvaConfig.put("dt_backup", dateFormat.format(date));

                    if(csrConfigBackup.getCount() > 0){

                        csrConfigBackup.moveToFirst();

                        if(db.update("tba_config", ctvSalvaConfig, "_id = ?",  new String[]{String.valueOf(csrConfig.getInt(0))}) > 0){
                            Toast.makeText(getBaseContext(), "O Backup será realizado mensalmente a partir da data atual("+dateFormat.format(date)+").", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(getBaseContext(), "Não foi possivel atualizar as configurações!", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        if(db.insert("tba_config","_id",ctvSalvaConfig) > 0){
                            Toast.makeText(getBaseContext(), "O Backup será realizado mensalmente a partir da data atual("+dateFormat.format(date)+").", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Não foi possivel salvar as configurações!", Toast.LENGTH_LONG).show();
                        }
                    }
                    Toast.makeText(ConfiguracoesActivity.this, "entrei pra preencher a data", Toast.LENGTH_SHORT).show();
                    //preenche a data na tela
                    tvDataBackupManual.setText("("+dateFormat.format(date)+")");

                }else{

                    //preenche po valor do salaraio fixo na tela
                    tvDataBackupManual.setText("");
                }
            }
        });

        //Se o Switch do Cartão for checado/deschecado verifica se tem cartão cadastrado, se não
        //  tiver não deixa checar.
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

        //Abre a TipoContaFixaActivity para cadastrar manipular os tipos de contas fixas
        if(tvCadastrarContasFixas != null){
            tvCadastrarContasFixas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent inttTipoContaFixa = new Intent(getApplicationContext(),TipoContaFixaActivity.class);
                    startActivity(inttTipoContaFixa);
                }
            });
        }

        //Abre um Dialog para cadastrar um salário fixo
        if(tvCadastrarSalarioFixo != null){
            tvCadastrarSalarioFixo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DIALOG_LOGIN);
                }
            });
        }

        //Abre a BackupActivity para fazer o backup manualmente
        if(tvBackupManual != null){
            tvBackupManual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder adBackupManual=  new AlertDialog.Builder(ConfiguracoesActivity.this);
                    adBackupManual.setMessage("Deseja fazer o backup manual agora?");
                    adBackupManual.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent inttBackupManual = new Intent(getApplicationContext(),BackupActivity.class);
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

        //Abre a CartaoActivity para cadastrar novos cartões
        if(tvCadastrarCartao != null){
            tvCadastrarCartao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent inttCartao = new Intent(getApplicationContext(),CartaoActivity.class);
                    startActivity(inttCartao);
                }
            });
        }

        //Exclui o valor do salário fixo
        if(tvValorSalarioFixo.getText() != ""){
            tvValorSalarioFixo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertExcluirSalario=  new AlertDialog.Builder(ConfiguracoesActivity.this);
                    alertExcluirSalario.setMessage("Deseja excluir o salário fixo cadastrado?");
                    alertExcluirSalario.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SQLiteCursor csrConfigExcluiSalario = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_config;", null);
                            csrConfigExcluiSalario.moveToFirst();
                            ContentValues ctvExcluirSalarioFixo = new ContentValues();
                            ctvExcluirSalarioFixo.put("vl_salariofixo", 0);
                            ctvExcluirSalarioFixo.put("tp_salariofixo", 0);

                            if(db.update("tba_config", ctvExcluirSalarioFixo, "_id = ?",  new String[]{String.valueOf(csrConfigExcluiSalario.getInt(0))}) > 0){
                                Toast.makeText(getBaseContext(), "Salário Fixo excluído com sucesso!", Toast.LENGTH_SHORT).show();
                                swtSalarioFixo.setChecked(false);
                                tvValorSalarioFixo.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                    alertExcluirSalario.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertExcluirSalario.show();
                }
            });
        }

        //Exclui a data do backup automático
        if(tvDataBackupManual.getText() != ""){
            tvDataBackupManual.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertExcluirDataBackup = new AlertDialog.Builder(ConfiguracoesActivity.this);
                    alertExcluirDataBackup.setMessage("Deseja excluir a data do backup cadastrada?");
                    alertExcluirDataBackup.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            SQLiteCursor csrConfigExcluiData = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_config;", null);
                            csrConfigExcluiData.moveToFirst();
                            ContentValues ctvExcluirDataBackup = new ContentValues();
                            ctvExcluirDataBackup.put("dt_backup", "");
                            ctvExcluirDataBackup.put("tp_backupauto", 0);

                            if(db.update("tba_config", ctvExcluirDataBackup, "_id = ?",  new String[]{String.valueOf(csrConfigExcluiData.getInt(0))}) > 0){
                                Toast.makeText(getBaseContext(), "Data do Backup excluída com sucesso!", Toast.LENGTH_SHORT).show();
                                swtBackupAutomatico.setChecked(false);
                                tvDataBackupManual.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                    alertExcluirDataBackup.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertExcluirDataBackup.show();
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

                btnSalvarSalarioFixo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        try{

                            final EditText etSalarioFixo = (EditText) alertDialog.findViewById(R.id.etSalarioFixo);
                            double vlSalarioFixo = Double.parseDouble(etSalarioFixo.getText().toString());

                            ContentValues ctvSalarioFixo = new ContentValues();
                            ctvSalarioFixo.put("vl_salariofixo",vlSalarioFixo);

                            //Criando instancia do banco
                            db = openOrCreateDatabase("db_cfin", MODE_PRIVATE, null);
                            SQLiteCursor csrConfig = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_config;", null);
                            csrConfig.moveToFirst();
                            if(db.update("tba_config",ctvSalarioFixo,"_id = ?", new String[]{String.valueOf(csrConfig.getInt(0))}) > 0){
                                Toast.makeText(getBaseContext(), "Salário salvo com sucesso!", Toast.LENGTH_SHORT).show();

                                //Salva 1 para tpSalarioFixo checando o Switch swtSalarioFixo
                                ContentValues ctvSwtSalarioFixo = new ContentValues();
                                ctvSwtSalarioFixo.put("tp_salariofixo",1);
                                db.update("tba_config",ctvSwtSalarioFixo,"_id = ?", new String[]{String.valueOf(csrConfig.getInt(0))});

                                Switch swtSalarioFixo = (Switch) findViewById(R.id.swtSalarioFixo);
                                assert swtSalarioFixo != null;
                                swtSalarioFixo.setChecked(true);

                                SQLiteCursor csrConfigSalario = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_config;", null);
                                csrConfigSalario.moveToFirst();
                                //preenche po valor do salaraio fixo na tela
                                TextView tvValorSalarioFixo = (TextView) findViewById(R.id.tvValorSalarioFixo);
                                double vlSalarioFixoFormat = csrConfigSalario.getDouble(csrConfigSalario.getColumnIndex("vl_salariofixo"));
                                tvValorSalarioFixo.setText("("+NumberFormat.getCurrencyInstance().format(vlSalarioFixoFormat)+")");

                            }
                        }catch (Exception e){
                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        alertDialog.dismiss();
                    }
                });
        }
    }
}
