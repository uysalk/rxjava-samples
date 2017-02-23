import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

/**
 * Created by uysal.kara on 21.02.2017.
 */
public interface GitHubService {
    @GET("users")
    Flowable<List<User>> listUsers ();


    @GET("users/{user}/repos")
    Flowable<List<Repo>> listRepos(@Path("user") String user);


}