# Fortune Cookie - aplikacja spring cloud  - krok po kroku

![Fortune Cookie](/images/fortune-cookie.png)

#  I Response / hosts 


## I wpis w /etc/hosts

###  Symulujemy domeny dla eurek i serwera configuracyjnego, dla konfiguracji serwisów

	#START  fortune-cookie
	127.0.0.1       eureka-peer1
	127.0.0.1       eureka-peer2
	127.0.0.1       config-server
	#END fortune-cookie

## II model - Response 


### Poniższa klasa będzie używana jako zależność przez serwisy (endpointy serwisów zwrcają obiekt Response)

####1. Utwórz pusty projekt maven 

	<groupId>workshop.sc.model</groupId>
    	<artifactId>response</artifactId>
    <version>1.0</version>

####2. Dodaj wersję Javy i zależności w maven:

	<properties>
	    <maven.compiler.target>11</maven.compiler.target>
	    <maven.compiler.source>11</maven.compiler.source>
	</properties>

lombok

	 <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.12</version>
        </dependency>
    </dependencies>
	
	
####3. utwórz klasę workshop.sc.model.Response

  	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public class Response {

	    private String service;
	    private String msg;

	}
    
####4. mvn clean install 

    Po komendzie powstanie biblioteka w lokalnym repo ~/.m2, do wykorzystania w serwisach:
    ~/.m2/repository/workshop/sc/model/response/1.0/response-1.0.pom
    
#  II Config Server 

## I wygeneruj projekt Spring Boot z zależnością spring-cloud-config-server (initalizr: Config Server)


## II dodaj do klasy głównej adnotację @EnableConfigServer

## III application.yml (.properties) 

### 1. wskazanie na repozytorium git

	spring:
     cloud:
       config:
         server:
           git:
     #     wskazanie na katalog git utworzony lokalnie (zadziała zarówno dla linux jak i win)
     #       uri: file:///${user.home}/localrepo
     #     wskazanie na repozytorium na github
             uri: https://github.com/lukasze/fortune-cookie.git
### 2. ustawienie portu (domyślny dla spring cloud config)
	
    server:
      port: 8888
      
### 3. test - przetestuj w przeglądarce, czy serwer zwraca konfiguracje dla eureki i serwisów 

	localhost:8888/<nazwa-aplikacji-i-pliku-konfiguracyjnego>/<nazwa-profilu>
	
	
#  III Eurkea Server 


## I Wygeneruj projekt Spring Boot z zależnościami:
	
	 spring-cloud-starter-config (initalizr: Config Client)
	 spring-cloud-starter-netflix-eureka-server (initalizr: Eureka Server)

### Uwaga, w nowszych wersjach spriong cloud (od 2020.0.1), potrzebujemy dla klientów serwera konfiguracyjnego dodać jeszcze jedną zależność:
	https://stackoverflow.com/questions/65430810/config-client-is-not-working-in-spring-boot 
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bootstrap</artifactId>
        </dependency>

## II Aktywuj eurekę - dodaj do klasy głównej adnotację @EnableEurekaServer
	Nie potrzeba adnotacji dla klienta serwera konfiguracyjnego - wystarczy zależność i wskazanie (w pliku bootstrap.yml), gdzie znajduje się serwer - jeśli nie wskażemy, domyślnie: localhost:8888.

## III bootstrap.yml (zamiast application.yml)

	Wpisy z bootstrap.yml będą pobierane wcześniej niż z application.yml - potrzebne do znalezienia serwera konfiguracyjnego. 

### 1. nazwa aplikacji ( identyczna, jak nazwa pliku konfiguracyjnego w git dla danej aplikacji)
	spring:
  	  application:
       name: eureka-server
	
### 2. config server uri
	
	spring:
	  application:
       name: eureka-server
	  cloud:
	    config:
	    # domenę config-server dodaliśmy do pliku host w kroku I
	      uri: http://config-server:8888
	      
## IV Test

### 1. Uruchom dwie instacje aplikacji z profilami

	Profile takie, jak w pliku konfiguracyjnym na githubie: peer1 i peer2 
	( Konfiguracja profilu w IntelliJ: 2x shift -> edit configurations)
	
### 2. Wpis w logu
	Podczas uruchomienia powinno pojawić się: 
	Fetching config from server at : http://config-server:8888,
	poniżej profil: peer1 / peer 2.
	
### 3. Przeglądarka - powinniśmy dostać się do 2 instancji:

	http://eureka-peer1:8761
	http://eureka-peer1:8762
	
	
#  IV Activity Service 



