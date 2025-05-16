package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.IncludeTags;

@IncludeTags(ClientAdminTest.TAG)
public class ClientAdminTest extends AbstractClientAdminTest {
    public static final String TAG = "client_admin";
}
