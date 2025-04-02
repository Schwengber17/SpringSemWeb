package br.com.alura.screenmatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.alura.screenmatch.Principal.Principal;

import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal();
		principal.ExecutaMenu();
	}
}


// var consumoApi = new ConsumoApi();
		// // var json = consumoApi.obterDados("https://www.omdbapi.com/?i=tt0317219&apikey=ec05df26");
		// // System.out.println(json);

		// ConverterDados converter = new ConverterDados();

		// // DadosFilme dadosFilme = converter.obterDados(json, DadosFilme.class);
		// // System.out.println(dadosFilme);

		// // json = consumoApi.obterDados("https://www.omdbapi.com/?i=tt0773262&season=2&episode=1&apikey=ec05df26");
		// // DadosEpisodeo dadosEpisodeo = converter.obterDados(json, DadosEpisodeo.class);
		// // System.out.println(dadosEpisodeo);

		// var json = consumoApi.obterDados("https://www.omdbapi.com/?i=tt0773262&apikey=ec05df26");
		// DadosSerie dadosSerie = converter.obterDados(json, DadosSerie.class);
		// System.out.println(dadosSerie);

		// List<DadosTemporada> listaTemporadas = new ArrayList<>();
		// DadosTemporada DadosTemporada;

		// for(int i=1;i<=dadosSerie.totalTemporadas();i++){
		// 	json= consumoApi.obterDados("https://www.omdbapi.com/?i=tt0773262&season="+i+"&apikey=ec05df26");
		// 	DadosTemporada = converter.obterDados(json, DadosTemporada.class);
		// 	listaTemporadas.add(DadosTemporada);
		// }

		// listaTemporadas.forEach(System.out::println);