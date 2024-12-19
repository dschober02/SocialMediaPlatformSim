import java.util.*;
import java.io.*;

/*
    Homework # 4
    Name: Derek Schober
    Description: This is a Social Media Platform simulation that implements an observer pattern between users and
                 platforms, strategy pattern with the different searching algorithms, and a factory pattern that
                 creates Social Media Platform objects.
 */


/**********************************************************/
/* Feed                                                   */
/**********************************************************/

class Feed{
    private String _title;
    private String _desc;
    public Feed(){}
    public Feed(String a, String b){
        _title = a; _desc = b;
    }
    public void setTitle(String a){_title = a;}
    public void setDesc(String a){_desc = a;}
    public String getTitle(){return _title;}
    public String getDesc(){return _desc;}
    public String toString(){return _title + " " + _desc;}
}

/**********************************************************/
/* NewsFeed				                                  */
/**********************************************************/

class NewsFeed {
    private ArrayList<Feed> _newsFeed;
    public NewsFeed(){_newsFeed = new ArrayList<Feed>();}
    public NewsFeed(String fileName) throws IOException {
        _newsFeed = new ArrayList<>();
        Scanner scanner = new Scanner(new File(fileName));
        while(scanner.hasNextLine()){
            String[] strings = scanner.nextLine().split(";");
            if (strings.length != 0)
                _newsFeed.add(new Feed(strings[0], strings[1]));
        }

    }
    public Feed getRandomFeed(){
        return _newsFeed.get(new Random().nextInt(_newsFeed.size()));
    }
}
/**********************************************************/
/* Strategy Pattern Interface/Classes                     */
/**********************************************************/

// Provided: Strategy Interface 
interface AnalysisBehavior {
    // analyze might be useful as a double if it were implemented to return the percentage of a given words presence
    double analyze(String[] words, String searchWord);
}

// Task: Complete Class CountIfAnalysis
class CountIfAnalysis implements AnalysisBehavior {

    // In an array of words, we return 1 if it is found and a 0 if not
    // I returned out of the for loop because the loop body was only 2 lines (please spare me)
    public double analyze(String[] words, String searchWord) {
        for (String word : words) {
            if (word.equalsIgnoreCase(searchWord)) {
                return 1.0;
            }
        }
        return 0;
    }
}
// Task: Complete Class CountAllAnalysis
class CountAllAnalysis implements AnalysisBehavior {

    public double analyze(String[] words, String searchWord) {
        int ctr = 0;
        for (String word : words) {
            if (word.equalsIgnoreCase(searchWord)) {
                ctr++;
            }
        }
        return ctr;
    }
}

/**********************************************************/
/* Observer Pattern Interface/Classes                     */
/**********************************************************/

interface Subject {  // Notifying about state changes 
    void subscribe(Observer obs);
    void unsubscribe(Observer obs);
    void notifyObservers(Feed f);
}

interface Observer {  // Waiting for notification of state changes 
    void update(Feed f, String platformName);
}


abstract class SocialMediaPlatform implements Subject {
    private String _name;
    private ArrayList<Feed> _feed;
    private ArrayList<Observer> _observers;
    private int _updateRate;

    public SocialMediaPlatform(String n, int x){
        _name = n;
        _feed = new ArrayList<Feed>();
        _observers = new ArrayList<Observer>();
        _updateRate = x;
    }
    public void addFeed(Feed f){_feed.add(f);}
    public Feed getFeed(int i){return _feed.get(i);}
    public int getRate(){return _updateRate;}
    public String getName(){return _name;}
    public int size(){return _feed.size();}
    public void subscribe(Observer obs){_observers.add(obs);}
    public void unsubscribe(Observer obs){_observers.remove(obs);}
    public void notifyObservers(Feed f){
        for (Observer observer : _observers)
            observer.update(f, _name);
    }
    public void generateFeed(NewsFeed nf){
        Random rand = new Random();
        if (rand.nextInt(100) < this._updateRate){
            Feed f = nf.getRandomFeed();
            _feed.add(f);
            notifyObservers(f);
        }
    }
    public double analyzeFeed(String w, AnalysisBehavior ab){
        // we will start by iterating through the feed array
        ArrayList<String> words = new ArrayList<>();
        for (Feed f : _feed) {
            // Because  the amount of words in each title and desc is not uniform, we will have to use an ArrayList
            // then turn it into an array
            String all = f.getTitle() + " " + f.getDesc();
            String word = "";
            // for this algorithm, we are assuming "words" only contain alphabetic characters,
            // apostrophes, and hyphens, but, no numbers or other punctuation
            for (int i = 0; i < all.length(); i++) {
                if (Character.isLetter(all.charAt(i)) || all.charAt(i) == '\'' || all.charAt(i) == '-') {
                    word += all.charAt(i);
                } else
                    if (!word.isEmpty()) {
                        words.add(word);
                        word = "";
                    }
            }
        }
        // from here we just use the built-in convertor to change the arrayList to an array then return the analysis
        // with the given behavior
        String[] wordArray = words.toArray(new String[words.size()]);
        return ab.analyze(wordArray, w);
    }
    public String toString(){
        String s = "";
        for (Feed f: _feed)
            s = s + f.getTitle() + ", " + f.getDesc() + "\n";
        return s;
    }
}

