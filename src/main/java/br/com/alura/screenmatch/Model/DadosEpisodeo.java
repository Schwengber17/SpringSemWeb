package br.com.alura.screenmatch.Model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpisodeo(@JsonAlias("Title")String title,
                            @JsonAlias("Episode") Integer episodio,
                            @JsonAlias("imdbRating")String rate,
                            @JsonAlias("Released")String dataLancamento) {
}
