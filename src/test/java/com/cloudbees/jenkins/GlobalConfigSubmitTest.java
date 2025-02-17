package com.cloudbees.jenkins;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jenkinsci.plugins.github.GitHubPlugin;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Test Class for {@link GitHubPushTrigger}.
 *
 * @author Seiji Sogabe
 */
@Ignore("Have troubles with memory consumption")
public class GlobalConfigSubmitTest {

    public static final String OVERRIDE_HOOK_URL_CHECKBOX = "_.isOverrideHookUrl";
    public static final String HOOK_URL_INPUT = "_.hookUrl";

    private static final String WEBHOOK_URL = "http://jenkinsci.example.com/jenkins/github-webhook/";

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void shouldSetHookUrl() throws Exception {
        HtmlForm form = globalConfig();

        form.getInputByName(OVERRIDE_HOOK_URL_CHECKBOX).setChecked(true);
        form.getInputByName(HOOK_URL_INPUT).setValueAttribute(WEBHOOK_URL);
        jenkins.submit(form);

        assertThat(GitHubPlugin.configuration().getHookUrl(), equalTo(new URL(WEBHOOK_URL)));
    }

    @Test
    public void shouldNotSetHookUrl() throws Exception {
        GitHubPlugin.configuration().setHookUrl(WEBHOOK_URL);

        HtmlForm form = globalConfig();

        form.getInputByName(OVERRIDE_HOOK_URL_CHECKBOX).setChecked(false);
        form.getInputByName(HOOK_URL_INPUT).setValueAttribute("http://foo");
        jenkins.submit(form);

        assertThat(GitHubPlugin.configuration().getHookUrl(), equalTo(new URL(WEBHOOK_URL)));
    }

    @Test
    public void shouldNotOverrideAPreviousHookUrlIfNotChecked() throws Exception {
        GitHubPlugin.configuration().setHookUrl(WEBHOOK_URL);

        HtmlForm form = globalConfig();

        form.getInputByName(OVERRIDE_HOOK_URL_CHECKBOX).setChecked(false);
        form.getInputByName(HOOK_URL_INPUT).setValueAttribute("");
        jenkins.submit(form);

        assertThat(GitHubPlugin.configuration().getHookUrl(), equalTo(new URL(WEBHOOK_URL)));
    }

    public HtmlForm globalConfig() throws IOException, SAXException {
        JenkinsRule.WebClient client = configureWebClient();
        HtmlPage p = client.goTo("configure");
        return p.getFormByName("config");
    }

    private JenkinsRule.WebClient configureWebClient() {
        JenkinsRule.WebClient client = jenkins.createWebClient();
        client.setJavaScriptEnabled(true);
        return client;
    }
}
