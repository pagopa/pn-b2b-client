package it.pagopa.interop.authorization.service;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* DEV. NOTE 14/05/2025: nel metodo di aggiunta/sostituzione di un admin client al momento è
 * previsto il parametro di tipo InlineObject3, che per il momento consta del solo attributo adminId.
 * Poiché oggetti simili tendono a essere sostituiti frequentemente da alternative uguali in
 * tutto o in parte (chiamate InlineObject4, InlineObject5...), si usa questa classe come
 * interfaccia per minimizzare eventuali refactor qualora suddetto tipo cambiasse nuovamente. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientAdminConfig {
    private UUID adminId;
}