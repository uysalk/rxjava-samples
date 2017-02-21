import io.reactivex.Observable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by uysal.kara on 21.02.2017.
 */
public class IntroRx {

    void exercises(){

        List<String> words = Arrays.asList(
                "the",
                "quick",
                "brown",
                "fox",
                "jumped",
                "over",
                "the",
                "lazy",
                "dogs"
        );

        Observable.just(words)
                .subscribe(word->System.out.println(word));


        Observable.fromIterable(words)
                .subscribe(System.out::println);


        Observable.fromIterable(words)
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, count)->String.format("%2d. %s", count, string))
                .subscribe(System.out::println);

        Observable.fromIterable(words).flatMap(word-> Observable.fromArray(word.split("")))
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, count)->String.format("%2d. %s", count, string))
                .subscribe(System.out::println);

        Observable.fromIterable(words)
                .flatMap(word-> Observable.fromArray(word.split("")))
                .distinct()
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, count)->String.format("%2d. %s", count, string))
                .subscribe(System.out::println);

        Observable.fromIterable(words)
                .flatMap(word-> Observable.fromArray(word.split("")))
                .distinct()
                .sorted()
                .zipWith(Observable.range(1, Integer.MAX_VALUE),
                        (string, count)->String.format("%2d. %s", count, string))
                .subscribe(System.out::println);

    }
}
