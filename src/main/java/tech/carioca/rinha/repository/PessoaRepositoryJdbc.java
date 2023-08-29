package tech.carioca.rinha.repository;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.carioca.rinha.Pessoa;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.BiFunction;

@Component
public class PessoaRepositoryJdbc {

    private final DatabaseClient databaseClient;

    public  PessoaRepositoryJdbc(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public static final BiFunction<Row, RowMetadata, Pessoa> MAPPING_FUNCTION = (row, rowMetaData) ->
            new Pessoa(
                    row.get("id", UUID.class),
                    row.get("apelido", String.class),
                    row.get("nome", String.class),
                    row.get("nascimento", LocalDate.class),
                    row.get("stack", String[].class)
            );

    public static final BiFunction<Row, RowMetadata, Long> COUNT_MAPPING_FUNCTION = (row, rowMetaData) -> row.get(0, Long.class);

    public Mono<UUID> save(Pessoa p) {
        var uuid = UUID.randomUUID();

        var foo = this.databaseClient

                .sql("INSERT INTO  pessoa (id, apelido, nome, nascimento, stack) VALUES (:id, :apelido, :nome, :nascimento, :stack)")
                .bind("id", uuid)
                .bind("apelido", p.apelido())
                .bind("nome", p.nome())
                .bind("nascimento", p.nascimento())
                ;
        if (p.stack() == null) {
            return foo.bindNull("stack", String[].class)
                .fetch()
                .rowsUpdated()
                .map(r -> uuid);
        } else {
            return foo.bind("stack", p.stack())
                    .fetch()
                    .rowsUpdated()
                    .map(r -> uuid);
        }
    }

    public Mono<Pessoa> findById(UUID id) {
        return this.databaseClient
                .sql("SELECT * FROM pessoa WHERE id=:id")
                .bind("id", id)
                .map(MAPPING_FUNCTION)
                .one();
    }

    public Flux<Pessoa> findFirst50ByNomeContainingIgnoreCaseOrApelidoContainingIgnoreCaseOrStackContainingIgnoreCase(String term) {
        return this.databaseClient
                .sql("SELECT * FROM pessoa WHERE upper(apelido)=upper(:term) or upper(nome)=upper(:term) or :term=ANY(stack) limit 50")
                .bind("term", term)
                .map(MAPPING_FUNCTION)
                .all();
    }
    public Mono<Long> count() {
        return this.databaseClient
                .sql("SELECT count(0) FROM pessoa")
                .map(COUNT_MAPPING_FUNCTION)
                .first();
    }
}
