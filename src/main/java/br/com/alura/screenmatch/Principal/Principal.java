package br.com.alura.screenmatch.Principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.cglib.core.Local;

import br.com.alura.screenmatch.Model.DadosEpisodeo;
import br.com.alura.screenmatch.Model.DadosFilme;
import br.com.alura.screenmatch.Model.DadosSerie;
import br.com.alura.screenmatch.Model.DadosTemporada;
import br.com.alura.screenmatch.Model.Episodeo;
import br.com.alura.screenmatch.Service.ConsumoApi;
import br.com.alura.screenmatch.Service.ConverterDados;

public class Principal {

    private Scanner sc = new Scanner(System.in);

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=ec05df26";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverterDados converter = new ConverterDados();
    List<DadosTemporada> listaTemporadas = new ArrayList<>();

    public void ExibeMenu() {

        System.out.println("Bem vindo ao Menu de Filmes e Series");
        System.out.println("1 - Filmes");
        System.out.println("2 - Series");
        System.out.println("3 - Sair");

    }

    public List<DadosTemporada> criaListaTemporadas(DadosSerie dadosSerie) {
        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var jsonTemporada = consumoApi
                    .obterDados(ENDERECO + dadosSerie.title().replace(" ", "+") + API_KEY + "&season=" + i);
            DadosTemporada dadosTemporada = converter.obterDados(jsonTemporada, DadosTemporada.class);
            listaTemporadas.add(dadosTemporada);
        }
        return listaTemporadas;
    }

    public void verTemporadas(DadosSerie dadosSerie) {
        List<DadosTemporada> dadosTemporada = criaListaTemporadas(dadosSerie);
        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            System.out.println("Temporada: " + dadosTemporada.get(i - 1).numero() + " - Episódios: "
                    + dadosTemporada.get(i - 1).episodeos().size());
        }
    }

    public void verEpisodeosDasTemporadas(DadosSerie dadosSerie) {
        listaTemporadas
                .forEach(t -> t.episodeos().forEach(e -> System.out.println(t.numero() + " Temporada " + e.title())));
    }

    public void verEpAfterYear(DadosSerie dadosSerie, Integer ano) {
        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Episodeo> episodeos = listaTemporadas.stream()
                .flatMap(t -> t.episodeos().stream()
                        .map(e -> new Episodeo(t.numero(), e)))
                .collect(Collectors.toList());
        episodeos.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .sorted(Comparator.comparing(Episodeo::getDataLancamento))
                .forEach(e -> System.out.println(
                        e.getTemporada() + " - " + e.getTitulo() + " - " + e.getDataLancamento().format(formatter)));
    }

    public void DadosEpNome(DadosSerie dadosSerie, String nomeEpisodio) {
        List<Episodeo> episodeos = listaTemporadas.stream()
                .flatMap(t -> t.episodeos().stream()
                        .map(e -> new Episodeo(t.numero(), e)))
                .collect(Collectors.toList());
        Optional<Episodeo> epBusca = episodeos.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(nomeEpisodio.toUpperCase()))
                .findFirst();

        if (epBusca.isPresent()) {
            Episodeo episodio = epBusca.get();
            System.out.println("Episódio encontrado: " + episodio.getTemporada() + " - " + episodio.getTitulo() + " - "
                    + episodio.getDataLancamento());
        } else {
            System.out.println("Episódio não encontrado.");
        }
    }

    public void MenuOpcoesFilmes() {
        System.out.println("Você escolheu Filmes.");
        System.out.println("Digite o nome do filme: ");
        var nomeFilme = sc.nextLine();
        var jsonFilme = consumoApi.obterDados(ENDERECO + nomeFilme.replace(" ", "+") + API_KEY);
        DadosFilme dadosFilme = converter.obterDados(jsonFilme, DadosFilme.class);
        System.out.println(dadosFilme);
    }

    public void MenuOpcoesSeries() {
        System.out.println("Você escolheu Series.");
        System.out.println("Digite o nome da série: ");
        var nomeSerie = sc.nextLine();
        var jsonSerie = consumoApi.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dadosSerie = converter.obterDados(jsonSerie, DadosSerie.class);
        System.out.println(dadosSerie);
        System.out.println("1 - Ver temporadas");
        System.out.println("2 - Ver episódios");
        System.out.println("3 - Ver episódios por ano");
        System.out.println("4 - Achar dados do episódio por nome ");
        System.out.println("5 - Sair");
        int opcao = sc.nextInt();
        sc.nextLine();
        switch (opcao) {
            case 1:
                verTemporadas(dadosSerie);
                break;
            case 2:
                if (listaTemporadas.isEmpty()) {
                    criaListaTemporadas(dadosSerie);
                } else {
                    verEpisodeosDasTemporadas(dadosSerie);
                }
                break;
            case 3:
                // Em vez de pedir o ano aqui, pedir no método
                System.out.println("Qual ano?");
                var ano = sc.nextInt();
                sc.nextLine();
                if (listaTemporadas.isEmpty()) {
                    criaListaTemporadas(dadosSerie);
                }
                verEpAfterYear(dadosSerie, ano);
                break;
            case 4:
                // Em vez de pedir o nome aqui, pedir no método
                System.out.println("Qual o nome do episódio?");
                var nomeEpisodio = sc.nextLine();
                if (listaTemporadas.isEmpty()) {
                    criaListaTemporadas(dadosSerie);
                }
                DadosEpNome(dadosSerie, nomeEpisodio);
                break;
            case 5:
                System.out.println("Saindo...");
                break;
            default:
                throw new IllegalArgumentException("Opção inválida: " + opcao);
        }

    }

    public void ExecutaMenu() {
        boolean continuar = true;

        while (continuar) {
            ExibeMenu();
            System.out.println("Escolha uma opção:");
            int opcao = sc.nextInt();
            sc.nextLine(); // Limpa o buffer do scanner
            switch (opcao) {
                case 1:
                    MenuOpcoesFilmes();
                    break;
                case 2:
                    MenuOpcoesSeries();
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
