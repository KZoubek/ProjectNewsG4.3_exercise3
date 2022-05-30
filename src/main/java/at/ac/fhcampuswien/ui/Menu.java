package at.ac.fhcampuswien.ui;

import at.ac.fhcampuswien.controllers.AppController;
import at.ac.fhcampuswien.downloader.ParallelDownloader;
import at.ac.fhcampuswien.downloader.SequentialDownloader;
import at.ac.fhcampuswien.exception.NewsApiException;
import at.ac.fhcampuswien.models.Article;
import at.ac.fhcampuswien.models.Source;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.*;
import java.util.*;
import java.util.function.*;

public class Menu {
    private static final String INVALID_INPUT_MESSAGE = "No valid input. Try again";
    private static final String EXIT_MESSAGE = "Bye bye!";
    private AppController controller;

    public void start(){
        String input;
        controller = new AppController();

        do{
            System.out.println(getMenuText());
            input = readLine();
            handleInput(input);
        } while(!input.equals("q"));

    }

    private void handleInput(String input){
        try{
            switch (input) {
                case "a" -> getTopHeadlinesAustria(controller);
                case "b" -> getAllNewsBitcoin(controller);
                case "y" -> getArticleCount(controller);
                case "q" -> printExitMessage();
                case "c" -> getProviderWithMostArticles(controller);
                case "d" -> getLongestAuthorName(controller);
                case "e" -> countArticlesFromReuters(controller);
                case "f" -> getArticleWithShortTitle(controller);
                case "g" -> sortArticlesByContentLength(controller);
                case "h" -> downloadURLs();
                default -> printInvalidInputMessage();
            }
        } catch (NewsApiException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void sortArticlesByContentLength(AppController controller) throws NewsApiException {
        if (controller.getArticles() == null) {
            throw new NewsApiException("There are currently no articles");
        }
        Comparator<Article> compByLength = (a1, a2) -> (a1.getDescription().length() != a2.getDescription().length())
                ? (a1.getDescription().length() - a2.getDescription().length()): a1.getDescription().compareTo(a2.getDescription());

        System.out.println("The articles sorted by their length of description are given below:");
        controller.getArticles().stream().sorted(compByLength).forEach(System.out::println);
    }

    private void getArticleWithShortTitle(AppController controller) throws NewsApiException {
        if (controller.getArticles() == null) {
            throw new NewsApiException("There are currently no articles");
        }
        List<Article> reutersArticles =  controller.getArticles().stream().filter(a -> a.getTitle().length() < 15).collect(Collectors.toList());
        System.out.println("The articles whose length is smaller than 15 characters are : ");
        reutersArticles.forEach(System.out::println);
        System.out.println();
    }

    private void countArticlesFromReuters(AppController controller) throws NewsApiException {
        if (controller.getArticles() == null) {
            throw new NewsApiException("There are currently no articles");
        }
        Stream<Article> reutersArticles = controller.getArticles().stream().filter(a -> "Reuters".equals(a.getSource().getName()));
        System.out.println("The number of articles from Reuters is : " + reutersArticles.count());
    }

    private void getLongestAuthorName(AppController controller) throws NewsApiException {
        if (controller.getArticles() == null) {
            throw new NewsApiException("There are currently no articles");
        }
        Comparator<Article> longNameCmp = (a1, a2) ->
                (a1.getAuthor() == null)  ? 0:
                        (a2.getAuthor() == null) ? 1: a1.getAuthor().length() - a2.getAuthor().length();

        String name = controller.getArticles().stream().max(longNameCmp).get().getAuthor();
        System.out.println("The author with longest name is: " + name);
    }

    private void getProviderWithMostArticles(AppController controller) throws NewsApiException {
        if (controller.getArticles() == null) {
            throw new NewsApiException("There are currently no articles");
        }

        List<Article> articles = controller.getArticles();
        List<Source> providers = articles.stream().map(Article::getSource).collect(Collectors.toList());

        Map<Source, Long> map =  providers.stream().collect(
                Collectors.groupingBy(
                        Function.identity(),
                        HashMap::new, // can be skipped
                        Collectors.counting()
                )
        );

        Source maxProvider = map.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
        System.out.println("The provider with most frequency is: "+maxProvider.getName() );
    }

    // Method is needed for exercise 4 - ignore for exercise 2 solution
    private void downloadURLs(){
        int resultSequential = controller.downloadURLs(new SequentialDownloader());
        // TODO print time in ms it took to download URLs sequentially

        // TODO implement the process() function in ParallelDownloader class
        int resultParallel = controller.downloadURLs(new ParallelDownloader());

        // TODO print time in ms it took to download URLs parallel
    }

    private void getArticleCount(AppController controller) {
        System.out.println("Number of articles: " + controller.getArticleCount());
    }

    private void getTopHeadlinesAustria(AppController controller) {
        List<Article> articleList = controller.getTopHeadlinesAustria();

        for( Article a : articleList) {
            System.out.println(a);
        }
    }

    private void getAllNewsBitcoin(AppController controller) {
        System.out.println(controller.getAllNewsBitcoin());
    }

    public static void printExitMessage(){
        System.out.println(EXIT_MESSAGE);
    }

    public static void printInvalidInputMessage(){
        System.out.println(INVALID_INPUT_MESSAGE);
    }

    private static String getMenuText(){
        return """
                *****************************
                *   Welcome to NewsApp   *
                *****************************
                Enter what you wanna do:
                a: Get top headlines austria
                b: Get all news about bitcoin
                y: Count articles
                q: Quit program
                c: Get provider with most articles
                d: Get longest author name
                e: Count articles from Reuters
                f: Get articles with short title
                g: Sort articles by content length
                h: Download URLs
                """;
    }

    private static String readLine() {
        String value;
        Scanner scanner = new Scanner(System.in);
        value = scanner.nextLine();
        return value.trim();
    }

}
