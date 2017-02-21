import io.reactivex.Observable;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * Created by uysal.kara on 21.02.2017.
 */
public class Demo {

    public static void main(String[] args) throws IOException {

        GHService ghService = new GHService();
        Observable<User> userObservable = Observable.fromFuture(ghService.getUser());

        Observable<Repo> streamObservable = userObservable.map(
                user -> ghService.getRepos())
                .flatMap(futureWrapped-> Observable.fromFuture(futureWrapped))
                .flatMap(repoStream -> Observable.fromIterable(() -> repoStream.iterator()));

        streamObservable.subscribe( System.out::println);

        Observable<String> repoNames = streamObservable.map(repo -> repo.getName());

        repoNames.forEach(System.out::println);
    }
}

class    GHService {

    Future<User> getUser(){
        return CompletableFuture.completedFuture(new User("uysalk", "1"));
    }

    Future<Stream<Repo>> getRepos(){
        return CompletableFuture.completedFuture(
                Stream.of(new Repo[]{ new Repo(1, "Repo 1"), new Repo(2, "Repo 2")})
        );
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

    public User(){
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