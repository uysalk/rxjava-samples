/**
 * Created by uysal.kara on 21.02.2017.
 */
public class IntroRetrofit {

    void exercise(){



/*
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/?access_token=248e6198024de99d8fdf0c3889a3654c76758bcc")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();



        GitHubService service = retrofit.create(GitHubService.class);

        Observable<List<Repo>> repos = service.listRepos("uysalk");
        repos.subscribe(
                repoList -> Observable.fromIterable(repoList).map(x->x.getName()).forEach(System.out::println)
        );

        //Flowable<List<User>> users = service.listUsers();

        users.subscribe(
                userList -> Observable.fromIterable(userList).subscribe(user -> service.listRepos(user.getLogin()).subscribe(System.out::println))
                ) ;
         */

    }
}
