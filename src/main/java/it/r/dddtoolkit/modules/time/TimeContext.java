package it.r.dddtoolkit.modules.time;

import it.r.dddtoolkit.core.Context;

import java.time.Instant;

/**
 * Created by rascio on 04/02/18.
 */
public interface TimeContext extends Context {

    Instant getTime();

}
