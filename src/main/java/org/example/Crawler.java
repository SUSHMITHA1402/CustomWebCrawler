package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;

public class Crawler {
    // HashSet of all unique url links for data retrieval from a page through recursion like cache for urls
    private HashSet<String> urlLink;
    // Define Maximum possible recursive page url retrieval depth
    private int MAX_DEPTH = 2;
    // Our Database connection
    public Connection connection;
    // Constructor for Crawler that creates new urlLink hashset
    public Crawler(){
        // Set up the connection with My database from crawler
        connection = MyDatabaseConnection.getConnection();
        urlLink = new HashSet<String>();
    }
    // Method to get text and URLs of the given url Page
    public void getPageTextAndLinks(String url, int depth){
        // Check if the url not present in the hashset
        if(!urlLink.contains(url)){
            // Then add url to hashset and print the url
            if(urlLink.add(url)){
                System.out.println(url);
            }
            try{
                // Parsing HTML object of given url to Java document object using the jsoup library with given delay
                Document document = Jsoup.connect(url).timeout(5000).get();
                // Get the text content of maximum 500 length from the java document object
                String text = document.text().length()<501? document.text() : document.text().substring(0,500);
                // Print the text
                System.out.println(text);
                // Insert the data into pages table in DB
                PreparedStatement preparedStatement = connection.prepareStatement("Insert into pages values(?,?,?)");
                preparedStatement.setString(1,document.title());
                preparedStatement.setString(2,url);
                preparedStatement.setString(3,text);
                preparedStatement.executeUpdate();

                // Increase recursive depth of url
                depth++;
                // If the url depth recursion reaches maximum depth return to the home url
                if(depth>MAX_DEPTH){
                    return;
                }
                // Get all the available hyperlinks present from current url page
                Elements availableLinksOnPage = document.select("a[href]");
                // Get Text and URL links present in each sub urls in the given url page using absHref url of element
                for(Element currLink: availableLinksOnPage){
                    getPageTextAndLinks(currLink.attr("abs:href"),depth);
                }
            }
            catch (IOException ioException){
                ioException.printStackTrace();
            }
            catch (SQLException sqlException){
                sqlException.printStackTrace();
            }


        }
    }


    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        // Initialise the starting url for the crawler to get data from Internet
        crawler.getPageTextAndLinks("https://www.javatpoint.com/",0);
    }
}