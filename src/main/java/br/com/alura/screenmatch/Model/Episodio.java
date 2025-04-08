package br.com.alura.screenmatch.Model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Episodio {
    private Integer temporada;
    private Integer numero;
    private String titulo;
    //private String resumo;
    private Double avaliacao;
    private LocalDate dataLancamento;

    public Episodio(Integer numeroTemporada, DadosEpisodio dadosEpisodeo){
        this.temporada = numeroTemporada;
        this.numero = dadosEpisodeo.episodio();
        this.titulo = dadosEpisodeo.title();
        try{
            this.avaliacao = Double.valueOf(dadosEpisodeo.rate());
        } catch (NumberFormatException e){
            this.avaliacao = 0.0;
        }
        try{
            this.dataLancamento = LocalDate.parse(dadosEpisodeo.dataLancamento());
        }catch (DateTimeParseException e){
            this.dataLancamento = null;
        }
        
    }
    public Integer getTemporada() {
        return temporada;
    }
    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }
    public Integer getNumero() {
        return numero;
    }
    public void setNumero(Integer numero) {
        this.numero = numero;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }
    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }
    public LocalDate getDataLancamento() {
        return dataLancamento;
    }
    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    @Override
    public String toString() {
        return "Episodio [temporada=" + temporada + ", numero=" + numero + ", titulo=" + titulo + ", avaliacao="
                + avaliacao + ", dataLancamento=" + dataLancamento + "]";
    }
}
