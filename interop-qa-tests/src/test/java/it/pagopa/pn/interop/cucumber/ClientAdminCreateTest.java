package it.pagopa.pn.interop.cucumber;

import org.junit.platform.suite.api.IncludeTags;

@IncludeTags(ClientAdminCreateTest.TAG)
public class ClientAdminCreateTest extends AbstractClientAdminTest {
    public static final String TAG = "client_admin_create";
}
