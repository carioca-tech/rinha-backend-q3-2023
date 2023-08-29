package tech.carioca.rinha.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.carioca.rinha.Pessoa;
import tech.carioca.rinha.repository.PessoaRepositoryJdbc;

import java.util.Arrays;
import java.util.UUID;

@Configuration(proxyBeanMethods = false)
public class PessoaHandler {

    private final PessoaRepositoryJdbc pessoaRepository;

    public PessoaHandler(PessoaRepositoryJdbc pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }

    public Mono<ServerResponse> post(ServerRequest request) {
        return request.bodyToMono(Pessoa.class)
                .map( body -> {
                    if (!StringUtils.hasText( body.apelido())) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
                    if (body.apelido().length() > 32 ) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
                    if (!StringUtils.hasText( body.nome())) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
                    if (body.nome().length() > 100 ) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);

                    if (body.stack() !=null) {
                        var invalidElement = Arrays.stream(body.stack())
                                .filter( it -> it == null || it.length() > 32)
                                .findAny();
                        if (invalidElement.isPresent()) throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
                    }
                    return body;
                })
                .flatMap(pessoaRepository::save)
                .flatMap(id -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.LOCATION, "/pessoas/" + id)
                        .build())
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        final var id = UUID.fromString(request.pathVariable("id"));
        return pessoaRepository.findById(id)
                .flatMap(person -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(person))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
    public Mono<ServerResponse> query(ServerRequest request) {
        var term = request.queryParam("t");
        if (term.isEmpty()) {
            return badRequest();
        }
        var pessoas = term
                .map(pessoaRepository::findFirst50ByNomeContainingIgnoreCaseOrApelidoContainingIgnoreCaseOrStackContainingIgnoreCase)
                .orElse(Flux.empty());
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(pessoas, Pessoa.class);
    }

    public Mono<ServerResponse> badRequest() {
        return ServerResponse.badRequest().build();
    }

    public Mono<ServerResponse> count(ServerRequest request) {


        return pessoaRepository.count()
                .map(c -> String.valueOf(c))
           .flatMap(count -> ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue(count));
    }

}
