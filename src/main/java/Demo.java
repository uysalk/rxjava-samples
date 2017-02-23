import io.reactivex.Observable;

import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * Created by uysal.kara on 21.02.2017.
 */
public class Demo {

    static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {

        IGitHubService ghService = new GHService();

        Observable<Repo> streamObservable = Observable.fromFuture(ghService.getUser()).map(
                user -> ghService.getRepos(user.getLogin()))
                .flatMap(futureWrapped -> Observable.fromFuture(futureWrapped))
                .flatMap(repoStream -> {
                    return Observable.fromIterable(() -> repoStream.iterator());
                })
                //.retry()// retry until success
                .retry(3)// retry 3 times
                .onErrorReturnItem(new Repo(-1, "Dummy repo"))
                .cache();
        streamObservable.subscribe(System.out::println);

        Observable<String> repoNames = streamObservable.map(repo -> repo.getName());  // cached so it wont call the chain again..
        repoNames.subscribe(System.out::println);
        executor.shutdown();
    }

    public static void imperativeMain(String[] args) throws Exception {

        GHService ghService = new GHService();
        Future<User> fUser = ghService.getUser();
        User user = fUser.get(); // blocking
        Future<Stream<Repo>> fRepos = ghService.getRepos(user.getLogin());
        Stream<Repo> repos = fRepos.get(); // again blocking
        Iterator<Repo> itRepo = repos.iterator();

        while (itRepo.hasNext()) {
            System.out.println(itRepo.next());
        }
    }
}
interface IGitHubService {
    Future<User> getUser();
    Future<Stream<Repo>> getRepos(String userLogin) throws Exception;
}

class GHService implements IGitHubService {

    public Future<User> getUser() {


        return CompletableFuture.completedFuture(new User("uysalk", "1"));
    }

    public Future<Stream<Repo>> getRepos(String userLogin) throws Exception {
        Callable<Stream<Repo>> callable = new Callable<Stream<Repo>>() {

            @Override
            public Stream<Repo> call() throws Exception {
                int i = new Random().nextInt(100);
                System.out.printf("Trying for %d%n", i);
                if (i % 2 == 0)
                    throw new RuntimeException("Oppps");
                else
                    return Stream.of(new Repo[]{new Repo(1, "Repo 1"), new Repo(2, "Repo 2")});
            }
        };

        return Demo.executor.submit(callable);
    }
}

class Repo {

    public Repo(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Repo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Integer id;
    private String name;

}

class User {

    String login;
    String id;

    public User() {
    }

    public User(String login, String id) {
        this.login = login;
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}