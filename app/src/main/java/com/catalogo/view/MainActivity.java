package com.catalogo.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.catalogo.R;
import com.catalogo.adapter.JogoAdapter;
import com.catalogo.controller.CatalogoController;
import com.catalogo.model.Jogo;
import com.catalogo.model.Opcoes;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CatalogoController controller;
    private JogoAdapter adapter;
    private EditText editBusca;
    private Spinner spinnerFiltro;
    private TextView txtMedia;
    private TextView txtResumo;
    private TextView txtVazio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new CatalogoController(this);
        adapter = new JogoAdapter(this);

        editBusca = findViewById(R.id.editBusca);
        spinnerFiltro = findViewById(R.id.spinnerFiltro);
        txtMedia = findViewById(R.id.txtMedia);
        txtResumo = findViewById(R.id.txtResumo);
        txtVazio = findViewById(R.id.txtVazio);
        ListView lista = findViewById(R.id.listaJogos);
        Button btnBuscar = findViewById(R.id.btnBuscar);
        Button btnTodos = findViewById(R.id.btnTodos);
        Button btnNova = findViewById(R.id.btnNova);

        ArrayAdapter<String> adapterFiltro = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                Opcoes.filtrosStatus()
        );
        adapterFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapterFiltro);

        lista.setAdapter(adapter);
        lista.setEmptyView(txtVazio);
        lista.setOnItemClickListener((parent, view, position, id) -> abrirEdicao(adapter.getJogo(position)));

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                carregar();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnBuscar.setOnClickListener(v -> {
            fecharTeclado();
            carregar();
        });
        editBusca.setOnEditorActionListener((v, actionId, event) -> {
            fecharTeclado();
            carregar();
            return true;
        });
        btnTodos.setOnClickListener(v -> listarTodos());
        btnNova.setOnClickListener(v -> abrirCadastro());
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregar();
    }

    private void listarTodos() {
        editBusca.setText("");
        if (spinnerFiltro.getSelectedItemPosition() == 0) {
            carregar();
        } else {
            spinnerFiltro.setSelection(0);
        }
    }

    private void carregar() {
        String busca = editBusca.getText().toString();
        String status = spinnerFiltro.getSelectedItem().toString();
        List<Jogo> jogos = controller.listar(busca, status);
        adapter.setJogos(jogos);
        txtMedia.setText(controller.textoMedia());
        txtResumo.setText(textoResumo(jogos.size()));
        if (busca.trim().isEmpty() && Opcoes.FILTRO_TODOS.equals(status)) {
            txtVazio.setText("Nenhum jogo cadastrado.\nToque em Novo jogo para começar.");
        } else {
            txtVazio.setText("Nenhum jogo encontrado com esse filtro.");
        }
    }

    private String textoResumo(int quantidade) {
        String jogos = quantidade == 1 ? "1 jogo" : quantidade + " jogos";
        return jogos + " · ordenado por nota";
    }

    private void abrirCadastro() {
        startActivity(new Intent(this, FormActivity.class));
    }

    private void abrirEdicao(Jogo jogo) {
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra(FormActivity.EXTRA_ID, jogo.getId());
        startActivity(intent);
    }

    private void fecharTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View foco = getCurrentFocus();
        if (imm != null && foco != null) {
            imm.hideSoftInputFromWindow(foco.getWindowToken(), 0);
        }
    }
}
