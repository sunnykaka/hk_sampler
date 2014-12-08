package com.origingame.sampler.player;

import com.origingame.client.main.ClientSession;
import com.origingame.sampler.AbstractSampler;
import org.apache.jmeter.samplers.SampleResult;


/**
 * User: Liub
 * Date: 2014/12/8
 */
public class HandShakeSampler extends AbstractSampler {

    @Override
    public void doRunTest(SampleResult sr) {
        ClientSession clientSession;
        try {
            sr.sampleStart();

            clientSession = initSession();
        } finally {
            sr.sampleEnd();
        }
        if(clientSession != null) {
            clientSession.destroy();
        }

    }
}
