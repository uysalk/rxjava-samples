import io.reactivex.Observable;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by uysal.kara on 21.02.2017.
 */
public class Demo {

    static ExecutorService executor;

    public static void rmainWithRetry(String[] args) throws IOException {
        executor = Executors.newFixedThreadPool(10);
        GitHubService ghService = new UnreilableGHService();
        try {
            Observable<Repo> streamObservable = Observable.fromFuture(ghService.getUser()).map(
                    user -> ghService.getRepos(user.getLogin()))
                    .flatMap(futureWrapped -> Observable.fromFuture(futureWrapped))
                    .flatMap(repoStream -> {
                        return Observable.fromIterable(() -> repoStream.iterator());
                    })
                    .retry(3)// retry 3 times
                    .onErrorReturnItem(new Repo(-1, "Dummy repo"))
                    .cache();
            streamObservable.subscribe(System.out::println);

            Observable<String> repoNames = streamObservable.map(repo -> repo.getName());  // cached so it wont call the chain again..
            repoNames.subscribe(System.out::println);
        } finally {
            executor.shutdown();
        }
    }


    public static void main(String[] args) throws IOException {
        GitHubService ghService = new GitHubServiceImpl();
        Observable<Repo> streamObservable = Observable.fromFuture(ghService.getUser()).map(
                user -> ghService.getRepos(user.getLogin()))
                .flatMap(futureWrapped -> Observable.fromFuture(futureWrapped))
                .flatMap(repoStream -> {
                    return Observable.fromIterable(() -> repoStream.iterator());
                })
                .cache();
        streamObservable.subscribe(System.out::println);

        Observable<String> repoNames = streamObservable.map(repo -> repo.getName());  // cached so it won't re-evaluate the call chain again..
        repoNames.subscribe(System.out::println);
        List<String> list = repoNames.toList().blockingGet(); // blocking is avoided till the last moment

        assert (list.contains("Repo 1"));
    }


    public static void imain(String[] args) throws Exception {
        GitHubService ghService = new GitHubServiceImpl();

        Future<User> fUser = ghService.getUser();
        User user = fUser.get(); // blocking
        Future<Stream<Repo>> fRepos = ghService.getRepos(user.getLogin());
        Stream<Repo> repoStream = fRepos.get(); // blocking

        Iterator<Repo> itRepo = repoStream.iterator();

        while (itRepo.hasNext()) {
            System.out.println(itRepo.next());
        }




    }


    public static void imainWithRetry(String[] args) throws Exception {
        executor = Executors.newFixedThreadPool(10);
        GitHubService ghService = new UnreilableGHService();
        int i = 0;

        try {
            Stream<Repo> repoStream = callService(ghService, 3, 1, new Repo(-1, "Dummy repo"));
            List<Repo> repoList = repoStream.collect(Collectors.toList());

            Iterator<Repo> itRepo = repoList.iterator();

            while (itRepo.hasNext()) {
                System.out.println(itRepo.next());
            }

        } finally {
            executor.shutdown();
        }
    }


    static Stream<Repo> callService(GitHubService ghService, int tryLimit, int tryCount, Repo fallback) throws Exception {

        try {
            Future<User> fUser = ghService.getUser();
            User user = fUser.get(); // blocking
            Future<Stream<Repo>> fRepos = ghService.getRepos(user.getLogin());
            return fRepos.get(); // again blocking


        } catch (Exception e) {

            if (tryCount < tryLimit) return callService(ghService, tryLimit, tryCount + 1, fallback);
            else return Stream.of(fallback);
        }

    }
}


interface GitHubService {
    Future<User> getUser();

    Future<Stream<Repo>> getRepos(String userLogin) throws Exception;
}

class GitHubServiceImpl implements GitHubService {

    public Future<User> getUser() {
        return CompletableFuture.completedFuture(new User("uysalk", "1"));
    }

    public Future<Stream<Repo>> getRepos(String userLogin) throws Exception {
        return CompletableFuture.completedFuture(Stream.of(new Repo[]{new Repo(1, "Repo 1"), new Repo(2, "Repo 2")}));


    }
}

class UnreilableGHService implements GitHubService {

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