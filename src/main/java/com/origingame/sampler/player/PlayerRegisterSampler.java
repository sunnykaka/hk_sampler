package com.origingame.sampler.player;

import com.origingame.client.main.ClientSession;
import com.origingame.sampler.AbstractSampler;
import org.apache.jmeter.samplers.SampleResult;


/**
 * User: Liub
 * Date: 2014/12/8
 */
public class PlayerRegisterSampler extends AbstractSampler {

    @Override
    public void doRunTestWithSession(SampleResult sr, ClientSession clientSession) {

        try {
            sr.sampleStart();

            register(clientSession);
        } finally {
            sr.sampleEnd();
        }

    }

}
