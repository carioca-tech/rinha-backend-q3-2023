package tech.carioca.rinha;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import tech.carioca.rinha.web.PessoaHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@SpringBootApplication
@EnableR2dbcRepositories
public class RinhaApp {

    public static void main(String[] args) {

        var app = SpringApplication.run(RinhaApp.class, args);
        var connectionFactory = app.getBean(ConnectionFactory.class);

        Mono.from(connectionFactory.create())
                .flatMapMany(connection -> connection
                        .createStatement("CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\"; drop TABLE if exists pessoa; CREATE TABLE pessoa( id UUID NOT NULL, apelido VARCHAR(40) NOT NULL, nome VARCHAR(140), nascimento date NOT NULL, stack varchar[], primary key(id)); CREATE INDEX if not exists idx_01 ON pessoa (upper(apelido), upper(nome), stack)")
                        .execute())
                .subscribe();
    }

    @Bean
    public RouterFunction<ServerResponse> route(PessoaHandler personHandler) {
        return RouterFunctions
                .route(POST("/pessoas").and(accept(MediaType.APPLICATION_JSON)), personHandler::post)
                .andRoute(GET("/pessoas/{id}"), personHandler::findById)
                .andRoute(GET("/pessoas"), personHandler::query)
                .andRoute(GET("/contagem-pessoas"), personHandler::count);
    }

}
