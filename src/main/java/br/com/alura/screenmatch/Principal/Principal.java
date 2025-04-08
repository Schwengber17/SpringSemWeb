package br.com.alura.screenmatch.Principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.cglib.core.Local;

import br.com.alura.screenmatch.Model.DadosEpisodio;
import br.com.alura.screenmatch.Model.DadosFilme;
import br.com.alura.screenmatch.Model.DadosSerie;
import br.com.alura.screenmatch.Model.DadosTemporada;
import br.com.alura.screenmatch.Model.Episodio;
import br.com.alura.screenmatch.Service.ConsumoApi;
import br.com.alura.screenmatch.Service.ConverterDados;

public class Principal {

    private Scanner sc = new Scanner(System.in);

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=ec05df26";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverterDados converter = new ConverterDados();
    List<DadosTemporada> listaTemporadas = new ArrayList<>();

    public int ExibeMenu() {

        System.out.println("Bem vindo ao Menu de Filmes e Series");
        System.out.println("1 - Filmes");
        System.out.println("2 - Series");
        System.out.println("3 - Sair");
        System.out.println("Escolha uma opção:");
        int opcao = sc.nextInt();
        sc.nextLine();
        if (opcao < 1 || opcao > 3) {
            System.out.println("Opção inválida. Tente novamente.");
            ExibeMenu();
        } else {
            System.out.println("Você escolheu a opção: " + opcao);
        }
        return opcao;
    }

    // Método possui gargalo na saída do sistema
    public List<DadosTemporada> criaListaTemporadas(DadosSerie dadosSerie) {
        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            var jsonTemporada = consumoApi
                    .obterDados(ENDERECO + dadosSerie.title().replace(" ", "+") + API_KEY + "&season=" + i);
            DadosTemporada dadosTemporada = converter.obterDados(jsonTemporada, DadosTemporada.class);
            listaTemporadas.add(dadosTemporada);
        }
        return listaTemporadas;
    }

    // Criar metodo para criar lista de episodios

    public void mediaAvaliacao(DadosSerie serie) {
        List<Episodio> episodios = listaTemporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(e -> new Episodio(t.numero(), e)))
                .collect(Collectors.toList());
        System.out.println("Média de avaliação por temporada: ");
        Map<Integer, String> avaliacoesTemp = episodios.stream()
                .filter(e -> e.getAvaliacao() != null && e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> String.format("%.1f", entry.getValue())));
        System.out.println(avaliacoesTemp);
        // Por Estatistica do Spring
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade: " + est.getCount());
    }

    public void verTemporadas(DadosSerie dadosSerie) {
        List<DadosTemporada> dadosTemporada = criaListaTemporadas(dadosSerie);
        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            System.out.println("Temporada: " + dadosTemporada.get(i - 1).numero() + " - Episódios: "
                    + dadosTemporada.get(i - 1).episodios().size());
        }
    }

    public void verEpisodiosDasTemporadas(DadosSerie dadosSerie) {
        listaTemporadas
                .forEach(t -> t.episodios().forEach(e -> System.out.println(t.numero() + " Temporada " + e.title())));
    }

    public void verEpAfterYear(DadosSerie dadosSerie, Integer ano) {
        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Episodio> episodios = listaTemporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(e -> new Episodio(t.numero(), e)))
                .collect(Collectors.toList());
        episodios.stream()
                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
                .sorted(Comparator.comparing(Episodio::getDataLancamento))
                .forEach(e -> System.out.println(
                        e.getTemporada() + " - " + e.getTitulo() + " - " + e.getDataLancamento().format(formatter)));
    }

    public void DadosEpNome(DadosSerie dadosSerie, String nomeEpisodio) {
        List<Episodio> episodios = listaTemporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(e -> new Episodio(t.numero(), e)))
                .collect(Collectors.toList());
        Optional<Episodio> epBusca = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(nomeEpisodio.toUpperCase()))
                .findFirst();

        if (epBusca.isPresent()) {
            Episodio episodio = epBusca.get();
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
        if (listaTemporadas.size() != 0) {
            listaTemporadas.clear();
            criaListaTemporadas(dadosSerie);
        } else {
            criaListaTemporadas(dadosSerie);
        }
        System.out.println("1 - Ver temporadas");
        System.out.println("2 - Ver episódios");
        System.out.println("3 - Ver episódios por ano");
        System.out.println("4 - Achar dados do episódio por nome ");
        System.out.println("5 - Média de avaliação por temporada");
        System.out.println("6 - Sair");
        Integer opcao = sc.nextInt();
        sc.nextLine();
        switch (opcao) {
            case 1:
                verTemporadas(dadosSerie);
                break;
            case 2:
                verEpisodiosDasTemporadas(dadosSerie);
                break;
            case 3:
                // Em vez de pedir o ano aqui, pedir no método
                System.out.println("Qual ano?");
                var ano = sc.nextInt();
                sc.nextLine();
                verEpAfterYear(dadosSerie, ano);
                break;
            case 4:
                // Em vez de pedir o nome aqui, pedir no método
                System.out.println("Qual o nome do episódio?");
                var nomeEpisodio = sc.nextLine();
                DadosEpNome(dadosSerie, nomeEpisodio);
                break;
            case 5:
                mediaAvaliacao(dadosSerie);
                break;
            case 6:
                System.out.println("Saindo...");
                break;
            default:
                throw new IllegalArgumentException("Opção inválida: " + opcao);
        }

    }

    public void ExecutaMenu() {
        boolean continuar = true;

        while (continuar) {
            int opcao2 = ExibeMenu();

            switch (opcao2) {
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
                    throw new IllegalArgumentException("Opção inválida: " + opcao2);
            }
        }
    }
}
