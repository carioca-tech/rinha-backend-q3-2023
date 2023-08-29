package tech.carioca.rinha;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.UUID;

public record Pessoa(
                @Id UUID id,
                String apelido,
                String nome,
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
                LocalDate nascimento,
                String[] stack
    ) {

    public Pessoa withId(UUID id) {
        return new Pessoa(id, apelido(), nome(), nascimento(), stack());
    }
}
