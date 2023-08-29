package tech.carioca.rinha.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import tech.carioca.rinha.Pessoa;

import java.util.UUID;

public interface PessoaRepository extends ReactiveCrudRepository<Pessoa, UUID> {

    @Query("SELECT pessoa.id, pessoa.apelido, pessoa.nome, pessoa.nascimento, pessoa.stack FROM pessoa WHERE UPPER(pessoa.nome) LIKE UPPER($1) OR (UPPER(pessoa.apelido) LIKE UPPER($1)) LIMIT 50")
    Flux<Pessoa> findFirst50ByNomeContainingIgnoreCaseOrApelidoContainingIgnoreCaseOrStackContainingIgnoreCase(String query);
}
