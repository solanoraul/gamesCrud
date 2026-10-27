package com.catalogo.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.catalogo.R;
import com.catalogo.controller.CatalogoController;
import com.catalogo.model.Jogo;
import com.catalogo.model.Opcoes;

import java.util.Locale;

public class FormActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "jogo_id";

    private CatalogoController controller;
    private long jogoId;
    private boolean carregando;

    private TextView txtTituloTela;
    private EditText editTitulo;
    private EditText editAno;
    private EditText editNota;
    private EditText editComentario;
    private Spinner spinnerPlataforma;
    private Spinner spinnerGenero;
    private Spinner spinnerStatus;
    private Button btnSalvar;
    private Button btnExcluir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        controller = new CatalogoController(this);
        jogoId = getIntent().getLongExtra(EXTRA_ID, 0);

        txtTituloTela = findViewById(R.id.txtTituloTela);
        editTitulo = findViewById(R.id.editTitulo);
        editAno = findViewById(R.id.editAno);
        editNota = findViewById(R.id.editNota);
        editComentario = findViewById(R.id.editComentario);
        spinnerPlataforma = findViewById(R.id.spinnerPlataforma);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnExcluir = findViewById(R.id.btnExcluir);
        Button btnVoltar = findViewById(R.id.btnVoltar);

        prepararSpinner(spinnerPlataforma, Opcoes.PLATAFORMAS);
        prepararSpinner(spinnerGenero, Opcoes.GENEROS);
        prepararSpinner(spinnerStatus, Opcoes.STATUS);

        spinnerStatus.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                atualizarCampoNota(!carregando);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        if (jogoId == 0) {
            txtTituloTela.setText("Novo jogo");
            btnExcluir.setVisibility(View.GONE);
            atualizarCampoNota(false);
        } else {
            txtTituloTela.setText("Editar jogo");
            btnExcluir.setVisibility(View.VISIBLE);
            if (!preencherJogo()) {
                return;
            }
        }

        btnVoltar.setOnClickListener(v -> finish());
        btnSalvar.setOnClickListener(v -> salvar());
        btnExcluir.setOnClickListener(v -> confirmarExclusao());
    }

    private void prepararSpinner(Spinner spinner, String[] opcoes) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opcoes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private boolean preencherJogo() {
        Jogo jogo = controller.buscar(jogoId);
        if (jogo == null) {
            Toast.makeText(this, "Jogo não encontrado.", Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
        carregando = true;
        editTitulo.setText(jogo.getTitulo());
        editAno.setText(String.valueOf(jogo.getAno()));
        editComentario.setText(jogo.getComentario() == null ? "" : jogo.getComentario());
        if (jogo.getNota() == null) {
            editNota.setText("");
        } else {
            editNota.setText(String.format(Locale.US, "%.1f", jogo.getNota()));
        }
        selecionar(spinnerPlataforma, jogo.getPlataforma(), Opcoes.PLATAFORMAS);
        selecionar(spinnerGenero, jogo.getGenero(), Opcoes.GENEROS);
        selecionar(spinnerStatus, jogo.getStatus(), Opcoes.STATUS);
        carregando = false;
        atualizarCampoNota(false);
        return true;
    }

    private void selecionar(Spinner spinner, String valor, String[] opcoes) {
        for (int i = 0; i < opcoes.length; i++) {
            if (opcoes[i].equals(valor)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void atualizarCampoNota(boolean limparSeNaoZerado) {
        boolean zerado = Opcoes.STATUS_ZERADO.equals(spinnerStatus.getSelectedItem().toString());
        editNota.setEnabled(zerado);
        if (!zerado && limparSeNaoZerado) {
            editNota.setText("");
        }
    }

    private void salvar() {
        btnSalvar.setEnabled(false);
        CatalogoController.Validacao validacao = controller.salvar(
                jogoId,
                editTitulo.getText().toString(),
                spinnerPlataforma.getSelectedItem().toString(),
                spinnerGenero.getSelectedItem().toString(),
                editAno.getText().toString(),
                spinnerStatus.getSelectedItem().toString(),
                editNota.getText().toString(),
                editComentario.getText().toString()
        );
        if (!validacao.isValido()) {
            btnSalvar.setEnabled(true);
            Toast.makeText(this, validacao.getMensagem(), Toast.LENGTH_LONG).show();
            focar(validacao.getCampo());
            return;
        }
        Toast.makeText(this, "Jogo salvo.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void focar(String campo) {
        if (CatalogoController.CAMPO_TITULO.equals(campo)) {
            editTitulo.requestFocus();
        } else if (CatalogoController.CAMPO_ANO.equals(campo)) {
            editAno.requestFocus();
        } else if (CatalogoController.CAMPO_NOTA.equals(campo)) {
            editNota.requestFocus();
        } else if (CatalogoController.CAMPO_COMENTARIO.equals(campo)) {
            editComentario.requestFocus();
        }
    }

    private void confirmarExclusao() {
        String titulo = editTitulo.getText().toString().trim();
        new AlertDialog.Builder(this)
                .setTitle("Excluir jogo")
                .setMessage("Deseja excluir \"" + titulo + "\"? Essa ação não pode ser desfeita.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Excluir", (dialog, which) -> {
                    controller.excluir(jogoId);
                    Toast.makeText(this, "Jogo excluído.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .show();
    }
}
