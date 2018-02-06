package it.r.dddtoolkit.modules.es.eventstore;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Created by rascio on 03/02/18.
 */
@Value
@AllArgsConstructor(staticName = "of")
public class Version {

    public static Version UNINITIALIZED = Version.of(-1L, -1L);

    private Long number;
    private Long timestamp;

    public Version next() {
        return Version.of(number + 1, System.currentTimeMillis());
    }
}
