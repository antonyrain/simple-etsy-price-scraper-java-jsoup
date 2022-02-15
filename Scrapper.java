import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * Example program to list prices from some shop on Etsy marketplace.
 */
public class Scrapper {

    public static StringBuilder url;

    private static final String PRODUCT_CARD_CLASS = "js-merch-stash-check-listing";
    private static final String PRODUCT_NAME_CLASS = "wt-text-caption";
    private static final String PRODUCT_PRICE_CLASS = "currency-value";
    private static final String PRODUCT_LINK_CLASS = "listing-link";
    private static final String EMPTY_STATE_CLASS = "empty-state";

    Scrapper(String url) {
        Scrapper.url = new StringBuilder(url);
    }

    class Product {

        private String name;
        private String price;
        private String link;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPrice() {
            return price;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getLink() {
            return link;
        }
    }

    public List<Product> extractData() throws IOException {
        List<Product> items = new ArrayList<>();
        int iterNum = 2;
        while (true) {
            Document doc = Jsoup.connect(url.toString()).get();
            Elements pagination = doc.getElementsByClass(EMPTY_STATE_CLASS);
            if (!pagination.isEmpty()) {
                break;
            } else {
                Elements content = doc.getElementsByClass(PRODUCT_CARD_CLASS);
                for (Element i : content) {
                    Product product = new Product();
                    Elements title_element = i.getElementsByClass(PRODUCT_NAME_CLASS);
                    if (!title_element.isEmpty()) {
                        product.setName(title_element.text());
                    }
                    Elements price_element = i.getElementsByClass(PRODUCT_PRICE_CLASS);
                    if (!price_element.isEmpty()) {
                        product.setPrice(price_element.text());
                    }
                    Elements link_element = i.getElementsByClass(PRODUCT_LINK_CLASS);
                    if (!link_element.isEmpty()) {
                        Element elem = link_element.select("a").first();
                        product.setLink(elem.attr("href"));
                    }
                    items.add(product);
                }
                if (iterNum == 2) {
                    Scrapper.url.append("?page=").append(iterNum);
                } else if (iterNum <= 10) {
                    Scrapper.url.setLength(Scrapper.url.length()-1);
                    Scrapper.url.append(iterNum);
                } else {
                    Scrapper.url.setLength(Scrapper.url.length()-2);
                    Scrapper.url.append(iterNum);
                }
            }
            System.out.println("Page number: " + iterNum);
            iterNum++;
        }
        return items;
    }

    public static void main(String[] args) throws IOException {
        Scanner myObj = new Scanner(System.in);
        String etsyURL;
        System.out.println("Input URL, for example: https://www.etsy.com/ shop/xyz/---there /xyz/ is a shop name " +
                           "and press Enter:");
        etsyURL= myObj.nextLine();
        Scrapper scrapper = new Scrapper(etsyURL);
        List<Product> products = scrapper.extractData();
        for (Product item : products) {
            System.out.println(item.getName());
            System.out.println(item.getPrice());
            System.out.println(item.getLink());
        }
    }
}
