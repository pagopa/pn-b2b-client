package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.IncludeTags;

@IncludeTags(ClientAdminDeleteTest.TAG)
public class ClientAdminDeleteTest extends AbstractClientAdminTest {
    public static final String TAG = "client_admin_delete";
}