// Concrete Social Media Platforms 
class Instagram extends SocialMediaPlatform {
    public Instagram() {
        super("Instagram", 30);  // 30% update rate 
    }
}

class MySpace extends SocialMediaPlatform {
    public MySpace() {
        // I decided to make MySpace because that was one of the larger platforms when social media
        // I made MySpace update slower because it is an old platform
        super("MySpace", 10); // 10% update rate
    }
}

class Kik extends SocialMediaPlatform {
    public Kik() {
        // I made Kik to put it on the map, a very underused platform
        // Kik is very laggy, so I made the update rate implicate that
        super("Kik", 11); // 11% update rate
    }
}

class User implements Observer{
    private String _name;
    private ArrayList<SocialMediaPlatform> _myfeeds;
    public User(){_myfeeds = new ArrayList<SocialMediaPlatform>();}
    public User(String s){
        _name = s;
        _myfeeds = new ArrayList<SocialMediaPlatform>();
    }
    public void addPlatform(SocialMediaPlatform smp){_myfeeds.add(smp);}
    public void update(Feed f, String s){
        for (int i=0; i<_myfeeds.size(); i++){
            SocialMediaPlatform smp = _myfeeds.get(i);
            if (smp.getName().equals(s))
                _myfeeds.get(i).addFeed(f);
        }
    }
    public String toString(){
        String s = "";
        for (SocialMediaPlatform smp : _myfeeds) {
            for (int i=0; i<smp.size(); i++){
                Feed f = smp.getFeed(i);
                s = s + f.getTitle() + ", " + f.getDesc() + "\n";
            }
        }
        return s;
    }
}

/**********************************************************/
/* Factory Pattern Interface/Classes                      */
/**********************************************************/

// Factory Creator Interface 
interface SMPFactory {
    SocialMediaPlatform createPlatform();
}

// Concrete Factory classes for each platform 
class InstagramFactory implements SMPFactory {
    public SocialMediaPlatform createPlatform() {
        return new Instagram();
    }
}

class MySpaceFactory implements SMPFactory {
    public SocialMediaPlatform createPlatform() {
        return new MySpace();
    }
}

class KikFactory implements SMPFactory {
    public SocialMediaPlatform createPlatform() {
        return new Kik();
    }
}


public class Main {

    public static void subscribe(User user){
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        // Create main newsfeed from file 
        NewsFeed nf = new NewsFeed("data.txt");

        // Create SMP factories 
        SMPFactory instagramFactory = new InstagramFactory();
        SMPFactory kikFactory = new KikFactory();
        SMPFactory myspaceFactory = new MySpaceFactory();
        // Create the platforms container and add SMPs 
        ArrayList<SocialMediaPlatform> platforms = new ArrayList<>();
        platforms.add(instagramFactory.createPlatform());
        platforms.add(kikFactory.createPlatform());
        platforms.add(myspaceFactory.createPlatform());

        // Create Users and subscribe 
        User user1 = new User("Alice");
        user1.addPlatform(instagramFactory.createPlatform());
        user1.addPlatform(kikFactory.createPlatform());
        platforms.get(0).subscribe(user1); // notice 0 - not the best yet
        platforms.get(1).subscribe(user1);


        User user2 = new User("Derek");
        user2.addPlatform(instagramFactory.createPlatform());
        user2.addPlatform(myspaceFactory.createPlatform());
        platforms.get(0).subscribe(user2);
        platforms.get(2).subscribe(user2);

        User user3 = new User("Nathan");
        user3.addPlatform(instagramFactory.createPlatform());
        user3.addPlatform(kikFactory.createPlatform());
        user3.addPlatform(myspaceFactory.createPlatform());
        platforms.get(0).subscribe(user3);
        platforms.get(1).subscribe(user3);
        platforms.get(2).subscribe(user3);

        // Run a simulation to generate random feeds for the SMPs
            // Creating a 20-second loop
        long start = System.currentTimeMillis();
        for (int i = 0; i < 20; i++) {
             for (SocialMediaPlatform smp : platforms) {
                 smp.generateFeed(nf);
                 System.out.println("Generating feed for " + smp.getName() + "...");
             }
        }

        // Perform analysis
        AnalysisBehavior all = new CountAllAnalysis();
        AnalysisBehavior has = new CountIfAnalysis();
        String[] words = new String[] {"covid-19", "podcast", "and"};
        for (SocialMediaPlatform smp : platforms) {
            for (String word : words) {
                // For testing my analysis behavior, I chose to only execute the CountAll behavior if the CountIf behavior
                // concluded that there was an instance of the search-word. I thought this was appropriate because there would
                // be duplicate outputs if there is no instance of a word. And if the word does exist, I chose to display
                // the number of words and not if it exists, because if there are > 0 numbers of instances, that implies
                // it exists.
                if (smp.analyzeFeed(word, has) == 0){
                    System.out.println("'" + word + "'" + " was not found in " + smp.getName());
                }
                else
                    System.out.println("'" + word + "'" + " was found in " + smp.getName() + " " + smp.analyzeFeed(word, all) + " times" );
            }
        }
        // Print Users' Contents
        System.out.println("\n\tAlice's Feed: \n"  + user1);
        System.out.println("\n\n\tDerek's Feed: \n"  + user2);
        System.out.println("\n\n\tNathan's Feed: \n"  + user3);
    }
}