Reaktif mimariler:

Asenkron mesaj iletimine dayanan mimariler karmaşıklığa neden olur
ve genel sistemin anlaşılmasını zorlaştırır - sadece
program kodunu okuyup sistemin ne yaptığını anlamak
mümkün olmaktan çıkar diye düşünüyorsanız ve performans ve ölçeklenebilirlik için 
asenkron haberleşme gerçekten ihtiyacınız ise reactive streams'i  gerçekleyen   rx ailesi [http://reactivex.io/] projelerine bakmalısınız.

Aşağıdaki diller için rx ailesinde reaktif eklentiler bulmak  mümkün.

Java: RxJava
JavaScript: RxJS
C#: Rx.NET
C#(Unity): UniRx
Scala: RxScala
Clojure: RxClojure
C++: RxCpp
Lua: RxLua
Ruby: Rx.rb
Python: RxPY
Groovy: RxGroovy
JRuby: RxJRuby
Kotlin: RxKotlin
Swift: RxSwift
PHP: RxPHP
Elixir: reaxive

Asenkron programlamanın neden olduğu callback cehenneminden nasıl kurtulacağımızı java ve rxjava üzerinden görelim.




