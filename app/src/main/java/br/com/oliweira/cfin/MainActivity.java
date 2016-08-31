package br.com.oliweira.cfin;

import android.content.ContentValues;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Criando instancia do banco
        SQLiteDatabase db = openOrCreateDatabase("db_cfin", MODE_PRIVATE, null);


        //***Criação das tabelas auxiliares***

        //tba_contas
        StringBuilder sqlContas = new StringBuilder();
        sqlContas.append("CREATE TABLE IF NOT EXISTS tba_contas(");
        sqlContas.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlContas.append("no_conta VARCHAR(20), ");
        sqlContas.append("vl_bruto NUMERIC(10,2), ");
        sqlContas.append("tp_operador VARCHAR(1));");
        db.execSQL(sqlContas.toString());

        //tba_cartao
        StringBuilder sqlCartao = new StringBuilder();
        sqlCartao.append("CREATE TABLE IF NOT EXISTS tba_cartao(");
        sqlCartao.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlCartao.append("no_cartao VARCHAR(20), ");
        sqlCartao.append("tp_bandeira VARCHAR(1), ");
        sqlCartao.append("tp_operador VARCHAR(1));");
        db.execSQL(sqlCartao.toString());

        //tba_pessoa
        StringBuilder sqlPessoa = new StringBuilder();
        sqlPessoa.append("CREATE TABLE IF NOT EXISTS tba_pessoa(");
        sqlPessoa.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlPessoa.append("no_pessoa VARCHAR(20), ");
        sqlPessoa.append("nu_cpf VARCHAR(15), ");
        sqlPessoa.append("nu_agencia VARCHAR(10), ");
        sqlPessoa.append("nu_conta VARCHAR(10), ");
        sqlPessoa.append("tp_conta VARCHAR(1), ");
        sqlPessoa.append("vl_saldo NUMERIC(10,2)); ");
        db.execSQL(sqlPessoa.toString());

        //***Criação das tabelas***

        //tb_contasmensais
        StringBuilder sqlContaMensal = new StringBuilder();
        sqlContaMensal.append("CREATE TABLE IF NOT EXISTS tb_contasmensais(");
        sqlContaMensal.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlContaMensal.append("id_conta INTERGER(10), ");
        sqlContaMensal.append("vl_conta NUMERIC(10,2), ");
        sqlContaMensal.append("dt_conta DATE, ");
        sqlContaMensal.append("dt_vencimento DATE, ");
        sqlContaMensal.append("id_pessoa INTERGER); ");
        db.execSQL(sqlContaMensal.toString());

        //tb_contascartao
        StringBuilder sqlContasCartao = new StringBuilder();
        sqlContasCartao.append("CREATE TABLE IF NOT EXISTS tb_contascartao(");
        sqlContasCartao.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlContasCartao.append("no_compra VARCHAR(30), ");
        sqlContasCartao.append("vl_compra NUMERIC(10,2), ");
        sqlContasCartao.append("nu_qtdparcelas NUMERIC(2), ");
        sqlContasCartao.append("id_pessoa INTEGER); ");
        db.execSQL(sqlContasCartao.toString());

        //tb_contascartaomensal
        StringBuilder sqlContasCartaoMensal = new StringBuilder();
        sqlContasCartaoMensal.append("CREATE TABLE IF NOT EXISTS tb_contascartaomensal(");
        sqlContasCartaoMensal.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlContasCartaoMensal.append("id_contacartao INTEGER, ");
        sqlContasCartaoMensal.append("nu_parcela NUMERIC(2)); ");
        db.execSQL(sqlContasCartaoMensal.toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void contasPadroes(){

        //Criando instancia do banco
        SQLiteDatabase db = openOrCreateDatabase("db_cfin", MODE_PRIVATE, null);

        //contas padrões
        SQLiteCursor csrContas = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_contas;", null);
        if (csrContas.getCount() <= 0) {

            String[] no_conta = {"Celular","Empréstimo a pagar","Empréstimo a receber","Investimento","Poupanca","Plano de saúde","Prestação Carro","Salario"};
            String[] vl_bruto = {"0,00","0,00","0,00","0,00","0,00","0,00","0,00","0,00"};
            String[] tp_operador = {"D","D","C","C","C","D","D","C"};

            for (int i = 0; i < no_conta.length; i++) {
                ContentValues ctvContas = new ContentValues();
                ctvContas.put("no_contaprovisoria", no_conta[i]);
                ctvContas.put("vl_bruto", vl_bruto[i]);
                ctvContas.put("tp_operador", tp_operador[i]);
                db.insert("tba_contasprovisorias", "_id", ctvContas);
            }
        }

        Toast.makeText(getBaseContext(), "Contas Padrões adicionadas com sucesso!", Toast.LENGTH_SHORT).show();
    }
}
