package com.origingame.sampler;

import com.origingame.client.main.ClientSession;
import com.origingame.client.protocol.ClientResponseWrapper;
import com.origingame.message.LoginProtos;
import com.origingame.message.RegisterProtos;
import com.origingame.model.PlayerModelProtos;
import com.origingame.server.main.World;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

/**
 * User: Liub
 * Date: 2014/12/8
 */
public abstract class AbstractSampler extends AbstractJavaSamplerClient {

    public static String host = "127.0.0.1";

    public static String port = "8080";

    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
        try {
            World.getInstance().init();
        } catch (Exception e) {
            getLogger().error("", e);
            throw new RuntimeException(e);
        }
        initParams(context);
        doSetupTest(context);
    }

    protected void doSetupTest(JavaSamplerContext context) {

    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument("host", host);
        params.addArgument("port", port);
        return params;
    }

    private void initParams(JavaSamplerContext jc) {

        host = jc.getParameter("host");
        port = jc.getParameter("port");
    }


    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult sr = new SampleResult();
        boolean flag = true;
        try {

            doRunTest(sr);

            sr.setResponseCodeOK();
            sr.setResponseMessageOK();

        } catch (Exception e) {
            flag = false;
            getLogger().error("",e);
        }
        sr.setSuccessful(flag);
        return sr;
    }

    protected void doRunTest(SampleResult sr) {
        ClientSession clientSession = initSession();
        try {
            doRunTestWithSession(sr, clientSession);
        } finally {
            clientSession.destroy();
        }
    }

    protected void doRunTestWithSession(SampleResult sr, ClientSession clientSession) {

    }



    protected ClientSession initSession() {
        ClientSession clientSession = new ClientSession(host, Integer.parseInt(port));
        clientSession.openConnection();
        clientSession.shakeHand();
        return clientSession;
    }


    protected PlayerUsernamePassword register(ClientSession clientSession) {
        RegisterProtos.RegisterReq.Builder registerReq = RegisterProtos.RegisterReq.newBuilder();
        String username = RandomStringUtils.randomAlphabetic(6);
        String password = RandomStringUtils.randomAlphabetic(8);
        registerReq.setUsername(username);
        registerReq.setPassword(password);
        ClientResponseWrapper clientResponseWrapper = clientSession.sendMessage(registerReq.build());
        RegisterProtos.RegisterResp registerResp = (RegisterProtos.RegisterResp) clientResponseWrapper.getMessage();
        assertThat(registerResp.getPlayerId(), greaterThan(0));
        assertThat(registerResp.getUsername(), is(username));
        assertThat(registerResp.getPassword(), isEmptyOrNullString());

        return new PlayerUsernamePassword(username, password);
    }

    protected PlayerModelProtos.PlayerModel login(ClientSession clientSession, String username, String password) {
        ClientResponseWrapper clientResponseWrapper = clientSession.login(username, password);

        LoginProtos.LoginResp loginResp = (LoginProtos.LoginResp)clientResponseWrapper.getMessage();
        PlayerModelProtos.PlayerModel player = loginResp.getPlayer();
        assertThat(player, notNullValue());
        assertThat(player.getProperty().getUsername(), is(username));
        assertThat(player.getProperty().getPassword(), is(password));
        assertThat(player.getProperty().getSessionId(), is(clientSession.getSessionId()));
        assertThat(player.getProperty().getOuterId(), notNullValue());

        return player;
    }

    protected static class PlayerUsernamePassword {
        private String username;
        private String password;

        PlayerUsernamePassword(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
