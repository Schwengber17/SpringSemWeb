package br.com.alura.screenmatch.Model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosFilme(@JsonAlias("Title")String title,
                         @JsonAlias("Runtime")String runtime,
                         @JsonAlias("imdbRating") Double rate) {
    
}
