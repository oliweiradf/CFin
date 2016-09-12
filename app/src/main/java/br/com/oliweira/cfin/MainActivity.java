package br.com.oliweira.cfin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteCursor;
import android.content.ContentValues;
import android.content.Intent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;


public class MainActivity extends AppCompatActivity {

    private FloatingActionMenu fabMenu;
    private FloatingActionButton addContaFab;
    private FloatingActionButton AddContaCartaoFab;
    private FloatingActionButton cfinFab;
    private FloatingActionButton configFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabMenu = (FloatingActionMenu) findViewById(R.id.menuFab);
        fabMenu.setClosedOnTouchOutside(true);

        addContaFab = (FloatingActionButton) findViewById(R.id.addContaFab);
        AddContaCartaoFab = (FloatingActionButton) findViewById(R.id.AddContaCartaoFab);
        cfinFab = (FloatingActionButton) findViewById(R.id.cfinFab);
        configFab = (FloatingActionButton) findViewById(R.id.configFab);

        addContaFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contasPadroes("NovaCompra");
            }
        });

        AddContaCartaoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contasPadroes("NovaCompraCartao");
            }
        });

        cfinFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VisualizarCFinActivity.class);
                startActivity(intent);
            }
        });

        configFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ConfiguracoesActivity.class);
                startActivity(intent);
            }
        });

        //Criando instancia do banco
        SQLiteDatabase db = openOrCreateDatabase("db_cfin", MODE_PRIVATE, null);

        //***Criação das tabelas auxiliares***

        //tba_config
        StringBuilder sqlConfig = new StringBuilder();
        sqlConfig.append("CREATE TABLE IF NOT EXISTS tba_config(");
        sqlConfig.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlConfig.append("no_config VARCHAR(30), ");
        sqlConfig.append("tp_config VARCHAR(1));");
        db.execSQL(sqlConfig.toString());

        //tba_tipoconta
        StringBuilder sqlContas = new StringBuilder();
        sqlContas.append("CREATE TABLE IF NOT EXISTS tba_tipoconta(");
        sqlContas.append("_id INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sqlContas.append("no_tipoconta VARCHAR(30), ");
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

        db.close();
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

    public void contasPadroes(String nomeActivity){

        //Estacia o Banco de dados
        final SQLiteDatabase db = openOrCreateDatabase("db_cfin", MODE_PRIVATE, null);

        SQLiteCursor csrContas = (SQLiteCursor) db.rawQuery("SELECT * FROM tba_tipoconta;", null);

        if (csrContas.getCount() <= 0) {
            AlertDialog.Builder msg = new AlertDialog.Builder(MainActivity.this);

            msg.setMessage("Deseja cadastrar as contas padrões?");
            msg.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //contas padrões
                    String[] no_conta = {"Celular","Empréstimo a pagar","Empréstimo a receber","Investimento","Poupanca","Plano de saúde","Prestação Carro","Salário"};
                    double[] vl_bruto = {0.00,0.00 ,0.00,0.00,0.00,0.00,0.00,0.00};
                    String[] tp_operador = {"D","D","C","C","C","D","D","C"};

                    for (int i = 0; i < no_conta.length; i++) {
                        ContentValues ctvContas = new ContentValues();
                        ctvContas.put("no_tipoconta", no_conta[i]);
                        ctvContas.put("vl_bruto", vl_bruto[i]);
                        ctvContas.put("tp_operador", tp_operador[i]);
                        db.insert("tba_tipoconta", "_id", ctvContas);
                    }

                    Toast.makeText(getBaseContext(), "Contas padrões salvas com sucesso!", Toast.LENGTH_SHORT).show();

                }
            });
            msg.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this, "Você deve cadastrar pelo menos um tipo de conta.", Toast.LENGTH_SHORT).show();
                    db.close();
                    Intent intent = new Intent(getApplicationContext(),NovoTipoContaActivity.class);
                    startActivity(intent);
                }
            });

            msg.show();

        }else{
            db.close();
            Toast.makeText(MainActivity.this, nomeActivity, Toast.LENGTH_SHORT).show();
            if(nomeActivity.equals("NovaCompra")){
                Intent intent = new Intent(getApplicationContext(), NovaCompraActivity.class);
                startActivity(intent);
            }else if(nomeActivity.equals("NovaCompraCartao")){
                Intent intent = new Intent(getApplicationContext(), NovaCompraCartaoActivity.class);
                startActivity(intent);
            }
        }


    }
}
