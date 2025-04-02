package br.com.alura.screenmatch.Principal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import br.com.alura.screenmatch.Model.DadosEpisodeo;
import br.com.alura.screenmatch.Model.DadosFilme;
import br.com.alura.screenmatch.Model.DadosSerie;
import br.com.alura.screenmatch.Model.DadosTemporada;
import br.com.alura.screenmatch.Service.ConsumoApi;
import br.com.alura.screenmatch.Service.ConverterDados;

public class Principal {
    
    private Scanner sc = new Scanner(System.in);

    private final String ENDERECO= "https://www.omdbapi.com/?t=";
    private final String API_KEY =  "&apikey=ec05df26";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverterDados converter = new ConverterDados();

    public void ExibeMenu(){

        System.out.println("Bem vindo ao Menu de Filmes e Series");
        System.out.println("1 - Filmes");
        System.out.println("2 - Series");
        System.out.println("3 - Sair");

    }

    public void PercorreDadosTemporada(List<DadosTemporada> listaTemporadas){
       
    }

    public void ExecutaMenu() {
        boolean continuar = true; 
                
        while (continuar) {
            ExibeMenu();
            System.out.println("Escolha uma opção:");
            int opcao = sc.nextInt();

            switch (opcao) {
                case 1:
                    System.out.println("Digite o nome do filme: ");
                    var nome = sc.next();
                    var json = consumoApi.obterDados(ENDERECO+nome.replace(" ", "+")+API_KEY);
                    DadosFilme dadosFilme = converter.obterDados(json, DadosFilme.class);
                    System.out.println(dadosFilme);
                    break;
                case 2:
                    System.out.println("Você escolheu Series.");
                    System.out.println("Digite o nome da série: ");
                    var nomeSerie = sc.next();

                    var jsonSerie = consumoApi.obterDados(ENDERECO+nomeSerie.replace(" ", "+")+API_KEY);
                    DadosSerie dadosSerie = converter.obterDados(jsonSerie, DadosSerie.class);

                    System.out.println(dadosSerie);

                    System.out.println("Deseja ver as temporadas? (S/N)");
                    var resposta = sc.next().toUpperCase();

                    List<DadosTemporada> listaTemporadas = new ArrayList<>();
                    

                    if(resposta.equals("S")){
                        for(int i=1;i<=dadosSerie.totalTemporadas();i++){
                            var jsonTemporada = consumoApi.obterDados(ENDERECO+nomeSerie.replace(" ", "+")+API_KEY+"&season="+i);
                            DadosTemporada dadosTemporada = converter.obterDados(jsonTemporada, DadosTemporada.class);
                            System.out.println(dadosTemporada.numero());
                            System.out.println("Episódios: "+dadosTemporada.episodeos().size());
                            listaTemporadas.add(dadosTemporada);
                        }
                        System.out.println("Quer ver os episódios de cada temporada? (S/N)");
                        var respostaEpisodios = sc.next().toUpperCase();
                        if(respostaEpisodios.equals("S")){
                            listaTemporadas.forEach(t -> t.episodeos().forEach(e -> System.out.println(t.numero() + " Temporada " + e.title())));
                            List<DadosEpisodeo> dadosEpisodeos = listaTemporadas.stream()
                                .flatMap(t -> t.episodeos().stream())
                                .collect(Collectors.toList());
                                //Collectoros é mutável .toList() é imutável

                            dadosEpisodeos.stream()
                                .filter(e -> !e.rate().equals("N/A"))
                                .sorted(Comparator.comparing(DadosEpisodeo::rate).reversed())
                                .limit(5)
                                .forEach(e -> System.out.println(e.title() + " - " + e.rate()));
                        }
                        else{
                            System.out.println("Você escolheu não ver os episódios. Mas aqui está um top 5 de episódios:");
                        }
                    }else{
                        System.out.println("Você escolheu não ver as temporadas.");
                    }
                    break;
                case 3:
                    System.out.println("Saindo...");
                    continuar = false;
                break;
                    default:
                    throw new IllegalArgumentException("Opção inválida: " + opcao);
            }
        }
    }
}
