package com.catalogo.controller;

import android.content.Context;

import com.catalogo.dao.JogoDAO;
import com.catalogo.model.Jogo;
import com.catalogo.model.Opcoes;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Regras do catálogo:
 * a nota média considera só jogos com status Zerado;
 * a listagem ordena por nota decrescente e, em empate, por título;
 * os filtros são listar todos, buscar por título e filtrar por status.
 */
public class CatalogoController {

    public static final String CAMPO_TITULO = "titulo";
    public static final String CAMPO_ANO = "ano";
    public static final String CAMPO_NOTA = "nota";
    public static final String CAMPO_COMENTARIO = "comentario";

    private final JogoDAO dao;

    public CatalogoController(Context context) {
        dao = new JogoDAO(context);
    }

    public List<Jogo> listar(String busca, String status) {
        return dao.listar(busca, status);
    }

    public Jogo buscar(long id) {
        return dao.buscarPorId(id);
    }

    public boolean excluir(long id) {
        return dao.excluir(id) > 0;
    }

    public String textoMedia() {
        Double media = dao.mediaZerados();
        if (media == null) {
            return "Nota média dos zerados: nenhum jogo zerado.";
        }
        String valor = String.format(Locale.forLanguageTag("pt-BR"), "%.1f", media);
        return "Nota média dos zerados: " + valor;
    }

    public Validacao salvar(long id, String titulo, String plataforma, String genero, String anoTexto,
                            String status, String notaTexto, String comentario) {
        Validacao validacao = validar(titulo, anoTexto, status, notaTexto, comentario);
        if (!validacao.isValido()) {
            return validacao;
        }

        Jogo jogo = new Jogo();
        jogo.setId(id);
        jogo.setTitulo(titulo.trim());
        jogo.setPlataforma(plataforma);
        jogo.setGenero(genero);
        jogo.setAno(Integer.parseInt(anoTexto.trim()));
        jogo.setStatus(status);
        jogo.setComentario(comentario == null ? "" : comentario.trim());
        jogo.setNota(notaParaSalvar(status, notaTexto));

        if (id == 0) {
            dao.inserir(jogo);
        } else {
            dao.atualizar(jogo);
        }
        return Validacao.ok();
    }

    private Validacao validar(String titulo, String anoTexto, String status, String notaTexto, String comentario) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return Validacao.erro(CAMPO_TITULO, "Informe o nome do jogo.");
        }
        if (titulo.trim().length() > 80) {
            return Validacao.erro(CAMPO_TITULO, "O nome deve ter no máximo 80 caracteres.");
        }

        if (anoTexto == null || anoTexto.trim().isEmpty()) {
            return Validacao.erro(CAMPO_ANO, "Informe o ano de lançamento.");
        }
        int ano;
        try {
            ano = Integer.parseInt(anoTexto.trim());
        } catch (NumberFormatException e) {
            return Validacao.erro(CAMPO_ANO, "Informe um ano numérico válido.");
        }
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        if (ano < 1970 || ano > anoAtual) {
            return Validacao.erro(CAMPO_ANO, "Informe um ano entre 1970 e " + anoAtual + ".");
        }

        if (comentario != null && comentario.trim().length() > 200) {
            return Validacao.erro(CAMPO_COMENTARIO, "O comentário deve ter no máximo 200 caracteres.");
        }

        String notaLimpa = limparNota(notaTexto);
        boolean zerado = Opcoes.STATUS_ZERADO.equals(status);
        if (zerado && notaLimpa.isEmpty()) {
            return Validacao.erro(CAMPO_NOTA, "Informe a nota de 0 a 10 para um jogo zerado.");
        }
        if (!notaLimpa.isEmpty()) {
            try {
                double nota = Double.parseDouble(notaLimpa);
                if (nota < 0 || nota > 10) {
                    return Validacao.erro(CAMPO_NOTA, "A nota deve estar entre 0 e 10.");
                }
            } catch (NumberFormatException e) {
                return Validacao.erro(CAMPO_NOTA, "Informe uma nota numérica entre 0 e 10.");
            }
        }
        return Validacao.ok();
    }

    private Double notaParaSalvar(String status, String notaTexto) {
        if (!Opcoes.STATUS_ZERADO.equals(status)) {
            return null;
        }
        return Double.parseDouble(limparNota(notaTexto));
    }

    private String limparNota(String notaTexto) {
        if (notaTexto == null) {
            return "";
        }
        return notaTexto.trim().replace(',', '.');
    }

    public static class Validacao {
        private final boolean valido;
        private final String mensagem;
        private final String campo;

        private Validacao(boolean valido, String mensagem, String campo) {
            this.valido = valido;
            this.mensagem = mensagem;
            this.campo = campo;
        }

        public static Validacao ok() {
            return new Validacao(true, null, null);
        }

        public static Validacao erro(String campo, String mensagem) {
            return new Validacao(false, mensagem, campo);
        }

        public boolean isValido() {
            return valido;
        }

        public String getMensagem() {
            return mensagem;
        }

        public String getCampo() {
            return campo;
        }
    }
}