## I Wygeneruj projekt Spring Boot z zależnościami:

	 Response (zainstalowaliśmy w lokalnym repo .m2 w punkcie pierwszym)
	 spring-boot-starter-web (initalizr: Web)
	 spring-cloud-starter-config (initalizr: Config Client)
	 spring-cloud-starter-netflix-eureka-client (initalizr: Eureka Discovery Client)



## II bootstrap.yml (zamiast application.yml)

	bootstrap.yml (plik konfiguracyjny o tej nazwie będzie użyty wcześniej, w cyklu życia aplikacji, niż application.yml).

### 1. nazwa aplikacji ( identyczna, jak nazwa pliku konfiguracyjnego w git dla danej aplikacji)
	spring:
  	  application:
       name: activity-service
	
### 2. config server uri
	
	spring:
	  application:
       name: activity-service
	  cloud:
	    config:
	      uri: http://config-server:8888
	      
Serwis odnajdzie eurekę na podstawie wpisu w pliku konfiguracyjnym (domyślnie localhost:8761)
	  
## III Endpoint /activity, zwracający obiekt Response
	- nazwa aplikacji
	- losowa wartość z pliku konfiguracyjnego activity-service.yml (github): activities

	@RestController
	public class ActivityController {

	    @Value("${activities}")
	    private String [] activities;
	    @Value("${spring.application.name}")
	    private String serviceName;
	
	    @GetMapping("/activity")
	    public Response decision() {
	        return getResponseWithRandomDecision();
	    }
	
	
	    private Response getResponseWithRandomDecision() {
	        String msg  activities[new Random().nextInt(activities.length)];
	        Response response  new Response(serviceName.toUpperCase(), msg);
	        return response;
	    }
	}

		      
	      
## IV Test

### 1. Uruchom aplikację z 2 profilami

	Profile takie, jak w pliku konfiguracyjnym na githubie: lazy i crazy
	( Konfiguracja profilu w IntelliJ: 2 shift -> edit configurations)
	
### 2. Wpis w logu
	Podczas uruchomienia powinno pojawić się: 
	Fetching config from server at : http://config-server:8888,
	poniżej profil lazy / crazy.
	
### 3. Przeglądarka

	W eurece powinniśmy widzieć 2 instancje ACTIVITY-SERVICE
	
	Żądanie localhost:<port-dla-profilu-z-pliku-konfiguracyjnego-w-git>/<activity> powinno zwrócić komunikat.
	
	


#  V Decision Service 

	Analogicznie jak punkt IV

## I Wygeneruj projekt Spring Boot z zależnościami:

	 Response (zainstalowaliśmy w lokalnym repo .m2 w punkcie pierwszym)
	 spring-boot-starter-web (initalizr: Web)
	 spring-cloud-starter-config (initalizr: Config Client)
	 spring-cloud-starter-netflix-eureka-client (initalizr: Eureka Discovery Client)


## II bootstrap.yml (zamiast application.yml)

	bootstrap.yml (plik konfiguracyjny o tej nazwie będzie użyty wcześniej, w cyklu życia aplikacji, niż application.yml).

### 1. nazwa aplikacji ( identyczna, jak nazwa pliku konfiguracyjnego w git dla danej aplikacji)
	spring:
  	  application:
       name: decision-service
	
### 2. config server uri
	
	spring:
	  application:
       name: decision-service
    # domyślnie localhost:8888
	  cloud:
	    config:
	      uri: http://config-server:8888
	      
    Serwis odnajdzie eurekę na podstawie wpisu w pliku konfiguracyjnym, serwowanym przez config server (domyślnie localhost:8761)	      
## III Endpoint /decision, zwracający obiekt Response
	- nazwa aplikacji
	- losowa wartość z pliku konfiguracyjnego decision-service.yml (github): decisions
	

	@RestController
	public class DecisionController {

    @Value("${decisions}")
    private String [] decisions;
    @Value("${spring.application.name}")
    private String serviceName;

    @GetMapping("/decision")
    public Response decision() {
        return getResponseWithRandomDecision();
    }


    private Response getResponseWithRandomDecision() {
        String msg  decisions[new Random().nextInt(decisions.length)];
        Response response  new Response(serviceName.toUpperCase(), msg);
        return response;
    }
}

		      
## IV Test

### 1. Uruchom aplikację z 2 profilami

	Profile takie, jak w pliku konfiguracyjnym na githubie: polite i rough
	( Konfiguracja profilu w IntelliJ: 2 shift -> edit configurations)
	
### 2. Wpis w logu
	Podczas uruchomienia powinno pojawić się: 
	Fetching config from server at : http://config-server:8888,
	poniżej profil polite / rough.
	
