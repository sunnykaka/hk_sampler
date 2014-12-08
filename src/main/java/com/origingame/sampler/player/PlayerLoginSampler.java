package com.origingame.sampler.player;

import com.origingame.client.main.ClientSession;
import com.origingame.message.RegisterProtos;
import com.origingame.sampler.AbstractSampler;
import org.apache.jmeter.samplers.SampleResult;


/**
 * User: Liub
 * Date: 2014/12/8
 */
public class PlayerLoginSampler extends AbstractSampler {

    @Override
    public void doRunTestWithSession(SampleResult sr, ClientSession clientSession) {

        AbstractSampler.PlayerUsernamePassword playerUsernamePassword = register(clientSession);

        try {
            sr.sampleStart();

            login(clientSession, playerUsernamePassword.getUsername(), playerUsernamePassword.getPassword());

        } finally {

            sr.sampleEnd();
        }


    }
}