### 3. Przeglądarka

	W eurece powinniśmy widzieć 2 instancje DECISION-SERVICE
	
	Żądanie localhost:<port-dla-profilu-z-pliku-konfiguracyjnego-w-git>/decision powinno zwrócić komunikat.


#  VI Cookie Service 

Kontroler zwracający Response agregujący dane z serwisów.
Użycie Open Feign, Hystrix i Ribbon

## I Wygeneruj projekt Spring Boot z zależnościami:

	 Response (zainstalowaliśmy w lokalnym repo .m2 w punkcie pierwszym)
	 	
	 spring-boot-starter-web (initalizr: Web)
	 spring-cloud-starter-config (initalizr: Config Client)
	 spring-cloud-starter-netflix-eureka-client (initalizr: Eureka Discovery Client)
	 spring-cloud-starter-openfeign (initalizr: OpenFeign)
	 spring-cloud-starter-netflix-ribbon (initalizr: Ribbon)
	 spring-cloud-starter-netflix-hystrix (initalizr: Hystrix)

## II Dodaj @EnableFeignClients do klasy głównej

## III bootstrap.yml (zamiast application.yml)

	bootstrap.yml (plik konfiguracyjny o tej nazwie będzie użyty wcześniej, w cyklu życia aplikacji, niż application.yml).

### 1. nazwa aplikacji ( identyczna, jak nazwa pliku konfiguracyjnego w git dla danej aplikacji)
	spring:
  	  application:
       name: cookie-service
	
### 2. config server uri
	
	spring:
	  application:
       name: cookie-service
	  cloud:
	    config:
	      uri: http://config-server:8888
	      
Serwis odnajdzie eurekę na podstawie wpisu w pliku konfiguracyjnym (domyślnie localhost:8761)

## IV Użycie Open Feign do wygenerowania klienta REST - activity service

### 1. Activity Service
	 - ddefiniujemy interfejs z adnotacją @FeignClient, z atrybutem o idnentyfikatorze aplikacji (spring.appliacation.name), tak jak jest rejestrowana w Eurece
	 - definiujemy metodę interfejsu wskazującą na endpoint i zwrajającą obiekt, do którego ma być zmapowana odpowiedź z endpoint'u:

	@FeignClient(name  "ACTIVITY-SERVICE")
	public interface ActivityClient {
    	@GetMapping("/activity")
    	Response getResponse();
	}
	
### 2. Decision Service - analogicznie jak punkt 1
	@FeignClient(name  "DECISION-SERVICE")
	public interface DecisionClient {
	    @GetMapping("/decision")
	    Response getResponse();
	}


		      
## V Endpoint /fortune, zwracający obiekt Response
	- nazwa aplikacji
	- zagregowana informacja z serwisów decision-service i activity-service, np. "It's a good day to take a nap."
	

	@RestController
	public class CookieController {
	    private final ActivityClient activity;
	    private final DecisionClient decision;
	
	    @Value("${spring.application.name}")
	    private String serviceName;
	
		// IDE może wyświetlać "Could not autowire" - spróbuj uruchomić aplikację mimo to :)
	    public CookieController(ActivityClient activity, DecisionClient decision) {
	        this.activity  activity;
	        this.decision  decision;
	    }
	
	    @GetMapping("/fortune")
	    public Response fortune() {
	        return getResponse();
	    }
	
	    private Response getResponse() {
	
	        return getFortune();
	    }
	
	    private Response getFortune() {
	        return new Response(serviceName.toUpperCase(),
	                decision.getResponse().getMsg() + " " +
	                        activity.getResponse().getMsg());
	    }
	}

		      
## V Test

### 1. Uruchom aplikację z domyślnym profilem

### 2. Wpis w logu
	Podczas uruchomienia powinno pojawić się: 
	Fetching config from server at : http://config-server:8888
	
### 3. Przeglądarka

	W eurece powinniśmy widzieć 1 instancję cookie-service
	
	Żądanie localhost:8080/fortune powinno zwrócić wróżbę.
	
	
## VI Hystrix


### 1. Fallback dla Activity Service

domyślna implementacja

	@Component
	public class ActivityFallback implements ActivityClient {

	    @Override
	    public Response getResponse() {
	        return new Response("","take a rest!");
	    }
	}	
	
dodanie atrybutu w adnotacji interfejsu

	@FeignClient(name  "ACTIVITY-SERVICE", fallback  ActivityFallback.class)
	
### 1. Fallback dla Decision Service - analogicznie

		
Zwróć uwagę na plik konfiguracyjny cookie-service.yml w repozytorium - wpis aktywujący hystrix



